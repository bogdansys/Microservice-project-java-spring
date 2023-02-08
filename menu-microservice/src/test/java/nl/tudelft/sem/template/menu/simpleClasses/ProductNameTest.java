package nl.tudelft.sem.template.menu.simpleClasses;

import nl.tudelft.sem.template.menu.domain.product.ProductName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProductNameTest {


    @Test
    void testToString() {
        ProductName p = new ProductName("name");
        ProductName p2 = new ProductName("name");
        ProductName p3 = new ProductName("name2");
        ProductName p4 = new ProductName(null);
        assertThat(p.toString()).isEqualTo(p2.toString());
        assertThat(p.toString()).isNotEqualTo(p3.toString());
        assertThat(p.toString()).isNotEqualTo(p4.toString());
    }

    @Test
    void getName() {
        ProductName p = new ProductName("name");
        ProductName p2 = new ProductName("name");
        ProductName p3 = new ProductName("name2");
        ProductName p4 = new ProductName(null);
        assertThat(p.getName()).isEqualTo(p2.getName());
        assertThat(p.getName()).isNotEqualTo(p3.getName());
        assertThat(p.getName()).isNotEqualTo(p4.getName());
    }
}