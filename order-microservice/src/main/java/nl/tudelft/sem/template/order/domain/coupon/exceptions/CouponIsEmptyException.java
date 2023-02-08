package nl.tudelft.sem.template.order.domain.coupon.exceptions;

public class CouponIsEmptyException extends Exception{

    static final long serialVersionUID = 12352312324L;

    public CouponIsEmptyException(){
        super("Coupon is empty. Please enter a valid coupon");
    }

}
