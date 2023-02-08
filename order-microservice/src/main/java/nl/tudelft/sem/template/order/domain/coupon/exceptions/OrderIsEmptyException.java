package nl.tudelft.sem.template.order.domain.coupon.exceptions;

public class OrderIsEmptyException extends Exception{

    static final long serialVersionUID = 12352312324L;

    public OrderIsEmptyException(){
        super("Order is empty. Please enter a valid order");
    }

}
