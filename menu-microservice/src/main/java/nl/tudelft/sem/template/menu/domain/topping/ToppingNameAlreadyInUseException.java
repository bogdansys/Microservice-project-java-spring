package nl.tudelft.sem.template.menu.domain.topping;

/**
 * Exception to indicate the ToppingName is already in use.
 */
public class ToppingNameAlreadyInUseException extends Exception {
    static final long serialVersionUID = -138753213349948L;

    public ToppingNameAlreadyInUseException(ToppingName name) {
        super(name.toString());
    }
}
