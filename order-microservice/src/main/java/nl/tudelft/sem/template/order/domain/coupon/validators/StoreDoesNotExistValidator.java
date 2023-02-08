package nl.tudelft.sem.template.order.domain.coupon.validators;

import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreDoesNotExistException;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

public class StoreDoesNotExistValidator extends BaseValidator {

    public StoreDoesNotExistValidator(CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        super(couponRepository, storeRepository, dateProvider);
    }

    @Override
    public boolean handle(CouponValidatorRequest coupon) throws Exception {

        //-1 is a valid storeid because it means that the coupon is applied to all stores
        if (coupon.getStoreid() != -1 && !storeRepository.existsById(coupon.getStoreid())) {
            throw new StoreDoesNotExistException(coupon.getStoreid());
        }
        setNext(null);
        return checkNext(coupon);
    }
}
