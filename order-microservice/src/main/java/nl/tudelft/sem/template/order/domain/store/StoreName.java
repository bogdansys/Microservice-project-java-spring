package nl.tudelft.sem.template.order.domain.store;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * A DDD value object representing a store name in our domain.
 */
@EqualsAndHashCode
public class StoreName implements Serializable {
    static final long serialVersionUID = 12312312321L;

    @Getter
    private final transient String name;

    public StoreName(String n) {
        // validate storeName
        this.name = n;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
