package nl.tudelft.sem.template.order.domain.order;

public class NotValidPricingStrategyException extends Exception{

    static final long serialVersionUID = -3387516993124229948L;

    public NotValidPricingStrategyException(){
        super("The pricing strategy is invalid");
    }
}
