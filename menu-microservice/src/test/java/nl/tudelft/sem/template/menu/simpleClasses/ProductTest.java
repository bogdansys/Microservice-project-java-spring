package nl.tudelft.sem.template.menu.simpleClasses;

import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.product.Product;
import nl.tudelft.sem.template.menu.domain.product.ProductName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

    private Product p;

    @BeforeEach
    void setup() throws Exception {
        ProductName pn = new ProductName("name");
        Price pp = new Price(100.0);
        p = new Product(pn, pp, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void testEquals() throws Exception {
        ProductName pn = new ProductName("name");
        ProductName pn2 = new ProductName("name2");
        Price pp = new Price(1.0);
        Product p2 = new Product(pn, pp, new ArrayList<>(), new ArrayList<>());
        Product p3 = new Product(pn2, pp, new ArrayList<>(), new ArrayList<>());
        assertThat(p2).isEqualTo(p);
        assertThat(p).isNotEqualTo(p3);
    }

    @Test
    void testHashCode() throws Exception {
        ProductName pn = new ProductName("name");
        ProductName pn2 = new ProductName("name2");
        Price pp = new Price(1.0);
        Product p2 = new Product(pn, pp, new ArrayList<>(), new ArrayList<>());
        Product p3 = new Product(pn2, pp, new ArrayList<>(), new ArrayList<>());
        assertThat(p2.hashCode()).isEqualTo(p.hashCode());
        assertThat(p.hashCode()).isNotEqualTo(p3.hashCode());
    }

    @Test
    void testToString() throws Exception {
        ProductName pn = new ProductName("name");
        ProductName pn2 = new ProductName("name2");
        Price pp = new Price(1.0);
        Product p2 = new Product(pn, pp, new ArrayList<>(), new ArrayList<>());
        Product p3 = new Product(pn2, pp, new ArrayList<>(), new ArrayList<>());
        assertThat(p.toString()).isEqualTo(p.toString());
        assertThat(p2.toString()).isNotEqualTo(p.toString());
        assertThat(p.toString()).isNotEqualTo(p3.toString());
    }

    @Test
    void toUserString() throws Exception {
        ProductName pn = new ProductName("name");
        ProductName pn2 = new ProductName("name2");
        Price pp = new Price(1.0);
        Product p2 = new Product(pn, pp, new ArrayList<>(), new ArrayList<>());
        Product p3 = new Product(pn2, pp, new ArrayList<>(), new ArrayList<>());
        assertThat(p.toUserString()).isEqualTo(p.toUserString());
        assertThat(p2.toUserString()).isNotEqualTo(p.toUserString());
        assertThat(p.toUserString()).isNotEqualTo(p3.toUserString());
    }

    @Test
    void getId() {
        assertThat(p.getId()).isEqualTo(0);
    }

    @Test
    void getName() {
        assertThat(p.getName().getName()).isEqualTo("name");
    }

    @Test
    void getToppings() {
        assertThat(p.getToppings()).isNotNull();
    }

    @Test
    void getAllergens() {
        assertThat(p.getAllergens()).isNotNull();
    }

    @Test
    void getPrice() {
        assertThat(p.getPrice().getValue()).isEqualTo(100.0);
    }
}
