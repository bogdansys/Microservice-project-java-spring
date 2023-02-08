package nl.tudelft.sem.template.order.domain.store;

/**
 * Exception that indicates that a store does not exist by id
 */
public class StoreDoesNotExistException extends Exception{
    static final long serialVersionUID = -3387516993124229943L;

    public StoreDoesNotExistException(int storeId){
        super("Store with id '" + Integer.toString(storeId) + "' does not exist");
    }
}
