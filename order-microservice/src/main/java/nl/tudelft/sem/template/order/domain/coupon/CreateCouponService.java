package nl.tudelft.sem.template.order.domain.coupon;

import nl.tudelft.sem.template.order.domain.coupon.exceptions.*;
import nl.tudelft.sem.template.order.domain.coupon.validators.ActivationCodeIsNotValidValidator;
import nl.tudelft.sem.template.order.domain.coupon.validators.CouponValidatorRequest;
import nl.tudelft.sem.template.order.domain.coupon.validators.Validator;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("PMD")

@Service
public class CreateCouponService {

    private final transient CouponRepository couponRepository;
    private final transient StoreRepository storeRepository;
    private final transient DateProvider dateProvider;

    /**
     * Instantiates new create coupon service
     *
     * @param couponRepository the coupon repository that contains the coupons
     * @param dateProvider     provides current date
     */
    public CreateCouponService(CouponRepository couponRepository, StoreRepository storeRepository,
                               DateProvider dateProvider) {
        this.couponRepository = couponRepository;
        this.storeRepository = storeRepository;
        this.dateProvider = dateProvider;
    }

    /**
     * Adds coupon to the coupon repository if the coupon input is valid
     *
     * @param activationCode  activation code of coupon
     * @param couponType      String representing the coupon type (validity is checked in the service)
     * @param discountPercent discount percentage of discount coupon
     * @param expirationDate  expiration date of the coupon
     * @param storeid         the store id that the coupon is associated to (note: a value of -1 is valid
     *                        since it means that the coupon can be applied to an order from any store)
     * @return if coupon successfully created, the coupon created is returned
     * @throws Exception if any of the coupon fields are invalid
     */
    public Coupon addCoupon(ActivationCode activationCode, String couponType, double discountPercent,
                            String expirationDate, int storeid) throws Exception {

        CouponValidatorRequest couponValidatorRequest = new CouponValidatorRequest(activationCode,
                couponType, discountPercent, expirationDate, storeid);
        //start the chain of responsibility pattern to validate the coupon
        Validator handler = new ActivationCodeIsNotValidValidator(couponRepository, storeRepository, dateProvider);

        boolean isValid = handler.handle(couponValidatorRequest); //start the chain of responsibility pattern to validate the coupon
        if (isValid) {
            LocalDate expirationDateObject = LocalDate.parse(expirationDate);
            Coupon.CouponType enumeratedCouponType = Coupon.translateToCouponType(couponType).get();
            //discount should be -1 if the type is BUYONEGETONE
            double discount = (enumeratedCouponType == Coupon.CouponType.BUYONEGETONE) ? -1 : discountPercent;
            Coupon coupon = new Coupon(activationCode, enumeratedCouponType, discount, expirationDateObject, storeid);
            couponRepository.save(coupon);
            return coupon;
        }

        return null;
    }

    /**
     * Lists all coupons stored in coupon database
     *
     * @return the list of coupons in the database
     */
    public List<Coupon> listAllCoupons() {
        return couponRepository.findAll();
    }

    public void deleteCouponById(int couponId) throws CouponDoesNotExistException {
        if (!couponRepository.existsById(couponId)) {
            throw new CouponDoesNotExistException(couponId);
        }
        Coupon coupon = couponRepository.findById(couponId).get();
        couponRepository.delete(coupon);
    }
}
