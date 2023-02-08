package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.item.Item;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {
    @Test
    void testToString() {
        Item i1 = new Item("itemName", null, null, 10.0);
        assertThat(i1.toString()).isEqualTo("Item{, name=itemName, allergens=null, warning=null, price=10.0}");

        Item i2 = new Item("itemName", 10.0, null);
        assertThat(i2.toString()).isEqualTo("Item{, name=itemName, allergens=null, warning=null, price=10.0}");
    }

    @Test
    void testEquals(){
        Item i = new Item("itemName", null, null, 10.0);
        Item i2 = new Item("itemName", null, null, 10.0);
        String dd = "";

        assertThat(i).isEqualTo(i2);
        assertThat(i).isEqualTo(i);
        assertThat(i).isNotEqualTo(null);
        assertThat(i).isNotEqualTo(dd);
    }

    @Test
    void testHashCode() {
        Item i = new Item("itemName", null, null, 10.0);
        Item i2 = new Item("differentName", null, null, 10.0);

        assertThat(i.hashCode()).isEqualTo(i.hashCode());
        assertThat(i2.hashCode()).isNotEqualTo(i.hashCode());
    }

    @Test
    void testGetters() {
        Item i = new Item("itemName", null, null, 10.0);

        assertThat(i.getName()).isEqualTo("itemName");
        assertThat(i.getAllergens()).isNull();
        assertThat(i.getWarning()).isNull();
        assertThat(i.getPrice()).isEqualTo(10.0);
    }
}
