package nl.tudelft.sem.template.authentication.domain.user;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AllergensTests {

    @Test
    void testGetAllergens(){
        Allergens allergens = new Allergens("1,5,7,2");
        List<Integer> testAllergens = new ArrayList<>();
        testAllergens.add(1);
        testAllergens.add(5);
        testAllergens.add(7);
        testAllergens.add(2);
        assertThat(allergens.getAllergens()).hasSameElementsAs(testAllergens);
        assertThat(allergens.getAllergens().size()).isEqualTo(testAllergens.size());
    }

    @Test
    void testNoArgsConstructor(){
        Allergens allergens = new Allergens();
        assertThat(allergens.getAllergens()).isEmpty();
    }

    @Test
    void testNullAllergensConstructor(){
        Allergens allergens = new Allergens(null);
        assertThat(allergens.getAllergens()).isEmpty();
    }

    @Test
    void testAllergensConstructor(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.getAllergens()).hasSize(4);
        assertThat(allergens.getAllergens()).contains(1);
        assertThat(allergens.getAllergens()).contains(5);
        assertThat(allergens.getAllergens()).contains(7);
        assertThat(allergens.getAllergens()).contains(2);
    }

    @Test
    void testAddAllergenNotAlreadyThere(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.getAllergens()).hasSize(4);
        allergens.addAllergen(6);
        assertThat(allergens.getAllergens()).hasSize(5);
        assertThat(allergens.getAllergens()).contains(6);
    }

    @Test
    void testAddAllergenAlreadyThere(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.getAllergens()).hasSize(4);
        allergens.addAllergen(7);
        assertThat(allergens.getAllergens()).hasSize(4);
    }

    @Test
    void testRemoveAllergenExists(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.getAllergens()).hasSize(4);
        allergens.removeAllergen(1);
        assertThat(allergens.getAllergens()).hasSize(3);
        assertThat(allergens.getAllergens()).doesNotContain(1);
    }

    @Test
    void testRemoveAllergenDoesntExists(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.getAllergens()).hasSize(4);
        allergens.removeAllergen(3);
        assertThat(allergens.getAllergens()).hasSize(4);
        assertThat(allergens.getAllergens()).doesNotContain(3);
    }

    @Test
    void testContainsAllergenHasAllergen(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.contains(1)).isTrue();
    }

    @Test
    void testContainsAllergenDoesntHaveAllergen(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.contains(3)).isFalse();
    }

    @Test
    void testHasAllergenTrue(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.hasAllergen(1)).isTrue();
    }

    @Test
    void testHasAllergenFalse(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.hasAllergen(3)).isFalse();
    }

    @Test
    void testAllergenSizeZero(){
        Allergens allergens = new Allergens();
        assertThat(allergens.size()).isZero();
    }

    @Test
    void testAllergenSizeNonZero(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.size()).isEqualTo(4);
    }

    @Test
    void testAllergenToStringNone(){
        Allergens allergens = new Allergens();
        assertThat(allergens.toString()).isEqualTo("");
    }

    @Test
    void testAllergenToStringFull(){
        Allergens allergens = new Allergens("1,5,7,2");
        assertThat(allergens.toString()).isEqualTo("1,5,7,2,");
    }
}
