package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.store.StoreName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreNameTest {
    @Test
    void testToString() {
        StoreName storeName1 = new StoreName("name");
        StoreName storeName2 = new StoreName("name");
        StoreName storeName3 = new StoreName("name2");
        StoreName storeName4 = new StoreName(null);
        assertThat(storeName1.toString()).isEqualTo(storeName2.toString());
        assertThat(storeName1.toString()).isNotEqualTo(storeName3.toString());
        assertThat(storeName1.toString()).isNotEqualTo(storeName4.toString());
    }

    @Test
    void getName() {
        StoreName storeName1 = new StoreName("name");
        StoreName storeName2 = new StoreName("name");
        StoreName storeName3 = new StoreName("name2");
        StoreName storeName4 = new StoreName(null);
        assertThat(storeName1.getName()).isEqualTo(storeName2.getName());
        assertThat(storeName1.getName()).isNotEqualTo(storeName3.getName());
        assertThat(storeName1.getName()).isNotEqualTo(storeName4.getName());
    }
}
