package nl.tudelft.sem.template.order.domain.order;

public class InvalidToppingsException extends Exception{
    static final long serialVersionUID = 4011851994954449009L;

    public InvalidToppingsException(String productName) {
        super("No valid toppings requested for product " + productName + ".");
    }
}
