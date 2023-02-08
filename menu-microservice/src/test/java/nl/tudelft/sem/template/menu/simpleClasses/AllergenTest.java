package nl.tudelft.sem.template.menu.simpleClasses;

import nl.tudelft.sem.template.menu.domain.Allergen;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class AllergenTest {

    @Test
    void testConstructor() {
        assertThatThrownBy(() -> new Allergen(0));
        assertThatThrownBy(() -> new Allergen(15));
        assertDoesNotThrow(() -> new Allergen(1));
        assertDoesNotThrow(() -> new Allergen(14));
    }

    @Test
    void testToString() throws Exception {
        Allergen a = new Allergen(5);
        assertThat(a.toString()).isEqualTo("5");
    }

    @Test
    void testEquals() throws Exception {
        Allergen a1 = new Allergen(5);
        Allergen a2 = new Allergen(4);
        assertThat(a1).isEqualTo(a1);
        assertThat(a2).isEqualTo(a2);
        assertThat(a1).isNotEqualTo(a2);
    }

    @Test
    void getValue() throws Exception {
        Allergen a = new Allergen(5);
        assertThat(a.getValue()).isEqualTo(5);
    }
}