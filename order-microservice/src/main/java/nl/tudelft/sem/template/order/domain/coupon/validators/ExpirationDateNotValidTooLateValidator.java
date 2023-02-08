package nl.tudelft.sem.template.order.domain.coupon.validators;


import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.ExpirationDateNotValidException;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

import java.time.LocalDate;

public class ExpirationDateNotValidTooLateValidator extends BaseValidator {

    public ExpirationDateNotValidTooLateValidator(  CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        super(couponRepository, storeRepository, dateProvider);
    }

    @Override
    public boolean handle(CouponValidatorRequest coupon) throws Exception {

        //checks if the expiration date is not the current date, or before the current date
        LocalDate expirationDateObject = LocalDate.parse(coupon.getExpirationDate().toString());
        LocalDate currentDate = dateProvider.getDate();
        if (currentDate.isAfter(expirationDateObject) || currentDate.isEqual(expirationDateObject)) {
            throw new ExpirationDateNotValidException(coupon.getExpirationDate().toString());
        }

        setNext(new StoreDoesNotExistValidator( couponRepository, storeRepository, dateProvider));
        return checkNext(coupon);
    }
}
