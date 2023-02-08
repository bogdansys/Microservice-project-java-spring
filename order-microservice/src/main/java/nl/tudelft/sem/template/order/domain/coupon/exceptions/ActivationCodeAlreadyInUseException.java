package nl.tudelft.sem.template.order.domain.coupon.exceptions;

import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;

/**
 * Exception to indicate that the activation code is already being used in the database
 */
public class ActivationCodeAlreadyInUseException extends Exception{
    static final long serialVersionUID = 12312312326L;

    public ActivationCodeAlreadyInUseException(ActivationCode activationCode){
        super("Activation Code '" + activationCode.toString() + "' is already in use.");
    }
}
