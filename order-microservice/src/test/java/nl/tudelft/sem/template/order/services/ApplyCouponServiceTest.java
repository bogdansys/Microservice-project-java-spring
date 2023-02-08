package nl.tudelft.sem.template.order.services;

import nl.tudelft.sem.template.order.domain.coupon.*;
import nl.tudelft.sem.template.order.domain.order.Order;
import nl.tudelft.sem.template.order.domain.order.OrderRepository;
import nl.tudelft.sem.template.order.domain.order.OrderUtils;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ApplyCouponServiceTest {
    @Mock
    private transient CouponRepository couponRepository;

    @Mock
    private transient OrderRepository orderRepository;

    @Mock
    private transient DateProvider dateProvider;

    private transient ApplyCouponService applyCouponService;

    @BeforeEach
    void setup() {
        applyCouponService = new ApplyCouponService(couponRepository, dateProvider, orderRepository);
    }

    @Test
    void testApplyCoupon() throws Exception {
        ActivationCode activationCode = new ActivationCode("test99");

        String couponType = "discount";

        double discountPercent = 50.0;

        String expirationDate = "2019-10-15";

        int storeId = 5;

        Coupon coupon = new Coupon(activationCode, Coupon.translateToCouponType(couponType).get(), discountPercent,
                LocalDate.parse(expirationDate), storeId);

        int orderId = 5;
        Order order = new Order("testEmail", storeId);
        order.setOrderId(orderId);
        order.getData().setPrice(Double.MAX_VALUE);

        Product product = new Product(
                "Margherita",
                List.of(),
                3.0,
                List.of(1, 2),
                order,
                false);
        product.setId(1L);
        OrderUtils.addProduct(product, order);

        Product product2 = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                order,
                false);
        product2.setId(0L);

        OrderUtils.addProduct(product2, order);

        when(couponRepository.existsByActivationCode(activationCode)).thenReturn(true);
        when(couponRepository.findByActivationCode(activationCode)).thenReturn(Optional.of(coupon));
        when(dateProvider.getDate()).thenReturn(LocalDate.parse("2019-10-13"));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThat(applyCouponService.applyCoupon("test99", orderId)).isEqualTo(2d);
    }

    @Test
    void testStrategyTypes() throws Exception {
        ActivationCode activationCode = new ActivationCode("test99");

        String couponType = "discount";

        double discountPercent = 50.0;

        String expirationDate = "2019-10-15";

        int storeId = 5;

        Coupon coupon = new Coupon(activationCode, Coupon.translateToCouponType(couponType).get(), discountPercent,
                LocalDate.parse(expirationDate), storeId);

        int orderId = 5;
        Order order = new Order("testEmail", storeId);
        order.setOrderId(orderId);
        order.getData().setPrice(Double.MAX_VALUE);

        Product product = new Product(
                "Margherita",
                List.of(),
                3.0,
                List.of(1, 2),
                order,
                false);
        product.setId(1L);
        OrderUtils.addProduct(product, order);

        Product product2 = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                order,
                false);
        product2.setId(0L);

        OrderUtils.addProduct(product2, order);

        when(couponRepository.existsByActivationCode(activationCode)).thenReturn(true);
        when(couponRepository.findByActivationCode(activationCode)).thenReturn(Optional.of(coupon));
        when(dateProvider.getDate()).thenReturn(LocalDate.parse("2019-10-13"));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        applyCouponService.applyCoupon("test99", orderId);

        assertThat(order.getStrategy()).isOfAnyClassIn(ApplyDiscountCouponStrategy.class);

        couponType = "buyonegetone";
        coupon = new Coupon(activationCode, Coupon.translateToCouponType(couponType).get(),
                discountPercent, LocalDate.parse(expirationDate), storeId);
        when(couponRepository.findByActivationCode(activationCode)).thenReturn(Optional.of(coupon));

        applyCouponService.applyCoupon("test99", orderId);

        assertThat(order.getStrategy()).isOfAnyClassIn(ApplyBOGOCouponStrategy.class);
    }
}
