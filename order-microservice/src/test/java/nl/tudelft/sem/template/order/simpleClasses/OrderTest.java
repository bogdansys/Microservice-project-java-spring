package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.coupon.ApplyBOGOCouponStrategy;
import nl.tudelft.sem.template.order.domain.coupon.ApplyDiscountCouponStrategy;
import nl.tudelft.sem.template.order.domain.order.*;
import nl.tudelft.sem.template.order.domain.product.Product;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Order test.
 */
public class OrderTest {

    @Test
    public void testAddingProducts() {
        String customerId = "testId";
        int storeId = 5;

        Order order = new Order(customerId, storeId);

        // First, we test that adding products to order works

        Product product = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                order,
                false);
        product.setId(0L);

        Product noName = new Product();

        assertTrue(OrderUtils.addProduct(product, order));
        assertFalse(OrderUtils.addProduct(product, order));
        assertFalse(OrderUtils.addProduct(null, order));
        assertFalse(OrderUtils.addProduct(noName, order));
        assertFalse(OrderUtils.addProduct(null, null));

        List<Product> products = new ArrayList<>();

        assertFalse(OrderUtils.addProducts(null, null));
        assertFalse(OrderUtils.addProducts(products, null));
        assertFalse(OrderUtils.addProducts(null, order));
        assertFalse(OrderUtils.addProducts(products, order));

        products.add(product);

