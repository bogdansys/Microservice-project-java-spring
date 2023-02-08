package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;
import nl.tudelft.sem.template.order.domain.coupon.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CouponTest {

    private Coupon coupon1;
    private Coupon coupon2;

    @BeforeEach
    void setup() {
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
    void testIsValidCouponTypeDiscount(){
        assertThat(Coupon.translateToCouponType("DISCOUNT")).isNotEmpty();
        assertThat(Coupon.translateToCouponType("discount")).isNotEmpty();
    }

    @Test
    void testIsValidCouponTypeBuyOne(){
        assertThat(Coupon.translateToCouponType("buyonegetonefree")).isNotEmpty();
        assertThat(Coupon.translateToCouponType("buyonegetone")).isNotEmpty();
        assertThat(Coupon.translateToCouponType("BUYONEGETONEFREE")).isNotEmpty();
        assertThat(Coupon.translateToCouponType("BUYONEGETONE")).isNotEmpty();
    }

    @Test
    void testIsNotValidCouponType(){
        assertThat(Coupon.translateToCouponType("dicsount")).isEmpty();
        assertThat(Coupon.translateToCouponType("buy")).isEmpty();
        assertThat(Coupon.translateToCouponType("BUYONE")).isEmpty();
        assertThat(Coupon.translateToCouponType("DISC")).isEmpty();
    }

    @Test
    void testTranslateToCouponDiscount(){
        assertThat(Coupon.translateToCouponType("DISCOUNT").get()).isEqualTo(Coupon.CouponType.DISCOUNT);
        assertThat(Coupon.translateToCouponType("discount").get()).isEqualTo(Coupon.CouponType.DISCOUNT);
    }

    @Test
    void testTranslateToCouponBuyOneGetOne(){
        assertThat(Coupon.translateToCouponType("buyonegetonefree").get()).isEqualTo(Coupon.CouponType.BUYONEGETONE);
        assertThat(Coupon.translateToCouponType("buyonegetone").get()).isEqualTo(Coupon.CouponType.BUYONEGETONE);
        assertThat(Coupon.translateToCouponType("BUYONEGETONEFREE").get()).isEqualTo(Coupon.CouponType.BUYONEGETONE);
        assertThat(Coupon.translateToCouponType("BUYONEGETONE").get()).isEqualTo(Coupon.CouponType.BUYONEGETONE);
    }

    @Test
    void testTranslateInvalid(){
        assertThat(Coupon.translateToCouponType("dicsount")).isEmpty();
        assertThat(Coupon.translateToCouponType("buy")).isEmpty();
        assertThat(Coupon.translateToCouponType("BUYONE")).isEmpty();
        assertThat(Coupon.translateToCouponType("DISC")).isEmpty();
    }

    @Test
    void testCouponsNotEqual(){
        assertThat(coupon1).isNotEqualTo(coupon2);
    }

    @Test
    void testCouponEqual(){
        assertThat(coupon1).isEqualTo(coupon1);
        assertThat(coupon2).isEqualTo(coupon2);
    }

    @Test
    void testDiscountCouponToString(){
        String str = "Coupon{" +
               "id=" + "0" +
               ", activationCode=" + "ABCD04" +
               ", couponType=" + "DISCOUNT" +
               ", discountPercent=" + "20.2" +
               ", expirationDate=" + "2023-01-08" +
               ", storeid=" + "1" +
               '}';
        assertThat(coupon1.toString()).isEqualTo(str);
    }

    @Test
    void testBuyOneCouponToString(){
        String str = "Coupon{" +
               "id=" + "0" +
               ", activationCode=" + "ABCD01" +
               ", couponType=" + "BUYONEGETONE" +
               ", discountPercent=" + "20.2" +
               ", expirationDate=" + "2023-01-08" +
               ", storeid=" + "-1" +
               '}';
        assertThat(coupon2.toString()).isEqualTo(str);
    }
}
