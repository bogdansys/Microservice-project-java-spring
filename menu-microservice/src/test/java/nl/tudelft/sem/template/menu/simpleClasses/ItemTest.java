package nl.tudelft.sem.template.menu.simpleClasses;

import nl.tudelft.sem.template.menu.domain.item.Item;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {


    @Test
    void testToString() {
        Item i = new Item("itemName", 10.0, null, null);
        assertThat(i.toString()).isEqualTo("Item{, name=itemName, allergens=null, warning=null, price=10.0}");
    }

    @Test
    void testEquals(){
        Item i = new Item("itemName", 10.0, null, null);
        Item i2 = new Item("itemName", 10.0, null, null);
        assertThat(i).isEqualTo(i2);
        assertThat(i).isEqualTo(i);
        assertThat(i).isNotEqualTo(null);
    }
}