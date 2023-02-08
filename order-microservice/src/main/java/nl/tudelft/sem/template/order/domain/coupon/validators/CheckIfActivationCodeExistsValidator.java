package nl.tudelft.sem.template.order.domain.coupon.validators;

import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;
import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.ActivationCodeAlreadyInUseException;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

public class CheckIfActivationCodeExistsValidator extends BaseValidator {

    public CheckIfActivationCodeExistsValidator(CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        super(couponRepository, storeRepository, dateProvider);
    }


    //throws exception if activation code already exists in database / is not unique
    @Override
    public boolean handle(CouponValidatorRequest coupon) throws Exception {

        if (checkIfActivationCodeExists(coupon.getActivationCode())) {
            throw new ActivationCodeAlreadyInUseException(coupon.getActivationCode());
        }
        setNext(new CouponTypeNotValidValidator(couponRepository, storeRepository, dateProvider));
        return checkNext(coupon);
    }

    public boolean checkIfActivationCodeExists(ActivationCode activationCode) {
        return couponRepository.existsByActivationCode(activationCode);
    }

}
