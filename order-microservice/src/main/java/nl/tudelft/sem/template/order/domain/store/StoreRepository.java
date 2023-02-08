package nl.tudelft.sem.template.order.domain.store;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting store aggregate roots.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    /**
     * Find store by storeName.
     */
    Optional<Store> findByName(StoreName name);

    /**
     * Check if an existing store with name.
     */
    boolean existsByName(StoreName name);

    /**
     * Check if there is a store with the input id
     */
    boolean existsById(int storeId);
}
