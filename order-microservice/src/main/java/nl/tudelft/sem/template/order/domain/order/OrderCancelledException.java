package nl.tudelft.sem.template.order.domain.order;

public class OrderCancelledException extends Exception{
    static final long serialVersionUID = 9047192551473239464L;

    public OrderCancelledException(int orderId) {
        super("The order with ID " + orderId + " has already been cancelled.");
    }
}
