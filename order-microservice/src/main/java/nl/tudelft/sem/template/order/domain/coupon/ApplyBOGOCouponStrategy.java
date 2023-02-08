package nl.tudelft.sem.template.order.domain.coupon;


import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.CanNotApplyBOGOCouponWithSingleProductException;

import java.util.Collections;
import java.util.List;

/**
 * Pricing strategy that calculates price with buy one, get one free coupon
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
@NoArgsConstructor
public class ApplyBOGOCouponStrategy implements PricingStrategy {
    @Override
    public double computePrice(List<Double> prices) throws CanNotApplyBOGOCouponWithSingleProductException {
        if(prices.size() == 1){ //creates PMD error unnecessarily, so it is suppressed
            throw new CanNotApplyBOGOCouponWithSingleProductException();
        }

        Collections.sort(prices);
        double cheapestPizzaPrice = (prices.size() == 0) ? 0 : Math.max(0, prices.get(0));
        double result = prices.stream().mapToDouble(Double::doubleValue).sum();

        return Math.round((result - cheapestPizzaPrice) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "ApplyBOGOCouponStrategy";
    }
}
