package nl.tudelft.sem.template.menu.domain.topping;

import java.util.List;

/**
 * Exception to indicate the toppings don't exist
 */
public class InvalidToppingsException extends Exception {
    static final long serialVersionUID = 33123123349948L;

    public InvalidToppingsException(List<Integer> toppingIds) {
        super(toppingIds.toString());
    }
}
