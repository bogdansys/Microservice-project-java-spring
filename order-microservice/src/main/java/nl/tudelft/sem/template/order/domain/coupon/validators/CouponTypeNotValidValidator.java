package nl.tudelft.sem.template.order.domain.coupon.validators;

import nl.tudelft.sem.template.order.domain.coupon.Coupon;
import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.CouponTypeNotValidException;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

public class CouponTypeNotValidValidator extends BaseValidator {

    public CouponTypeNotValidValidator(CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        super(couponRepository, storeRepository, dateProvider);
    }

    @Override
    public boolean handle(CouponValidatorRequest coupon) throws Exception {
        //throws exception if coupon type conversion won't work
        if (Coupon.translateToCouponType(coupon.getCouponTypeAsString()).isEmpty()) {
            throw new CouponTypeNotValidException(coupon.getCouponTypeAsString());
        }
        setNext(new DiscountPercentageNotValidValidator(couponRepository, storeRepository, dateProvider));
        return checkNext(coupon);
    }
}
