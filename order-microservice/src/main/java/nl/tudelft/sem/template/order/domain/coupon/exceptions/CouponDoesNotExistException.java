package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception that indicates that a coupon with certain id does not exist
 */
public class CouponDoesNotExistException extends Exception{
    static final long serialVersionUID = 12352312324L;

    public CouponDoesNotExistException(int couponId){
        super("Coupon with id '" + Integer.toString(couponId) + "' does not exist");
    }
}
