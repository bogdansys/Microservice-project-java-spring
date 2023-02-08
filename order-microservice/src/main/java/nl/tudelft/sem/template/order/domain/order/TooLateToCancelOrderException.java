package nl.tudelft.sem.template.order.domain.order;

public class TooLateToCancelOrderException extends Exception{
    static final long serialVersionUID = -3387516993124229948L;

    public TooLateToCancelOrderException(int orderId) {
        super("It is too late to cancel the order with ID " + orderId + ".");
    }
}
