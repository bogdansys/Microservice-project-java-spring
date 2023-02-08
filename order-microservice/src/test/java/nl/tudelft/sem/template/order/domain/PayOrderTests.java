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
import nl.tudelft.sem.template.order.model.PayOrderModel;
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

import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * The type Order controller test.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class PayOrderTests {
    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Mock
    private transient StoreRepository storeRepository;

    @Mock
    private transient StoreIdAdapter mockStoreIdAdapter;

    private transient StoreService storeService;

    @Mock
    private transient ProductService productService;

    @Mock
    private transient TimeProvider mockTimeProvider;

    private transient ModifyOrderService modifyOrderService;

    private transient AddToOrderService addToOrderService;

    private transient OrderHelperService orderHelperService;

    private transient OrderService orderService;

    @Mock
    private transient ProductMicroserviceAdapter productAdapter;

    private transient OrderController orderController;

    @Mock
    private OrderRepository orderRepository;

    /**
     * Setup.
     */
    @BeforeEach
    void setup() {
        orderService = new OrderService(orderRepository, mockTimeProvider);
        storeService = new StoreService(storeRepository);
        orderHelperService = new OrderHelperService(productAdapter);
        addToOrderService = new AddToOrderService(orderService, productService, productAdapter, orderHelperService);
        modifyOrderService = new ModifyOrderService(orderService, productService, productAdapter, addToOrderService,
                orderHelperService);

        orderController = new OrderController(mockAuthenticationManager, storeService, orderService,
                productService, modifyOrderService, mockStoreIdAdapter, addToOrderService);
    }

    /**
     * Test pay order with a deliveryTime in the correct range.
     */
    @Test
    public void testPayOrder() {
        String customerEmail = "testEmail";
        int storeId = 55;
        int orderId = 5;

        PayOrderModel payOrderModel = new PayOrderModel(orderId, 31);

        Order order = new Order(customerEmail, storeId);
        order.setOrderId(orderId);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(mockAuthenticationManager.getEmail()).thenReturn(customerEmail);
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockTimeProvider.getTime()).thenReturn(LocalDateTime.of(2002, 1, 1, 9, 0));

        orderController.payOrder(payOrderModel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
                .withChronology(IsoChronology.INSTANCE);
        String expectedDay = formatter.format(LocalDateTime.of(2002, 1, 1, 9, 31));

        assertThat(order.getData().getStatus()).isEqualTo(Status.PAID);
        assertThat(order.getData().getDeliveryTime()).isEqualTo(expectedDay);
    }

    /**
     * Test pay order with a delivery time less than the minimum.
     */
    @Test
    public void testPayOrderMinTime() {
        String customerEmail = "testEmail";
        int storeId = 55;
        int orderId = 5;

        PayOrderModel payOrderModel = new PayOrderModel(orderId, 29);

        Order order = new Order(customerEmail, storeId);
        order.setOrderId(orderId);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(mockAuthenticationManager.getEmail()).thenReturn(customerEmail);
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockTimeProvider.getTime()).thenReturn(LocalDateTime.of(2002, 1, 1, 9, 0));

        orderController.payOrder(payOrderModel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
                .withChronology(IsoChronology.INSTANCE);
        String expectedDay = formatter.format(LocalDateTime.of(2002, 1, 1, 9, 30));

        assertThat(order.getData().getStatus()).isEqualTo(Status.PAID);
        assertThat(order.getData().getDeliveryTime()).isEqualTo(expectedDay);
    }

    /**
     * Test pay order with a delivery time higher than the maximum.
     */
    @Test
    public void testPayOrderMaxTime() {
        String customerEmail = "testEmail";
        int storeId = 55;
        int orderId = 5;

        long expectedDeliveryTime = 12 * 60;

        PayOrderModel payOrderModel = new PayOrderModel(orderId, expectedDeliveryTime + 1L);

        Order order = new Order(customerEmail, storeId);
        order.setOrderId(orderId);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(mockAuthenticationManager.getEmail()).thenReturn(customerEmail);
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockTimeProvider.getTime()).thenReturn(LocalDateTime.of(2002, 1, 1, 9, 0));

        orderController.payOrder(payOrderModel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
                .withChronology(IsoChronology.INSTANCE);
        String expectedDay = formatter.format(LocalDateTime.of(2002, 1, 1, 21, 0));

        assertThat(order.getData().getStatus()).isEqualTo(Status.PAID);
        assertThat(order.getData().getDeliveryTime()).isEqualTo(expectedDay);
    }

    /**
     * Test trying to pay an already paid order
     */
    @Test
    public void testPayOrderAlready() {
        String customerEmail = "testEmail";
        int storeId = 55;
        int orderId = 5;

        PayOrderModel payOrderModel = new PayOrderModel(orderId, 30);

        Order order = new Order(customerEmail, storeId);
        order.setOrderId(orderId);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(mockAuthenticationManager.getEmail()).thenReturn(customerEmail);
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockTimeProvider.getTime()).thenReturn(LocalDateTime.of(2002, 1, 1, 9, 0));

        orderController.payOrder(payOrderModel);

        assertThatThrownBy(() -> orderController.payOrder(payOrderModel)).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> orderController.payOrder(payOrderModel)).hasMessageContaining("BAD_REQUEST");
        assertThatThrownBy(() -> orderController.payOrder(payOrderModel)).hasMessageNotContaining("UNAUTHORIZED");
    }

    /**
     * Test trying to pay an already paid order
     */
    @Test
    public void testWrongCustomer() {
        String customerEmail = "testEmail";
        int storeId = 55;
        int orderId = 5;

        PayOrderModel payOrderModel = new PayOrderModel(orderId, 30);

        Order order = new Order(customerEmail, storeId);
        order.setOrderId(orderId);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(mockAuthenticationManager.getEmail()).thenReturn("notTestEmail");
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockTimeProvider.getTime()).thenReturn(LocalDateTime.of(2002, 1, 1, 9, 0));

        assertThatThrownBy(() -> orderController.payOrder(payOrderModel)).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> orderController.payOrder(payOrderModel)).hasMessageContaining("UNAUTHORIZED");
        assertThatThrownBy(() -> orderController.payOrder(payOrderModel)).hasMessageNotContaining("BAD_REQUEST");
    }
}
