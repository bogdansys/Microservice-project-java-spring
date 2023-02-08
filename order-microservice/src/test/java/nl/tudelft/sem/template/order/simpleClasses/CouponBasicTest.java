package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;
import nl.tudelft.sem.template.order.domain.coupon.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CouponBasicTest {

    private Coupon coupon1;
    private Coupon coupon2;

    @BeforeEach
    void setup(){
        ActivationCode ac1 = new ActivationCode("ABCD04");
        ActivationCode ac2 = new ActivationCode("ABCD01");
        Coupon.CouponType typeDiscount = Coupon.CouponType.DISCOUNT;
        Coupon.CouponType typeBuyOne = Coupon.CouponType.BUYONEGETONE;
        double discount = 20.2;
        LocalDate date = LocalDate.parse("2023-01-08");
        int storeId1 = 1;
        int storeId2 = -1;

        coupon1 = new Coupon(ac1, typeDiscount, discount, date, storeId1);
        coupon2 = new Coupon(ac2, typeBuyOne, discount, date, storeId2);
    }

    @Test
    void testCouponConstructor(){
        assertThat(coupon1).isNotNull();
        assertThat(coupon2).isNotNull();
    }

    @Test
    void testGetActivationCode(){
        assertThat(coupon1.getActivationCode()).isEqualTo(new ActivationCode("ABCD04"));
        assertThat(coupon2.getActivationCode()).isEqualTo(new ActivationCode("ABCD01"));
    }

    @Test
    void testGetCouponType(){
        assertThat(coupon1.getCouponType()).isEqualTo(Coupon.CouponType.DISCOUNT);
        assertThat(coupon2.getCouponType()).isEqualTo(Coupon.CouponType.BUYONEGETONE);
    }

    @Test
    void testGetDiscountPercent(){
        assertThat(coupon1.getDiscountPercent()).isEqualTo(20.2);
        assertThat(coupon2.getDiscountPercent()).isEqualTo(20.2);
    }

    @Test
    void testGetExpirationDate(){
        assertThat(coupon1.getExpirationDate()).isEqualTo(LocalDate.parse("2023-01-08"));
        assertThat(coupon2.getExpirationDate()).isEqualTo(LocalDate.parse("2023-01-08"));
    }

    @Test
    void getStoreId(){
        assertThat(coupon1.getStoreid()).isEqualTo(1);
        assertThat(coupon2.getStoreid()).isEqualTo(-1);
    }
}
