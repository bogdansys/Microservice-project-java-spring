package nl.tudelft.sem.template.order.domain.order;

public class InvalidProductsException extends Exception{
    static final long serialVersionUID = 6048833961852189959L;

    public InvalidProductsException() {
        super("No valid products in the request.");
    }
}
