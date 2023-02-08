package nl.tudelft.sem.template.order.domain;

import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.domain.item.Item;
import nl.tudelft.sem.template.order.domain.order.*;
import nl.tudelft.sem.template.order.domain.product.BasicProductInfo;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.domain.product.ProductDoesNotExistException;
import nl.tudelft.sem.template.order.domain.product.ProductService;
import nl.tudelft.sem.template.order.framework.ProductMicroserviceAdapter;
import nl.tudelft.sem.template.order.model.OrderResponseModel;
import nl.tudelft.sem.template.order.model.RemoveProductsFromOrderRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ModifyOrderTests {

    OrderService mockOrderService;
    ProductService mockProductService;
    ProductMicroserviceAdapter mockProductMicroserviceAdapter;
    AddToOrderService addToOrderService;
    OrderHelperService orderHelperService;
    ModifyOrderService modifyOrderService;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Mock
    private transient OrderService orderService;
    @Mock
    private transient ProductService productService;
    private transient OrderController orderController;

    @BeforeEach
    public void setup() {
        mockOrderService = mock(OrderService.class);
        mockProductService = mock(ProductService.class);
        mockProductMicroserviceAdapter = mock(ProductMicroserviceAdapter.class);
        orderHelperService = new OrderHelperService(mockProductMicroserviceAdapter);
        addToOrderService = new AddToOrderService(mockOrderService, mockProductService,
                mockProductMicroserviceAdapter, orderHelperService);
        modifyOrderService = new ModifyOrderService(mockOrderService, mockProductService,
                mockProductMicroserviceAdapter, addToOrderService, orderHelperService);
        orderController = new OrderController(mockAuthenticationManager, null, mockOrderService,
                mockProductService, modifyOrderService, null, addToOrderService);
    }


    @Test
    public void testControllerRemoveProductsUnauthorised() throws ProductDoesNotExistException, OrderDoesNotExistException {
        //A user tries to remove someone else's product
        //Expect: error is thrown
        Order mockOrder = new Order();
        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        mockOrder.getData().setCustomerId("Franklin");
        mockOrder.setOrderId(2);
        Product product = new Product(
                "Margherita",
                List.of("Pepperoni"),
                3.0,
                List.of(1, 2, 3),
                mockOrder,
                false);
        RemoveProductsFromOrderRequestModel requestModel = new RemoveProductsFromOrderRequestModel();
        requestModel.setProducts(List.of(1L));
        OrderUtils.addProduct(product, mockOrder);
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockAuthenticationManager.getEmail()).thenReturn("Bill");
        when(mockProductService.getProduct(1L)).thenReturn(product);
        when(orderService.orderBelongsToUser(eq(2), eq("Bill"))).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> orderController.removeProductsFromOrder(requestModel));
    }

    @Test
    public void testAddFirstToppingSuccessful() throws Exception {
        //Add a topping to a product that didn't have toppings.
        //Expect: the topping gets added, price and allergens change, nothing else changes
        Item toppingItem = new Item("Pepperoni", List.of(3), false, 2.0);
        Order mockOrder = new Order();
        Product product = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                mockOrder,
                false);

        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        OrderUtils.addProduct(product, mockOrder);
        when(mockProductService.getProduct(1L)).thenReturn(product);
        when(mockProductMicroserviceAdapter.verifyToppings(any(), any())).thenReturn(List.of(toppingItem));

        addToOrderService.addToppingsToProduct(1L, List.of("Pepperoni"), "");
        assertEquals("Margherita", product.getName());
        assertThat(product.getToppingNames(), containsInAnyOrder("Pepperoni"));
        assertEquals(3.0, product.getPrice());
        assertThat(product.getAllergens(), containsInAnyOrder(1, 2, 3));
        assertSame(mockOrder, product.getOrder());
    }

    @Test
    public void testRemoveLastToppingSuccessful() throws Exception {
        //Product contains only one topping, which we remove
        //Expect: topping is gone, product still has the right price and allergens
        Item productItem = new Item("Margherita", List.of(1, 2), false, 1.0);
        Order mockOrder = new Order();
        Product product = new Product(
                "Margherita",
                List.of("Pepperoni"),
                3.0,
                List.of(1, 2, 3),
                mockOrder,
                false);
        when(mockProductService.getProduct(1L)).thenReturn(product);
        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        when(mockProductMicroserviceAdapter.verifyProducts(any(), any())).thenReturn(List.of(productItem));

        modifyOrderService.removeToppingFromProduct(1L, "Pepperoni", "");
        assertTrue(product.getToppingNames().isEmpty());
        assertEquals(1.0, product.getPrice());
        assertThat(product.getAllergens(), containsInAnyOrder(1, 2));

    }

    @Test
    public void testRemoveProductSuccessful() throws Exception {
        //Remove the only product from an order
        //Expect: the order now doesn't contain the product and its price is 0
        Order mockOrder = new Order();
        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        Product product = new Product(
                "Margherita",
                List.of("Pepperoni"),
                3.0,
                List.of(1, 2, 3),
                mockOrder,
                false);
        OrderUtils.addProduct(product, mockOrder);
        when(mockProductService.getProduct(1L)).thenReturn(product);

        modifyOrderService.removeProductFromOrder(1L);
        assertSame(0, mockOrder.getProducts().size());
        assertEquals(0.0, mockOrder.getData().getPrice());
    }

    @Test
    public void testAddToppingChangesOrder() throws Exception {
        //A topping gets added to a product
        //Expect: the price of the topping is added to the order price
        Item toppingItem = new Item("Pepperoni", List.of(3), false, 2.0);
        Order mockOrder = new Order();
        Product product = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                mockOrder,
                false);
        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        OrderUtils.addProduct(product, mockOrder);
        when(mockProductService.getProduct(1L)).thenReturn(product);
        when(mockProductMicroserviceAdapter.verifyToppings(any(), any())).thenReturn(List.of(toppingItem));

        addToOrderService.addToppingsToProduct(1L, List.of("Pepperoni"), "");
        assertEquals(3.0, mockOrder.getData().getPrice());
    }

    @Test
    public void testRemoveToppingChangesOrderOneTopping() throws Exception {
        //A topping gets removed from a product, with one topping
        //Expect: the price of the topping is subtracted from the order price
        Item productItem = new Item("Margherita", List.of(1, 2), false, 1.0);
        Order mockOrder = new Order();
        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        Product product = new Product(
                "Margherita",
                List.of("Pepperoni"),
                3.0,
                List.of(1, 2, 3),
                mockOrder,
                false);

        OrderUtils.addProduct(product, mockOrder);
        when(mockProductService.getProduct(1L)).thenReturn(product);
        when(mockProductMicroserviceAdapter.verifyProducts(any(), any())).thenReturn(List.of(productItem));
        modifyOrderService.removeToppingFromProduct(1L, "Pepperoni", "");

        assertEquals(mockOrder.getProducts().get(0).getToppingNames().size(), 0);
        assertEquals(mockOrder.getData().getPrice(), 1.0);
    }

    @Test
    public void testRemoveToppingChangesOrderTwoToppings() throws Exception {
        //A topping gets removed from a product, with two toppings
        //Expect: the price of the removed topping is subtracted from the order price
        Item stayingToppingItem = new Item("Salami", List.of(), false, 2.0);
        Item productItem = new Item("Margherita", List.of(1, 2), false, 1.0);
        Order mockOrder = new Order();
        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        Product product = new Product(
                "Margherita",
                List.of("Pepperoni", "Salami"),
                5.0,
                List.of(1, 2, 3),
                mockOrder,
                false);
        OrderUtils.addProducts(List.of(product), mockOrder);
        mockOrder.getData().setPrice(5.0);
        when(mockProductService.getProduct(1L)).thenReturn(product);
        when(mockProductMicroserviceAdapter.verifyProducts(any(), any())).thenReturn(List.of(productItem));
        when(mockProductMicroserviceAdapter.verifyToppings(any(), any())).thenReturn(List.of(stayingToppingItem));
        modifyOrderService.removeToppingFromProduct(1L, "Pepperoni", "");

        assertEquals(3.0, mockOrder.getData().getPrice());
    }

    @Test
    public void testRemoveTwoProductsSameOrder() throws Exception {
        //Two products get removed from the same order
        //Expect: the response contains the order only once
        Order mockOrder = new Order();
        mockOrder.setStrategy(new BasicPricingStrategy());
        mockOrder.getData().setStatus(Status.UNPAID);
        Product firstProduct = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                mockOrder,
                false);
        Product secondProduct = new Product(
                "Prosciutto",
                List.of(),
                1.0,
                List.of(1, 2),
                mockOrder,
                false);
        OrderUtils.addProduct(firstProduct, mockOrder);
        OrderUtils.addProduct(secondProduct, mockOrder);
        when(mockProductService.getProduct(1L)).thenReturn(firstProduct);
        when(mockProductService.getProduct(2L)).thenReturn(secondProduct);
        List<OrderResponseModel> response = modifyOrderService.removeProductsFromOrder(List.of(1L, 2L));

        assertSame(1, response.size());
    }

    @Test
    public void checkStatusMethodPaid(){
        ProductMicroserviceAdapter mockProductMicroserviceAdapter = mock(ProductMicroserviceAdapter.class);
        OrderHelperService orderHelperService = new OrderHelperService(mockProductMicroserviceAdapter);
        Order o = mock(Order.class);
        OrderData data = mock(OrderData.class);
        when(data.getStatus()).thenReturn(Status.PAID);
        when(o.getData()).thenReturn(data);

        Exception e = assertThrows(OrderIsAlreadyPaidException.class, () ->
                orderHelperService.checkStatus(o));
    }

    @Test
    public void checkStatusMethodCancelled(){
        ProductMicroserviceAdapter mockProductMicroserviceAdapter = mock(ProductMicroserviceAdapter.class);
        OrderHelperService orderHelperService = new OrderHelperService(mockProductMicroserviceAdapter);
        Order o = mock(Order.class);
        OrderData data = mock(OrderData.class);
        when(data.getStatus()).thenReturn(Status.CANCELLED);
        when(o.getData()).thenReturn(data);

        Exception e = assertThrows(OrderCancelledException.class, () ->
                orderHelperService.checkStatus(o));
    }

    @Test
    public void setAddToOrderServiceAddProductThrowsAlreadyPaid() throws OrderDoesNotExistException {
        AddToOrderService addToOrderService = new AddToOrderService(mockOrderService, mockProductService,
                mockProductMicroserviceAdapter, orderHelperService);
        int orderId = 1;
        Order o = mock(Order.class);
        when(o.getOrderId()).thenReturn(1);
        OrderData data = mock(OrderData.class);
        when(data.getStatus()).thenReturn(Status.PAID);
        when(o.getData()).thenReturn(data);
        List<BasicProductInfo> list = new ArrayList<>();

        when(mockOrderService.getOrder(orderId)).thenReturn(o);
        Exception e = assertThrows(OrderIsAlreadyPaidException.class, () ->
                addToOrderService.addProductsToOrder(orderId, list, ""));
    }

    @Test
    public void setAddToOrderServiceAddProductThrowsAlreadyCancelled() throws OrderDoesNotExistException {
        AddToOrderService addToOrderService = new AddToOrderService(mockOrderService, mockProductService,
                mockProductMicroserviceAdapter, orderHelperService);
        int orderId = 1;
        Order o = mock(Order.class);
        when(o.getOrderId()).thenReturn(1);
        OrderData data = mock(OrderData.class);
        when(data.getStatus()).thenReturn(Status.CANCELLED);
        when(o.getData()).thenReturn(data);
        List<BasicProductInfo> list = new ArrayList<>();

        when(mockOrderService.getOrder(orderId)).thenReturn(o);
        Exception e = assertThrows(OrderCancelledException.class, () ->
                addToOrderService.addProductsToOrder(orderId, list, ""));
    }

    @Test
    public void setAddToOrderServiceAddToppingThrowsAlreadyPaid() throws OrderDoesNotExistException,
            ProductDoesNotExistException {
        AddToOrderService addToOrderService = new AddToOrderService(mockOrderService, mockProductService,
                mockProductMicroserviceAdapter, orderHelperService);

        Product p = mock(Product.class);
        when(mockProductService.getProduct(anyLong())).thenReturn(p);

        long orderId = 1;
        Order o = mock(Order.class);
        when(o.getOrderId()).thenReturn(1);
        OrderData data = mock(OrderData.class);
        when(data.getStatus()).thenReturn(Status.PAID);
        when(o.getData()).thenReturn(data);
        List<String> list = new ArrayList<>();

        when(p.getOrder()).thenReturn(o);

        Exception e = assertThrows(OrderIsAlreadyPaidException.class, () ->
                addToOrderService.addToppingsToProduct(orderId, list, ""));
    }

    @Test
    public void setAddToOrderServiceAddToppingThrowsAlreadyCancelled() throws OrderDoesNotExistException,
            ProductDoesNotExistException {
        AddToOrderService addToOrderService = new AddToOrderService(mockOrderService, mockProductService,
                mockProductMicroserviceAdapter, orderHelperService);

        Product p = mock(Product.class);
        when(mockProductService.getProduct(anyLong())).thenReturn(p);

        long orderId = 1;
        Order o = mock(Order.class);
        when(o.getOrderId()).thenReturn(1);
        OrderData data = mock(OrderData.class);
        when(data.getStatus()).thenReturn(Status.CANCELLED);
        when(o.getData()).thenReturn(data);
        List<String> list = new ArrayList<>();

        when(p.getOrder()).thenReturn(o);

        Exception e = assertThrows(OrderCancelledException.class, () ->
                addToOrderService.addToppingsToProduct(orderId, list, ""));
    }
}
