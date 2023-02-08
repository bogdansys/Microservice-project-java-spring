package nl.tudelft.sem.template.order.domain.coupon.validators;

import nl.tudelft.sem.template.order.domain.coupon.Coupon;
import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.DiscountPercentageNotValidException;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;

public class DiscountPercentageNotValidValidator extends BaseValidator {

    public DiscountPercentageNotValidValidator(CouponRepository couponRepository, StoreRepository storeRepository, DateProvider dateProvider) {
        super(couponRepository, storeRepository, dateProvider);
    }

    @Override
    public boolean handle(CouponValidatorRequest coupon) throws Exception {

        Coupon.CouponType enumeratedCouponType = Coupon.translateToCouponType(coupon.getCouponType()).get();

        //throws exception if coupon type is discount and discount is invalid
        if (enumeratedCouponType == Coupon.CouponType.DISCOUNT &&
                (coupon.getDiscountPercent() <= 0.0 || coupon.getDiscountPercent() > 100.0)) {
            throw new DiscountPercentageNotValidException(coupon.getDiscountPercent());
        }

        if (enumeratedCouponType == Coupon.CouponType.BUYONEGETONE) coupon.setDiscountPercent(-1);

        setNext(new ExpirationDateNotValidValidator(couponRepository, storeRepository, dateProvider));
        return checkNext(coupon);
    }

}
