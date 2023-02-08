package nl.tudelft.sem.template.order.services;

import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;
import nl.tudelft.sem.template.order.domain.coupon.Coupon;
import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.CreateCouponService;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.ActivationCodeAlreadyInUseException;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.DiscountPercentageNotValidException;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.ExpirationDateNotValidException;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CreateCouponServiceTest {
    @Mock
    private transient CouponRepository couponRepository;

    @Mock
    private transient StoreRepository storeRepository;

    @Mock
    private transient DateProvider dateProvider;

    private transient CreateCouponService createCouponService;

    @BeforeEach
    void setup() {
        createCouponService = new CreateCouponService(couponRepository, storeRepository, dateProvider);
    }

    @Test
    void testAddCoupon() throws Exception {
        ActivationCode activationCode = new ActivationCode("test99");

        String couponType = "discount";

        double discountPercent = 50.0;

        String expirationDate = "2019-10-15";

        int storeId = 5;

        when(couponRepository.existsByActivationCode(activationCode)).thenReturn(false);
        when(dateProvider.getDate()).thenReturn(LocalDate.parse("2019-10-13"));
        when(storeRepository.existsById(storeId)).thenReturn(true);

        Coupon coupon = new Coupon(activationCode, Coupon.translateToCouponType(couponType).get(), discountPercent,
                LocalDate.parse(expirationDate), storeId);

        assertThat(createCouponService.addCoupon(activationCode, couponType,
                discountPercent, expirationDate, storeId).toString())
                .isEqualTo(coupon.toString());
    }

    @Test
    void testExceptionsAddCoupon() {
        ActivationCode activationCode = new ActivationCode("test99");

        String couponType = "discount";

        double discountPercent = 50.0;

        String expirationDate = "2019-10-15";

        int storeId = 5;

        when(couponRepository.existsByActivationCode(activationCode)).thenReturn(true);
        when(dateProvider.getDate()).thenReturn(LocalDate.parse("2019-10-13"));
        when(storeRepository.existsById(storeId)).thenReturn(true);

        // Lambda functions cannot use variables that are not final or effectively final, so I need to
        // store the current value in an unmodified variable
        double finalDiscountPercent = discountPercent;
        assertThatThrownBy(() -> createCouponService.addCoupon(activationCode, couponType, finalDiscountPercent, expirationDate, storeId))
                .isInstanceOf(ActivationCodeAlreadyInUseException.class);

        when(couponRepository.existsByActivationCode(activationCode)).thenReturn(false);
        discountPercent = -1.0;

        double finalDiscountPercent2 = discountPercent;
        assertThatThrownBy(() -> createCouponService.addCoupon(activationCode, couponType, finalDiscountPercent2, expirationDate, storeId))
                .isInstanceOf(DiscountPercentageNotValidException.class);

        discountPercent = 101.0;

        double finalDiscountPercent3 = discountPercent;
        assertThatThrownBy(() -> createCouponService.addCoupon(activationCode, couponType, finalDiscountPercent3, expirationDate, storeId))
                .isInstanceOf(DiscountPercentageNotValidException.class);

        discountPercent = 50.0;
        double finalDiscountPercent4 = discountPercent;
        when(dateProvider.getDate()).thenReturn(LocalDate.parse("2019-10-20"));
        assertThatThrownBy(() -> createCouponService.addCoupon(activationCode, couponType, finalDiscountPercent4, expirationDate, storeId))
                .isInstanceOf(ExpirationDateNotValidException.class);
    }
}
