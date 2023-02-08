package nl.tudelft.sem.template.order.domain.order;

public class OrderIsAlreadyPaidException extends Exception{

    static final long serialVersionUID = 12352312324L;

    public OrderIsAlreadyPaidException(){
        super("You have already paid for this order. Hence, you cannot add to it");
    }

}
