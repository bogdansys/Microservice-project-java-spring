package nl.tudelft.sem.template.order.domain.coupon.exceptions;

/**
 * Exception that indicates that a discount percentage is not valid, in that it is not between 0 and 100
 */
public class DiscountPercentageNotValidException extends Exception{
    static final long serialVersionUID = 12312312323L;

    public DiscountPercentageNotValidException(double discountPercent){
        super("The discount percentage " + Double.toString(discountPercent) + " is not a valid. Check" +
                "that the percentage is between 0 and 100.");
    }
}
