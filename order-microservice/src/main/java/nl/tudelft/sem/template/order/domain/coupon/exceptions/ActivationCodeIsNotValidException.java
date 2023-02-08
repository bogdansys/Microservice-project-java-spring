package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception to indicate that the activation code is not valid
 */
public class ActivationCodeIsNotValidException extends  Exception{
    static final long serialVersionUID = 12312312325L;

    public ActivationCodeIsNotValidException(String activationCode){
        super("The activation code '" + activationCode + "' is not valid. Please use " +
                "4 letters followed by 2 numbers.");
    }
}
