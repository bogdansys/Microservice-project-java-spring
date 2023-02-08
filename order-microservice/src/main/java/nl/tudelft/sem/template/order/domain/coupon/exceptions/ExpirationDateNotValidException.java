package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception that indicates that an expiration date is not valid
 */
public class ExpirationDateNotValidException extends Exception{
    static final long serialVersionUID = 12312312322L;

    public ExpirationDateNotValidException(String expirationDate){
        super("The date '" + expirationDate + "' is not a valid date. Must be in format YYYY-MM-DD and after" +
                "the current date.");
    }
}
