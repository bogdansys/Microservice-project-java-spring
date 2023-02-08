package nl.tudelft.sem.template.menu.simpleClasses;

import nl.tudelft.sem.template.menu.domain.Price;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {


    @Test
    void testToString() throws Exception {
        Price p = new Price(1.0);
        Price p2 = new Price(1.0);
        Price p3 = new Price(2.0);
        assertThat(p.toString()).isEqualTo(p2.toString());
        assertThat(p.toString()).isNotEqualTo(p3.toString());
    }

    @Test
    void getValue() throws Exception {
        Price p = new Price(1.0);
        Price p2 = new Price(1.0);
        Price p3 = new Price(2.0);
        assertThat(p.getValue()).isEqualTo(p2.getValue());
        assertThat(p.getValue()).isNotEqualTo(p3.getValue());
    }

    @Test
    void testConstructor() {
        assertThatThrownBy(() -> new Price(-10.0));
    }
}