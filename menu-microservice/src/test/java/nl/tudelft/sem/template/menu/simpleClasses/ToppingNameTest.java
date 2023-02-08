package nl.tudelft.sem.template.menu.simpleClasses;

import nl.tudelft.sem.template.menu.domain.topping.ToppingName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToppingNameTest {


    @Test
    void testToString() {
        ToppingName p = new ToppingName("name");
        ToppingName p2 = new ToppingName("name");
        ToppingName p3 = new ToppingName("name2");
        ToppingName p4 = new ToppingName(null);
        assertThat(p.toString()).isEqualTo(p2.toString());
        assertThat(p.toString()).isNotEqualTo(p3.toString());
        assertThat(p.toString()).isNotEqualTo(p4.toString());
    }

    @Test
    void getName() {
        ToppingName p = new ToppingName("name");
        ToppingName p2 = new ToppingName("name");
        ToppingName p3 = new ToppingName("name2");
        ToppingName p4 = new ToppingName(null);
        assertThat(p.getName()).isEqualTo(p2.getName());
        assertThat(p.getName()).isNotEqualTo(p3.getName());
        assertThat(p.getName()).isNotEqualTo(p4.getName());
    }
}