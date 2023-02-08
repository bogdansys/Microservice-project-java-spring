package nl.tudelft.sem.template.order.domain.store;

/**
 * Exception to indicate the store name is already in use.
 */
public class StoreNameAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public StoreNameAlreadyInUseException(StoreName name) {
        super(name.toString());
    }
}
