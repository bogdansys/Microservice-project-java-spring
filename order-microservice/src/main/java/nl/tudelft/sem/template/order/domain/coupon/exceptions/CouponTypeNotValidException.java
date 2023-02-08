package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception that indicates that the coupon type input was not valid
 */
public class CouponTypeNotValidException extends Exception{
    static final long serialVersionUID = 12312312329L;

    public CouponTypeNotValidException(String couponType){
        super("The coupon type '" + couponType + "' is not a valid type of coupon. " +
                "\n The valid coupon types are: Discount and BuyOneGetOneFree ");
    }
}
