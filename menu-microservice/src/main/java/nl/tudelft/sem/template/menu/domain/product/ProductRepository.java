package nl.tudelft.sem.template.menu.domain.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting product aggregate roots.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    /**
     * Find product by productName.
     */
    Optional<Product> findByName(ProductName name);

    /**
     * Find product by id.
     */
    Optional<Product> findById(Integer id);

    /**
     * Check if an existing product with name.
     */
    boolean existsByName(ProductName name);

    /**
     * Check if an existing product with id.
     */
    boolean existsById(Integer id);
}
