package nl.tudelft.sem.template.order.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A DDD repository for querying and persisting the coupon entity
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    Optional<Coupon> findByActivationCode(ActivationCode activationCode);

    boolean existsByActivationCode(ActivationCode activationCode);

    Optional<Coupon> findById(int couponId);

    boolean existsById(int couponId);
}
