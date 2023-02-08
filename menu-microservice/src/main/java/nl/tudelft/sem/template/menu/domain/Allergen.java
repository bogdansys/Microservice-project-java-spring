package nl.tudelft.sem.template.menu.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DDD value object representing an allergen in our domain.
 */
@NoArgsConstructor
public class Allergen implements Serializable {
    static final long serialVersionUID = 1232321122678674L;

    @Getter
    private transient int value;

    public Allergen(int a) throws InvalidAllergenException {
        if (a < 1 || a > 14) {
            throw new InvalidAllergenException(a);
        }
        this.value = a;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allergen allergen = (Allergen) o;
        return value == allergen.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
