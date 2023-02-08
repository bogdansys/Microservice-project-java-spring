package nl.tudelft.sem.template.order.domain.order;

import nl.tudelft.sem.template.order.domain.coupon.PricingStrategy;

import java.util.List;

/**
 * Pricing strategy that caclulates price without coupon
 */
public class BasicPricingStrategy implements PricingStrategy {

    @Override
    public double computePrice(List<Double> prices){
        if(prices.size()==0){
            return 0;
        }
        double result = 0;
        for(double price: prices) {
            result += price;
        }
        return Math.round((result) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "BasicPricingStrategy";
    }

}