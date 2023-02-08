package nl.tudelft.sem.template.order.domain.product;

public class ProductDoesNotExistException extends Exception{
    static final long serialVersionUID = 7058445554867233146L;

    public ProductDoesNotExistException(Long productId) {
        super("Product with id " + productId + "does not exist.");
    }
}
