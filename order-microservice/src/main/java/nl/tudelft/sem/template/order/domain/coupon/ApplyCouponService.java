package nl.tudelft.sem.template.order.domain.coupon;

import nl.tudelft.sem.template.order.domain.coupon.exceptions.*;
import nl.tudelft.sem.template.order.domain.order.*;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class ApplyCouponService {
    private final transient CouponRepository couponRepository;
    private final transient DateProvider dateProvider;
    private final transient OrderRepository orderRepository;

    /**
     * Instantiates apply coupon service
     *
     * @param couponRepository the coupon repository that contains the coupons
     * @param dateProvider     provides current date
     * @param orderRepository database of the orders
     */
    public ApplyCouponService(CouponRepository couponRepository, DateProvider dateProvider,
                              OrderRepository orderRepository){
        this.couponRepository = couponRepository;
        this.dateProvider = dateProvider;
        this.orderRepository = orderRepository;
    }

    /**
     * 1) checks whether the coupon exists
     * 2) checks the expiration date
     * 3) checks the store id
     * 4) based on the coupon type select the strategy
     * 5) compare with price of previous strategy, and select the cheapest strategy
     * @param activationCodeStr activation code of a coupon
     * @return price after modification
     * @throws Exception if the coupon has activation code that is not existing or has coupon expired, then it throws corresponding exception
     */
    public double applyCoupon(String activationCodeStr, int orderId) throws Exception {
        //checks whether the activation code exist
        if(!checkIfActivationCodeExists(activationCodeStr)) {
            throw new ActivationCodeDoesNotExistException(activationCodeStr);
        }

        ActivationCode a = new ActivationCode(activationCodeStr);
        if(couponRepository.findByActivationCode(a).isEmpty()){
            throw new CouponIsEmptyException();
        }
        Coupon c = couponRepository.findByActivationCode(a).orElseThrow();
        LocalDate currentDate = dateProvider.getDate();
        LocalDate expirationDateObject = c.getExpirationDate();
        if(currentDate.isAfter(expirationDateObject)){
            throw new ActivationCodeIsExpiredException(activationCodeStr);
        }
        if(orderRepository.findById(orderId).isEmpty()){
            throw new OrderIsEmptyException();
        }
        Order order = orderRepository.findById(orderId).orElseThrow();
        if(order.getData().getStoreId() != c.getStoreid()
                && c.getStoreid() != -1){ //-1 store ids are valid for all stores
            throw new CouponAppliedAtIncorrectStoreException(activationCodeStr);
        }


        //check whether the order is paid, and if it is paid, a coupon can not be applied
        if(order.getData().getStatus().equals(Status.PAID)){
            throw new OrderIsAlreadyPaidException();
        }
        if(order.getData().getStatus().equals(Status.CANCELLED)){
            throw new OrderCancelledException(order.getOrderId());
        }
        if(c.getCouponType().equals(Coupon.CouponType.DISCOUNT)){
            order.setStrategy(new ApplyDiscountCouponStrategy(c.getDiscountPercent()));
        }
        if(c.getCouponType().equals(Coupon.CouponType.BUYONEGETONE)){
            order.setStrategy(new ApplyBOGOCouponStrategy());
        }

        double newPrice = order.getStrategy().computePrice(order.getProducts().stream().map(Product::getPrice).collect(Collectors.toList()));

        //Note the order's calculatePrice method is not used here because of this conditional
        //if coupon is not cheaper than previous coupon
        if(newPrice >= order.getData().getPrice()) {
            return order.getData().getPrice(); //the new strategy is not saved, so the former coupon state is restored
        }

        order.getData().setMoneySaved(Math.round((//round to 2 decimal places
                order.getData().getPrice() + order.getData().getMoneySaved() - newPrice) * 100.0) / 100.0);  //subtract total price from new Price
        order.getData().setPrice(newPrice);
        orderRepository.save(order);
        return newPrice;
    }

    /**
     * Checks if an activation code is valid
     * @param activationCode the activation code to check the validity of
     * @return boolean is true if activation code is true
     */
    public boolean checkIfActivationCodeExists(String activationCode){
        ActivationCode temp = new ActivationCode(activationCode);
        return couponRepository.existsByActivationCode(temp);
    }
}

