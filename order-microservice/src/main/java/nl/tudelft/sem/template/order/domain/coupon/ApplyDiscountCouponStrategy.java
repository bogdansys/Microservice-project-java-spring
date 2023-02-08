package nl.tudelft.sem.template.order.domain.coupon;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Pricing strategy that calculates price with discount coupon
 */
public class ApplyDiscountCouponStrategy implements PricingStrategy {

    private final transient double discountPercent;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public ApplyDiscountCouponStrategy(double discountPercent){
        this.discountPercent =discountPercent;
    }

    @Override
    public double computePrice(List<Double> prices){
        if(prices.size()==0){
            return 0;
        }
        double result = 0;
        for(double price: prices) {
            result += price;
        }
        double amountSaved = result * 0.01 * discountPercent;
        return Math.round((result - amountSaved) * 100.0) / 100.0; //round to two decimal places
    }

    @Override
    public String toString() {
        return "ApplyDiscountCouponStrategy, "+ discountPercent;
    }
}
