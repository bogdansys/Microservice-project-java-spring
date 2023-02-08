package nl.tudelft.sem.template.menu.domain.product;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * A DDD value object representing a product name in our domain.
 */
@EqualsAndHashCode
public class ProductName implements Serializable {
    static final long serialVersionUID = 123124324112767L;

    @Getter
    private final transient String name;

    public ProductName(String n) {
        // validate productName
        this.name = n;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
