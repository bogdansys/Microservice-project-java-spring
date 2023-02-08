package nl.tudelft.sem.template.menu.domain.product;

/**
 * Exception to indicate the ProductName is already in use.
 */
public class ProductNameAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516992349948L;

    public ProductNameAlreadyInUseException(ProductName name) {
        super(name.toString());
    }
}
