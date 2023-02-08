package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception thrown when the activation code does not exist
 */
public class ActivationCodeDoesNotExistException extends Exception {

    static final long serialVersionUID = 12312312327L;

    public ActivationCodeDoesNotExistException(String activationCode){
        super("The activation code '" + activationCode + "' does not exist. Please use a valid code");
    }

}
