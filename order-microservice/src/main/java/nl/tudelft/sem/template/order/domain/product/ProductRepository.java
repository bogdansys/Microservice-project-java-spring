package nl.tudelft.sem.template.order.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A DDD Repository for querying and persisting the Product aggregate root
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "from Product b where b.order.orderId = ?1")
    List<Product> getProductsByOrderId(int orderId);
}
