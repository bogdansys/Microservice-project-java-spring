package nl.tudelft.sem.template.menu.simpleClasses;

import nl.tudelft.sem.template.menu.domain.Allergen;
import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.product.Product;
import nl.tudelft.sem.template.menu.domain.product.ProductName;
import nl.tudelft.sem.template.menu.domain.topping.Topping;
import nl.tudelft.sem.template.menu.domain.topping.ToppingName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ToppingTest {

    private Topping p;

    @BeforeEach
    void setup() throws Exception {
        ToppingName pn = new ToppingName("name");
        Price pp = new Price(100.0);
        p = new Topping(pn, pp, new ArrayList<>());
    }

    @Test
    void testEquals() throws Exception {
        ToppingName pn = new ToppingName("name");
        ToppingName pn2 = new ToppingName("name2");
        Price pp = new Price(1.0);
        Topping p2 = new Topping(pn, pp, new ArrayList<>());
        Topping p3 = new Topping(pn2, pp, new ArrayList<>());
        assertThat(p2).isEqualTo(p);
        assertThat(p).isNotEqualTo(p3);
    }

    @Test
    void testEquals2() throws Exception {
        ToppingName tn1 = new ToppingName("name");
        ProductName pn1 = new ProductName("name");
        Price pp = new Price(1.0);
        Topping t1 = new Topping(tn1, pp, new ArrayList<>());
        Product p1 = new Product(pn1, pp, new ArrayList<>(), new ArrayList<>());
        assertThat(t1).isNotEqualTo(p1);
        assertThat(t1).isEqualTo(t1);
    }

    @Test
    void testHashCode() throws Exception {
        ToppingName pn = new ToppingName("name");
        ToppingName pn2 = new ToppingName("name2");
        Price pp = new Price(1.0);
        Topping p2 = new Topping(pn, pp, new ArrayList<>());
        Topping p3 = new Topping(pn2, pp, new ArrayList<>());
        assertThat(p2.hashCode()).isEqualTo(p.hashCode());
        assertThat(p.hashCode()).isNotEqualTo(p3.hashCode());
    }

    @Test
    void testToString() throws Exception {
        ToppingName pn = new ToppingName("name");
        ToppingName pn2 = new ToppingName("name2");
        Price pp = new Price(1.0);
        Topping p2 = new Topping(pn, pp, new ArrayList<>());
        Topping p3 = new Topping(pn2, pp, new ArrayList<>());
        assertThat(p.toString()).isEqualTo(p.toString());
        assertThat(p2.toString()).isNotEqualTo(p.toString());
        assertThat(p.toString()).isNotEqualTo(p3.toString());
    }

    @Test
    void toUserString() throws Exception {
        ToppingName pn = new ToppingName("name");
        ToppingName pn2 = new ToppingName("name2");
        Price pp = new Price(15.0);
        List<Allergen> l = new ArrayList<>();
        l.add(new Allergen(5));
        Topping p2 = new Topping(pn, pp, l);
        Topping p3 = new Topping(pn2, pp, new ArrayList<>());
        assertThat(p2.toUserString()).isEqualTo("0 name - Allergens: 5; Price: 15.0");
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
    void getAllergens() {
        assertThat(p.getAllergens()).isNotNull();
    }

    @Test
    void getPrice() {
        assertThat(p.getPrice().getValue()).isEqualTo(100.0);
    }
}
