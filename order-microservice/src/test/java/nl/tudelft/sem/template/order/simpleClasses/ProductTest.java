package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.product.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {
    @Test
    public void basicProductCreation() {
        List<String> toppings = List.of("t1");
        List<Integer> allergens = List.of(2);
        Product product = new Product("testName", toppings, 34.0, allergens, null, false);

        assertThat(product.isAllergic()).isFalse();
        assertThat(product.getName()).isEqualTo("testName");
        assertThat(product.getPrice()).isEqualTo(34.0);
        assertThat(product.getToppingNames()).isEqualTo(toppings);
        assertThat(product.getAllergens()).isEqualTo(allergens);

        assertThat(product.toString()).isEqualTo("null; testName; Toppings: t1; Allergens: 2; Price: 34.0");
    }

    @Test
    public void testEquality() {
        List<String> toppings = List.of("t1");
        List<Integer> allergens = List.of(2);
        Product product1 = new Product("testName", toppings, 34.0, allergens, null, false);

        Product product2 = new Product("testName", toppings, 34.0, allergens, null, false);

        assertThat(product1).isEqualTo(product1);
        assertThat(product2).isEqualTo(product1);

        product1.setId(0L);
        product2.setId(1L);
        assertThat(product2).isNotEqualTo(product1);

        Product product3 = new Product("differentName", null, 4.0, null, null, true);
        product3.setId(0L);
        assertThat(product1).isEqualTo(product3);
    }

    @Test
    public void testHasCode() {
        List<String> toppings = List.of("t1");
        List<Integer> allergens = List.of(2);
        Product product1 = new Product("testName", toppings, 34.0, allergens, null, false);

        Product product2 = new Product("testName", toppings, 34.0, allergens, null, false);

        product1.setId(0L);
        product2.setId(1L);

        assertThat(product1.hashCode()).isNotEqualTo(product2.hashCode());

        Product product3 = new Product("differentName", null, 4.0, null, null, true);
        product3.setId(0L);
        assertThat(product3.hashCode()).isEqualTo(product1.hashCode());
    }
}
