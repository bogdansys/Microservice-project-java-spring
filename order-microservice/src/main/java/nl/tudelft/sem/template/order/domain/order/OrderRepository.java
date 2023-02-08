package nl.tudelft.sem.template.order.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A DDD Repository for querying and persisting the Order aggregate root
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Find all orders by customer id.
     *
     * @param customerId
     * @return
     */

    default List<Order> findAllByCustomerId(String customerId) {
        List<Order> orders = this.findAll();
        return orders.stream().filter(order -> order.getData().getCustomerId().equals(customerId)).collect(Collectors.toList());
    }

    default List<Order> findAllByStoreId(int storeId) {
        List<Order> orders = this.findAll();
        return orders.stream().filter(order -> order.getData().getStoreId() == storeId).collect(Collectors.toList());
    }

    default Optional<Order> findByOrderId(int orderId) {
        List<Order> orders = this.findAll();
        return orders.stream().filter(order -> order.getOrderId() == orderId).findFirst();
    }

}
