package nl.tudelft.sem.template.order.domain.coupon.exceptions;


public class CanNotApplyBOGOCouponWithSingleProductException extends  Exception{
    static final long serialVersionUID = 12312312330L;

    public CanNotApplyBOGOCouponWithSingleProductException(){
        super("To use buy one get one free coupon, you need at lease two pizzas");
    }


}
