package nl.tudelft.sem.template.order.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.domain.item.Item;
import nl.tudelft.sem.template.order.domain.order.*;
import nl.tudelft.sem.template.order.domain.product.BasicProductInfo;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.domain.product.ProductService;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;
import nl.tudelft.sem.template.order.domain.store.StoreService;
import nl.tudelft.sem.template.order.framework.ProductMicroserviceAdapter;
import nl.tudelft.sem.template.order.framework.StoreIdAdapter;
import nl.tudelft.sem.template.order.model.SendOrderModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * The type Order controller test.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class SendOrderTests {
    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Mock
    private transient StoreRepository storeRepository;

    @Mock
    private transient StoreIdAdapter mockStoreIdAdapter;

    private transient StoreService storeService;

    @Mock
    private transient ProductService productService;

    private transient ModifyOrderService modifyOrderService;

    private transient AddToOrderService addToOrderService;

    private transient OrderHelperService orderHelperService;

    @Mock
    private transient OrderService orderService;

    @Mock
    private transient ProductMicroserviceAdapter productAdapter;

    private transient OrderController orderController;

    /**
     * Setup.
     */
    @BeforeEach
    void setup() {
        storeService = new StoreService(storeRepository);
        orderHelperService = new OrderHelperService(productAdapter);
        addToOrderService = new AddToOrderService(orderService, productService, productAdapter, orderHelperService);
        modifyOrderService = new ModifyOrderService(orderService, productService, productAdapter, addToOrderService,
                orderHelperService);

        orderController = new OrderController(mockAuthenticationManager, storeService, orderService,
                productService, modifyOrderService, mockStoreIdAdapter, addToOrderService);
    }

    /**
     * Test sending (e.g., creating) an order with no products.
     * I.e., only IDs of the customer and the store are provided.
     *
     * @throws JsonProcessingException the json processing exception
     */
    @Test
    public void testSendEmptyOrder() throws JsonProcessingException {
        String customerEmail = "testEmail";
        int storeId = 55;

        Order order = new Order(customerEmail, storeId);

        // What we should return for the methods we will call
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockAuthenticationManager.getEmail()).thenReturn(customerEmail);
        when(storeService.checkId(anyInt())).thenReturn(true);
        when(orderService.sendOrder(customerEmail, storeId)).thenReturn(order);

        Order result = orderController.sendOrder(new SendOrderModel(storeId, null), null).getBody();

        // Make sure the operation was successful
        assert result != null;

        // Check the resulting Order object has the expected values
        assertThat(result.getData().getPrice()).isEqualTo(0);
        assertThat(result.getProducts().size()).isEqualTo(0);

        // Check the methods have been called the expected amount of times
        Mockito.verify(productAdapter, never()).verifyProducts(anyList(), any());
        Mockito.verify(orderService, times(1)).sendOrder(customerEmail, storeId);
        Mockito.verify(productAdapter, never()).verifyToppings(anyList(), any());
    }

    /**
     * Test sending more complex orders. We send orders with products:
     * 1. One product to which the user is allergic to, with one topping
     * 2. One product to which the user is not allergic to , with two toppings (one of which the user is allergic to)
     * 3. One product to which the user is allergic to, with two toppings
     *
     * @throws JsonProcessingException the json processing exception
     */
    @Test
    public void testSendOrder() throws JsonProcessingException, OrderDoesNotExistException {
        String customerEmail = "testEmail";
        int storeId = 55;
        int orderId = 1;
        String testHeader = "testHeader";

        Order order = new Order(customerEmail, storeId);
        order.setOrderId(orderId);

        // Basic info of the products only (no toppings' info), containing their name, starting price, if allergic, etc.
        Item product1 = new Item("product1", new ArrayList<>(), true, 3.0d);
        Item product2 = new Item("product2", new ArrayList<>(List.of(1)), false, 1.0d);
        Item product3 = new Item("product3", new ArrayList<>(List.of(4)), false, 0.0d);
        List<Item> productItems = List.of(product1, product2, product3);

        // Toppings info we will use in this test
        Item topping1 = new Item("topping1", new ArrayList<>(List.of(2)), false, 1.5d);
        Item topping2 = new Item("topping2", new ArrayList<>(), false, 4d);
        Item topping3 = new Item("topping3", new ArrayList<>(List.of(3)), true, 6d);

        // Relation between products and toppings
        List<BasicProductInfo> products = new ArrayList<>();
        products.add(new BasicProductInfo("product1", List.of("topping1", "topping2")));
        products.add(new BasicProductInfo("product2", List.of("topping3", "topping2")));
        products.add(new BasicProductInfo("product3", List.of("topping2")));

        // Expected products we will obtain. Because the id of the product is only added after saving in the repo, to replicate
        // this functionality we need to create 2 instances per product, one without id and one with id.
        Product product1NoId = new Product("product1", List.of("topping1", "topping2"), 8.5, List.of(2), order, true);
        Product product1Id = new Product("product1", List.of("topping1", "topping2"), 8.5, List.of(2), order, true);
        product1Id.setId(0L);

        Product product2NoId = new Product("product2", List.of("topping3", "topping2"), 11.0, List.of(1, 3), order, true);
        Product product2Id = new Product("product2", List.of("topping3", "topping2"), 11.0, List.of(1, 3), order, true);
        product2Id.setId(1L);

        Product product3NoId = new Product("product3", List.of("topping2"), 4.0, List.of(4), order, false);
        Product product3Id = new Product("product3", List.of("topping2"), 4.0, List.of(4), order, false);
        product3Id.setId(2L);

        // Headers we will pass to the methods in the test
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", testHeader);

        // What we should return for the methods we will call
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockAuthenticationManager.getEmail()).thenReturn(customerEmail);
        when(storeService.checkId(anyInt())).thenReturn(true);
        when(orderService.sendOrder(customerEmail, storeId)).thenReturn(order);
        when(orderService.getOrder(orderId)).thenReturn(order);
        when(productService.saveProducts(List.of(product1NoId, product2NoId, product3NoId))).thenReturn(List.of(product1Id, product2Id, product3Id));
        when(productAdapter.verifyProducts(List.of("product1", "product2", "product3"), headers)).thenReturn(productItems);
        when(productAdapter.verifyToppings(List.of("topping1", "topping2"), headers)).thenReturn(List.of(topping1, topping2));
        when(productAdapter.verifyToppings(List.of("topping3", "topping2"), headers)).thenReturn(List.of(topping3, topping2));
        when(productAdapter.verifyToppings(List.of("topping2"), headers)).thenReturn(List.of(topping2));

        Order result = orderController.sendOrder(new SendOrderModel(storeId, products), testHeader).getBody();

        // Make sure the operation was successful
        assert result != null;

        List<Product> expectedProducts = List.of(product1Id, product2Id, product3Id);

        // Check the resulting Order object has the expected values
        assertThat(result.getData().getPrice()).isEqualTo(23.5);
        assertThat(result.getProducts()).isEqualTo(expectedProducts);
        assertThat(result.getData().getStatus()).isEqualTo(Status.UNPAID);

        // Check the methods have been called the expected amount of times
        Mockito.verify(productAdapter, times(1)).verifyProducts(anyList(), any());
        Mockito.verify(orderService, times(1)).sendOrder(customerEmail, storeId);
        Mockito.verify(productAdapter, times(productItems.size())).verifyToppings(anyList(), any());
        Mockito.verify(productService, times(1)).saveProducts(expectedProducts);
        Mockito.verify(orderService, times(1)).saveOrder(any());
    }

    /**
     * Test send order with duplicates products. All the duplicate objects should be added to
     * the order (the user is not forced to only choose different orders, they can orders as many
     * times they want the same product).
     */
    @Test
    public void testSendOrderWithDuplicates() throws JsonProcessingException, OrderDoesNotExistException {
        String customerEmail = "testEmail";
        int storeId = 55;
        int orderId = 1;
        String testHeader = "testHeader";

        Order order = new Order(customerEmail, storeId);
        order.setOrderId(orderId);

        // Basic info of the products only (no toppings' info), containing their name, starting price, if allergic, etc.
        Item product1 = new Item("product1", new ArrayList<>(), true, 3.0d);
        List<Item> productItems = List.of(product1, product1);

        // Toppings info we will use in this test
        Item topping1 = new Item("topping1", new ArrayList<>(List.of(2)), false, 1.5d);
        Item topping2 = new Item("topping2", new ArrayList<>(), false, 4d);

        // Relation between products and toppings
        List<BasicProductInfo> products = new ArrayList<>();
        products.add(new BasicProductInfo("product1", List.of("topping1", "topping2")));
        products.add(new BasicProductInfo("product1", List.of("topping1", "topping2")));

        // Expected products we will obtain. Because the id of the product is only added after saving in the repo, to replicate
        // this functionality we need to create 2 instances per product, one without id and one with id.
        Product product1NoId = new Product("product1", List.of("topping1", "topping2"), 8.5, List.of(2), order, true);
        Product product1Id = new Product("product1", List.of("topping1", "topping2"), 8.5, List.of(2), order, true);
        product1Id.setId(0L);
        AtomicInteger idIncreased = new AtomicInteger();

        Product product2Id = new Product("product1", List.of("topping1", "topping2"), 8.5, List.of(2), order, true);
        product2Id.setId(1L);

        // Headers we will pass to the methods in the test
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", testHeader);

        // What we should return for the methods we will call
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockAuthenticationManager.getEmail()).thenReturn(customerEmail);
        when(storeService.checkId(anyInt())).thenReturn(true);
        when(orderService.sendOrder(customerEmail, storeId)).thenReturn(order);
        when(orderService.getOrder(orderId)).thenReturn(order);
        /*
            Every time this method is called, we make a product like product1Id or product2Id, but the
            ID is dynamically generated, using an atomic integer. This way every duplicate has a new
            id, which emulates the real functionality of orderService.
         */
        when(productService.saveProducts(List.of(product1NoId, product1NoId))).thenAnswer((Answer<List<Product>>) invocation -> {
            Product returnProduct1 = new Product("product1", List.of("topping1", "topping2"), 8.5, List.of(2), order, true);
            returnProduct1.setId(product1Id.getId() + idIncreased.getAndIncrement());

            Product returnProduct2 = new Product("product1", List.of("topping1", "topping2"), 8.5, List.of(2), order, true);
            returnProduct2.setId(product1Id.getId() + idIncreased.getAndIncrement());

            return List.of(returnProduct1, returnProduct2);
        });
        when(productAdapter.verifyProducts(List.of("product1", "product1"), headers)).thenReturn(productItems);
        when(productAdapter.verifyToppings(List.of("topping1", "topping2"), headers)).thenReturn(List.of(topping1, topping2));

        Order result = orderController.sendOrder(new SendOrderModel(storeId, products), testHeader).getBody();

        // Make sure the operation was successful
        assert result != null;

        List<Product> expectedProducts = List.of(product1Id, product2Id);

        // Check the resulting Order object has the expected values
        assertThat(result.getData().getPrice()).isEqualTo(17.0);
        assertThat(result.getProducts()).isEqualTo(expectedProducts);
        assertThat(result.getData().getStatus()).isEqualTo(Status.UNPAID);

        // Check the methods have been called the expected amount of times
        Mockito.verify(productAdapter, times(1)).verifyProducts(anyList(), any());
        Mockito.verify(orderService, times(1)).sendOrder(customerEmail, storeId);
        Mockito.verify(productAdapter, times(productItems.size())).verifyToppings(anyList(), any());
        Mockito.verify(productService, times(1)).saveProducts(expectedProducts);
        Mockito.verify(orderService, times(1)).saveOrder(any());
    }
}
