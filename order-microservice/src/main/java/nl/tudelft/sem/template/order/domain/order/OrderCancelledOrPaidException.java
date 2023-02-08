package nl.tudelft.sem.template.order.domain.order;

public class OrderCancelledOrPaidException extends Exception{
    static final long serialVersionUID = -3387516993124229948L;

    public OrderCancelledOrPaidException(Order order) {
        super(order.toString());
    }
}
