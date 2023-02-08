package nl.tudelft.sem.template.order.domain.coupon.validators;

import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.ActivationCodeIsNotValidException;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

public class ActivationCodeIsNotValidValidator extends BaseValidator {

    public ActivationCodeIsNotValidValidator(CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        super(couponRepository, storeRepository, dateProvider);
    }

    @Override
    public boolean handle(CouponValidatorRequest coupon) throws Exception {
        //throws exception if activation code is not valid
        if (!coupon.getActivationCode().isActivationCodeValid()) {
            throw new ActivationCodeIsNotValidException(coupon.getActivationCode().toString());
        }
        setNext(new CheckIfActivationCodeExistsValidator(couponRepository, storeRepository, dateProvider));
        return checkNext(coupon);
    }


}
