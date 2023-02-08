package nl.tudelft.sem.template.order.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.order.domain.coupon.exceptions.CanNotApplyBOGOCouponWithSingleProductException;
import nl.tudelft.sem.template.order.domain.coupon.PricingStrategy;
import nl.tudelft.sem.template.order.domain.product.Product;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Order class.
 */
@SuppressWarnings("PMD")
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    // As a customer can make multiple orders to one store, we need to store an id (primary key)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "orderId", nullable = false)
    @Setter
    @Getter
    private int orderId;
    /**
     * The type of the strategy that was applied to this order's price.
     * Useful for when recomupting the price.
     */
    @JsonIgnore
    @Column(name = "strategy")
    @Convert(converter = OrderStrategyConverter.class)
    @Getter
    @Setter
    private PricingStrategy strategy;

    @OneToMany(targetEntity = Product.class, mappedBy = "order",
            cascade = CascadeType.ALL)
    // https://www.baeldung.com/hibernate-lazy-eager-loading
    @Getter
    @Setter
    private List<Product> products = new LinkedList<>();


    @Column(name = "data")
    @Convert(converter = OrderDataConverter.class)
    @Getter
    private OrderData data;


    /**
     * Creates a new order.
     *
     * @param customerId The id of the customer making the order
     * @param storeId    The id of the store to which the order was sent
     */
    public Order(String customerId, int storeId) {
        this.data = new OrderData(customerId, storeId, Status.UNPAID, "", 0.0, 0.0);
        strategy = new BasicPricingStrategy();
    }

    public Order() {
        this.data = new OrderData(null, 0, Status.UNPAID, "", 0.0, 0.0);
        strategy = new BasicPricingStrategy();
    }


    /**
     * Creates a new order, providing additionally a list of products.
     *
     * @param customerId The id of the customer making the order
     * @param storeId    The id of the store to which the order was sent
     */
    public Order(String customerId, int storeId, List<Product> products) {
        this.data = new OrderData(customerId, storeId, Status.UNPAID, null, 0.0, 0.0);
        this.products = products;
        strategy = new BasicPricingStrategy();
        Stream<Double> stream = products.stream().map(Product::getPrice);
        try {
            data.setPrice(strategy.computePrice(stream.collect(Collectors.toList())));
        } catch (CanNotApplyBOGOCouponWithSingleProductException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * equals method.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return this.getOrderId() == order.getOrderId();
    }
}
