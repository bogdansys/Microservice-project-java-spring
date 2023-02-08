package nl.tudelft.sem.template.menu.domain;


/**
 * Exception to indicate that the price is invalid
 */
public class InvalidPriceException extends Exception {
    static final long serialVersionUID = -338751699349948L;

    public InvalidPriceException(Double price) {
        super(price.toString());
    }
}
