package nl.tudelft.sem.template.order.domain;

import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.domain.order.*;
import nl.tudelft.sem.template.order.domain.product.ProductService;
import nl.tudelft.sem.template.order.domain.providers.TimeProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;
import nl.tudelft.sem.template.order.domain.store.StoreService;
import nl.tudelft.sem.template.order.framework.ProductMicroserviceAdapter;
import nl.tudelft.sem.template.order.framework.StoreIdAdapter;
import nl.tudelft.sem.template.order.model.GetOrderModel;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class GetOrderTests {
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
    private transient OrderService mockOrderService;

    @Mock
    private transient ProductMicroserviceAdapter productAdapter;

    private transient OrderController mockOrderController;

    /**
     * Setup.
     */
    @BeforeEach
    void setup() {
        storeService = new StoreService(storeRepository);
        orderHelperService = new OrderHelperService(productAdapter);
        addToOrderService = new AddToOrderService(mockOrderService, productService, productAdapter, orderHelperService);
        modifyOrderService = new ModifyOrderService(mockOrderService, productService, productAdapter, addToOrderService,
                orderHelperService);

        mockOrderController = new OrderController(mockAuthenticationManager, storeService, mockOrderService,
                productService, modifyOrderService, mockStoreIdAdapter, addToOrderService);
    }

    /**
     * Test user gets access to the orders they have sent
     */
    @Test
    public void getOrdersUser() {
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockAuthenticationManager.getEmail()).thenReturn("testEmail");
        mockOrderController.getOrders(null);

        verify(mockOrderService, times(1)).findAllByCustomerId("testEmail");
    }

    /**
     * Test store owner gets access to the orders they have received
     */
    @Test
    public void getOrdersStoreOwner() {
        when(mockAuthenticationManager.getRole()).thenReturn("STORE_OWNER");
        when(mockAuthenticationManager.getEmail()).thenReturn("testEmail");
        when(mockStoreIdAdapter.getUsersStoreId("token")).thenReturn(5);
        when(mockStoreIdAdapter.getUsersStoreId("cata")).thenThrow(new RuntimeException());

        mockOrderController.getOrders("token");
        verify(mockOrderService, times(1)).findAllByStoreId(5);

        assertThatThrownBy(() -> mockOrderController.getOrders("cata")).isInstanceOf(ResponseStatusException.class);
    }

    /**
     * Test manager gets access to the orders
     */
    @Test
    public void getOrdersManager() {
        when(mockAuthenticationManager.getRole()).thenReturn("MANAGER");
        when(mockAuthenticationManager.getEmail()).thenReturn("testEmail");
        mockOrderController.getOrders(null);

        verify(mockOrderService, times(1)).listAllOrders();
    }

    /**
     * Test user gets access to a specific order
     */
    @Test
    public void getOrderUser() throws OrderDoesNotExistException {
        String firstCustomerId = "customerId";
        String secondCustomerId = "differentId";

        int orderId = 5;
        Order order = new Order(firstCustomerId, 4);
        order.setOrderId(orderId);

        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockAuthenticationManager.getEmail()).thenReturn(firstCustomerId);
        when(mockOrderService.getOrder(orderId)).thenReturn(order);

        Order result = mockOrderController.getOrder(new GetOrderModel(orderId), null).getBody();

        assertThat(result).isEqualTo(order);
        verify(mockOrderService, times(1)).getOrder(orderId);

        when(mockAuthenticationManager.getEmail()).thenReturn(secondCustomerId);
        assertThatThrownBy(() -> mockOrderController.getOrder(new GetOrderModel(orderId), null))
                .isInstanceOf(ResponseStatusException.class);
    }

    /**
     * Test store owner gets access to a specific order
     */
    @Test
    public void getOrderStoreOwner() throws OrderDoesNotExistException {
        int firstStoreId = 4;
        int secondStoreId = 5;
        String token = "token";

        int orderId = 5;
        Order order = new Order("customerId", firstStoreId);
        order.setOrderId(orderId);

        when(mockAuthenticationManager.getRole()).thenReturn("STORE_OWNER");
        when(mockStoreIdAdapter.getUsersStoreId(token)).thenReturn(firstStoreId);
        when(mockAuthenticationManager.getEmail()).thenReturn("testEmail");
        when(mockOrderService.getOrder(orderId)).thenReturn(order);

        Order result = mockOrderController.getOrder(new GetOrderModel(orderId), token).getBody();

        verify(mockOrderService, times(1)).getOrder(orderId);
        verify(mockStoreIdAdapter, times(1)).getUsersStoreId(token);
        assertThat(result).isEqualTo(order);

        when(mockStoreIdAdapter.getUsersStoreId(token)).thenReturn(secondStoreId);
        assertThatThrownBy(() -> mockOrderController.getOrder(new GetOrderModel(orderId), token))
                .isInstanceOf(ResponseStatusException.class);
    }

    /**
     * Test manager gets access to a specific order
     */
    @Test
    public void getOrderManager() throws OrderDoesNotExistException {
        String token = "token";

        int orderId = 5;
        Order order = new Order("customerId", 5);
        order.setOrderId(orderId);

        when(mockAuthenticationManager.getRole()).thenReturn("MANAGER");
        when(mockOrderService.getOrder(orderId)).thenReturn(order);

        Order result = mockOrderController.getOrder(new GetOrderModel(orderId), token).getBody();

        verify(mockOrderService, times(1)).getOrder(orderId);
        assertThat(result).isEqualTo(order);
    }


    @Test
    public void OrderServicegetOrdersEmpty() {
        TimeProvider timeProvider = mock(TimeProvider.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        int orderId = 2;
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        OrderService orderService = new OrderService(orderRepository, timeProvider);

        Exception e = assertThrows(OrderDoesNotExistException.class, () -> orderService.getOrder(orderId));
        assertThat(e.getMessage())
                .isEqualTo("An order with ID " + Integer.toString(orderId) + " does not exist.");
    }

    @Test
    public void getOrderEmpty() {

        TimeProvider timeProvider = mock(TimeProvider.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        int orderId = 2;
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        OrderService orderService = new OrderService(orderRepository, timeProvider);
        OrderController orderController = new OrderController(mockAuthenticationManager, storeService,
                orderService, productService, modifyOrderService, mockStoreIdAdapter, addToOrderService);

        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockAuthenticationManager.getEmail()).thenReturn("customerId");

        Exception e = assertThrows(ResponseStatusException.class,
                () -> orderController.getOrder(new GetOrderModel(orderId), null));

        assertThat(e.getMessage()).contains("An order with ID " + Integer.toString(orderId) + " does not exist.");
    }
}
