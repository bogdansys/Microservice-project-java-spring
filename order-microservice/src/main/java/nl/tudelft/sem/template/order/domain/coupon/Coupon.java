package nl.tudelft.sem.template.order.domain.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

/**
 * A DDD entity representing a coupon in our domain
 */
@Entity
@Table(name = "coupons")
@NoArgsConstructor
public class Coupon implements Serializable {
    static final long serialVersionUID = 12312312321L;

    /**
     * enumerator for the type of coupon. Easy for future coupon types to be added
     * public, so it can be tested
     */
    public enum CouponType{
        DISCOUNT,
        BUYONEGETONE

    }

    /**
     * ID that uniquely identifies a coupon entry in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    /**
     * An activation code that is unique for each coupon, that is used to apply the coupon
     */
    @Column(name = "activation_code", nullable = false, unique = true)
    @Convert(converter = ActivationCodeAttributeConverter.class)
    @Getter
    private ActivationCode activationCode;

    /**
     * The type of coupon, determines if the coupon is a discount or a buy one, get one free coupon
     * Can be extended to include other coupon types in future
     */
    @Column(name = "type_of_coupon", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private CouponType couponType;

    /**
     * Discount percentage for a discount coupon. Note: when the coupon is not of discount type, this
     * field is set to -1
     */
    @Column(name = "discount_percentage", nullable = false)
    @Getter
    private double discountPercent;

    /**
     * Expiration date for a coupon. Note: must be in format 'uuuu-MM-dd'
     */
    @Column(name = "expiration_date", nullable = false)
    @Convert(converter = ExpirationDateConverter.class)
    @Getter
    private LocalDate expirationDate;

    /**
     * Store id that a coupon is associated to. A coupon can only be applied if the
     * corresponding order has this store id. If storeid = -1, then it can be applied
     * to all stores
     */
    @Column(name = "storeid")
    @Getter
    private int storeid;

    /**
     * Create new coupon
     * @param activationCode activation code for coupon
     * @param couponType type of coupon
     * @param discountPercent if discount coupon, the percentage of the discount
     */
    public Coupon(ActivationCode activationCode, CouponType couponType, double discountPercent, LocalDate expirationDate, int storeid){
        this.activationCode = activationCode;
        this.couponType = couponType;
        this.discountPercent = discountPercent;
        this.expirationDate = expirationDate;
        this.storeid = storeid;
    }

    /**
     * Translates a string to a coupon type.
     * @param couponType string that is to be converted to a coupon type
     * @return Optional CouponType, empty if string cannot be converted, otherwise it contains the coupon
     * type corresponding to the string
     */
    public static Optional<CouponType> translateToCouponType(String couponType){
        couponType = couponType.toLowerCase(Locale.ENGLISH).replaceAll(" ", "");

        if(couponType.equals("discount")) return Optional.of(CouponType.DISCOUNT);
        if(couponType.equals("buyonegetone") || couponType.equals("buyonegetonefree")) {
            return Optional.of(CouponType.BUYONEGETONE);
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", activationCode=" + activationCode +
                ", couponType=" + couponType +
                ", discountPercent=" + discountPercent +
                ", expirationDate=" + expirationDate +
                ", storeid=" + storeid +
                '}';
    }
}
