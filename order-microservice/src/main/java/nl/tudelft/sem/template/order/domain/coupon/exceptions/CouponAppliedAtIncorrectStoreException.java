package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception that indicates that the coupon is applied at the incorrect store
 */
public class CouponAppliedAtIncorrectStoreException  extends  Exception{

    static final long serialVersionUID = 12312312330L;

    public CouponAppliedAtIncorrectStoreException(String activationCode){
        super("The activation code '" + activationCode + "' is not valid for this store.");
    }

}
