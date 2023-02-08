package nl.tudelft.sem.template.order.domain.coupon;

import nl.tudelft.sem.template.order.domain.coupon.exceptions.CanNotApplyBOGOCouponWithSingleProductException;

import java.util.List;

/**
 * interface of all pricing strategies
 */
public interface PricingStrategy {

    double computePrice(List<Double> prices) throws CanNotApplyBOGOCouponWithSingleProductException;

    String toString();

}
