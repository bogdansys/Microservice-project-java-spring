package nl.tudelft.sem.template.menu.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * A DDD value object representing a product name in our domain.
 */
@EqualsAndHashCode
public class Price implements Serializable {
    static final long serialVersionUID = 123121324112767L;

    @Getter
    private final transient Double value;

    public Price(Double p) throws InvalidPriceException {
        if (p < 0) {
            throw new InvalidPriceException(p);
        }
        this.value = p;
    }

    @Override
    public String toString() {
        if (this.value == null) {
            return null;
        }
        return this.value.toString();
    }
}
