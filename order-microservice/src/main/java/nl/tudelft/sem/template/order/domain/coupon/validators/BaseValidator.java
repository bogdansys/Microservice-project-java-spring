package nl.tudelft.sem.template.order.domain.coupon.validators;


import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

/**
 * contains shared logic for all validators.
 * implementing the chain of responsibility pattern
 */

public abstract class BaseValidator implements Validator {
    protected transient Validator next;
    protected transient CouponRepository couponRepository;
    protected transient StoreRepository storeRepository;
    protected transient DateProvider dateProvider;

    public BaseValidator(CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        this.storeRepository = storeRepository;
        this.couponRepository = couponRepository;
        this.dateProvider = dateProvider;
    }


    @Override
    public void setNext(Validator next) {
        this.next = next;
    }

    /**
     * runs the check on the next item in the chain if it exists.
     * or ends the chain if it doesn't.
     */
    protected boolean checkNext(CouponValidatorRequest coupon) throws Exception {
        if (next == null) {
            return true;
        }
        return next.handle(coupon);
    }

}

