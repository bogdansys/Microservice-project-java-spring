package nl.tudelft.sem.template.menu.domain.topping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A DDD repository for quering and persisting topping aggregate roots.
 */
@Repository
public interface ToppingRepository extends JpaRepository<Topping, Integer> {
    /**
     * Find topping by toppingName.
     */
    Optional<Topping> findByName(ToppingName name);

    /**
     * Find topping by id.
     */
    Optional<Topping> findById(Integer id);

    /**
     * Check if an existing topping with name.
     */
    boolean existsByName(ToppingName name);

    /**
     * Check if an existing topping with id.
     */
    boolean existsById(Integer id);
}
