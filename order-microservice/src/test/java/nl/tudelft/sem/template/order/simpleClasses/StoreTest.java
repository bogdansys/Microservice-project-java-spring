package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.store.Store;
import nl.tudelft.sem.template.order.domain.store.StoreName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreTest {
    private Store store;

    @BeforeEach
    void setup() {
        StoreName storeName = new StoreName("name");
        store = new Store(storeName);
    }

    @Test
    void testHashCode() {
        StoreName storeName = new StoreName("name");
        Store store1 = new Store(storeName);

        int expected = Objects.hash(storeName);

        assertThat(store1.hashCode()).isEqualTo(store.hashCode());
        assertThat(store1.hashCode()).isEqualTo(expected);
    }

    @Test
    void testToString() {
        StoreName storeName = new StoreName("name");
        StoreName storeName1 = new StoreName("name2");
        Store store2 = new Store(storeName);
        Store store1 = new Store(storeName1);
        assertThat(store.toString()).isEqualTo(store2.toString());
        assertThat(store1.toString()).isNotEqualTo(store.toString());
    }

    @Test
    void testEquals() {
        StoreName storeName = new StoreName("name");
        StoreName storeName1 = new StoreName("name2");
        Store store2 = new Store(storeName);
        Store store1 = new Store(storeName1);

        String store3 = "";

        store.setId(0);
        store2.setId(1);
        store1.setId(0);

        assertThat(store).isEqualTo(store);
        assertThat(store1).isEqualTo(store);
        assertThat(store2).isNotEqualTo(store);
        assertThat(store1).isNotEqualTo(store3);
    }

    @Test
    void getStoreName() {
        assertThat(store.getName().getName()).isEqualTo("name");
    }
}
