package nl.tudelft.sem.template.order.domain.coupon.validators;

import nl.tudelft.sem.template.order.domain.DateValidator;
import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.ExpirationDateNotValidException;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

public class ExpirationDateNotValidValidator extends BaseValidator {

    public ExpirationDateNotValidValidator( CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        super(couponRepository, storeRepository, dateProvider);
    }

    @Override
    public boolean handle(CouponValidatorRequest coupon) throws Exception {

        //uses date validator to check if expiration date is in the correct format - must be in form uuuu-MM-dd
        if (!DateValidator.isValid(coupon.getExpirationDate().toString())) {
            throw new ExpirationDateNotValidException(coupon.getExpirationDate().toString());
        }

        setNext(new ExpirationDateNotValidTooLateValidator(couponRepository, storeRepository, dateProvider));
        return checkNext(coupon);

    }
}

