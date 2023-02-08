package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception thrown when the coupon is expired
 */
public class ActivationCodeIsExpiredException extends Exception {

    static final long serialVersionUID = 12312312328L;

    public ActivationCodeIsExpiredException(String activationCode){
        super("The activation code '" + activationCode + "' is already expired. Please enter a valid coupon");
    }

}
