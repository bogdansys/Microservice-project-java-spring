package nl.tudelft.sem.template.order.domain.order;

public class OrderDoesNotExistException extends Exception{
    static final long serialVersionUID = -3387516993124229948L;

    public OrderDoesNotExistException(int orderId){
        super("An order with ID " + Integer.toString(orderId) + " does not exist.");
    }
}
