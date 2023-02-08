package nl.tudelft.sem.template.order.domain.coupon.validators;


public interface Validator {
    void setNext(Validator next);

    boolean handle(CouponValidatorRequest coupon) throws Exception;
}