        assertTrue(OrderUtils.addProducts(products, order));
        assertFalse(OrderUtils.addProducts(products, null));


    }

    @Test
    public void testDeleteProduct(){

        String customerId = "testId";
        int storeId = 5;

        Order order = new Order(customerId, storeId);

        // First, we test that adding products to order works

        Product product = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                order,
                false);
        product.setId(0L);
        Product noName = new Product();
        assertTrue (OrderUtils.addProduct(product, order));

        assertFalse(OrderUtils.deleteProduct(null, null));
        assertFalse(OrderUtils.deleteProduct(product, null));
        assertFalse(OrderUtils.deleteProduct(null, order));
        assertFalse(OrderUtils.deleteProduct(noName, order));
        assertTrue(OrderUtils.deleteProduct(product, order));
        assertFalse(OrderUtils.deleteProduct(product, order));

        List<Product> products = new ArrayList<>();
        products.add(product);
        assertTrue(order.getProducts().isEmpty());
        assertFalse(OrderUtils.deleteProducts(products, order));
        assertTrue(OrderUtils.addProducts(products, order));
        assertFalse(order.getProducts().isEmpty());
        assertFalse(OrderUtils.deleteProducts(null, order));
        assertFalse(OrderUtils.deleteProducts(products, null));
        assertFalse(OrderUtils.deleteProducts(null, null));
        assertTrue(OrderUtils.deleteProducts(products, order));

    }

    /**
     * Test order creation.
     */
    @Test
    void testOrderCreation() {
        String customerId = "testId";
        int storeId = 5;

        // first way of creating an order

        Order order = new Order(customerId, storeId);

        // Check order was created as expected

        assertThat(order.getData().getStoreId()).isEqualTo(storeId);
        assertThat(order.getData().getCustomerId()).isEqualTo(customerId);
        assertThat(order.getProducts().size()).isEqualTo(0);
        assertThat(order.getData().getPrice()).isEqualTo(0L);
        assertThat(order.getData().getStatus()).isEqualTo(Status.UNPAID);

        Order equivalent = new Order(customerId, storeId);

        assertThat(order).isEqualTo(equivalent);

        // second way of creating an order

        List<Product> products = List.of(new Product());
        order = new Order(customerId, storeId, products);

        // Check order was created as expected

        assertThat(order.getData().getStoreId()).isEqualTo(storeId);
        assertThat(order.getData().getCustomerId()).isEqualTo(customerId);
        assertThat(order.getProducts().size()).isEqualTo(1);
        assertThat(order.getData().getPrice()).isEqualTo(0L);
        assertThat(order.getData().getStatus()).isEqualTo(Status.UNPAID);

        equivalent = new Order(customerId, storeId);

        assertThat(order).isEqualTo(equivalent);
    }

    /**
     * Test order products.
     */
    @Test
    void testOrderProducts() {
        String customerId = "testId";
        int storeId = 5;

        Order order = new Order(customerId, storeId);

        // First, we test that adding products to order works

        Product product = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                order,
                false);
        product.setId(0L);

        assertTrue(OrderUtils.addProducts(List.of(product, product), order));
        // Check there are no duplicates
        assertThat(order.getProducts().size()).isEqualTo(1);
        // Check price has been correctly updated
        assertThat(order.getData().getPrice()).isEqualTo(1);

        Product otherProduct = new Product(
                "Margherita",
                List.of(),
                3.0,
                List.of(1, 2),
                order,
                false);
        otherProduct.setId(1L);

        assertTrue(OrderUtils.addProduct(otherProduct, order));
        // Check product list and price have been updated
        assertThat(order.getProducts().size()).isEqualTo(2);
        assertThat(order.getData().getPrice()).isEqualTo(4);

        // Check trying to delete a product not in order does nothing
        Product notInOrderProduct = new Product();
        assertFalse(OrderUtils.deleteProduct(notInOrderProduct, order));
        assertFalse(OrderUtils.addProduct(notInOrderProduct, order));
        assertThat(order.getProducts().size()).isEqualTo(2);

        // Check deleting a product in order updates the corresponding variables
        assertTrue(OrderUtils.deleteProducts(List.of(otherProduct, otherProduct), order));
        assertThat(order.getProducts().size()).isEqualTo(1);
        assertThat(order.getData().getPrice()).isEqualTo(1);
    }

    /**
     * Test equals() method of Order.
     */
    @Test
    void testEquals() {
        String customerId = "testId";
        int storeId = 5;

        Order order1 = new Order(customerId, storeId);
        order1.setOrderId(0);

        assertThat(order1).isEqualTo(order1);

        Order order2 = new Order(customerId, storeId);
        order2.setOrderId(1);

        assertThat(order1).isNotEqualTo(order2);
    }

    /**
     * Test calculating order price, using different strategies.
     */
    @Test
    void testCalculatePriceStrategies() {
        String customerId = "testId";
        int storeId = 5;
        Order order = new Order(customerId, storeId);
        Product product = new Product(
                "Margherita",
                List.of(),
                3.0,
                List.of(1, 2),
                order,
                false);
        product.setId(1L);
        assertTrue(OrderUtils.addProduct(product, order));

        Product product2 = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                order,
                false);
        product2.setId(0L);

        assertTrue(OrderUtils.addProduct(product2, order));

        // First strategy
        order.setStrategy(new BasicPricingStrategy());
        // no change
        assertThat(OrderUtils.calculatePrice(order)).isEqualTo(4.0d);

        assertThat(order.getStrategy().toString()).isEqualTo("BasicPricingStrategy");

        order.setStrategy(new ApplyBOGOCouponStrategy());
        assertThat(OrderUtils.calculatePrice(order)).isEqualTo(3.0d);
        assertThat(order.getStrategy().toString()).isEqualTo("ApplyBOGOCouponStrategy");

        order.setStrategy(new ApplyDiscountCouponStrategy(20));
        // we apply a 0.2 discount -> 0.8 * 4 = 3.2
        assertThat(OrderUtils.calculatePrice(order)).isEqualTo(3.2d);
        assertThat(order.getStrategy().toString()).isEqualTo("ApplyDiscountCouponStrategy, 20.0");

        // only one product in order will result in an exception when using BOGO Coupon Strategy
        assertTrue(OrderUtils.deleteProduct(product, order));
        order.setStrategy(new ApplyBOGOCouponStrategy());
        //assertThatThrownBy(OrderUtils.calculatePrice(order)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testMoneySaved() {
        String customerId = "testId";
        int storeId = 5;
        Order order = new Order(customerId, storeId);
        Product product = new Product(
                "Margherita",
                List.of(),
                3.0,
                List.of(1, 2),
                order,
                false);
        product.setId(1L);
        assertTrue(OrderUtils.addProduct(product, order));

        Product product2 = new Product(
                "Margherita",
                List.of(),
                1.0,
                List.of(1, 2),
                order,
                false);
        product2.setId(0L);

        assertTrue(OrderUtils.addProduct(product2, order));
        // First strategy
        order.setStrategy(new BasicPricingStrategy());
        // no change
        assertThat(OrderUtils.calculatePrice(order)).isEqualTo(4.0d);

        assertThat(order.getData().getMoneySaved()).isEqualTo(0.0d);

        order.setStrategy(new ApplyBOGOCouponStrategy());

        assertThat(OrderUtils.calculatePrice(order)).isEqualTo(3.0d);

        assertThat(order.getData().getMoneySaved()).isEqualTo(1.0d);

        OrderData data = order.getData();  // mutant killed

    }

}
