package nl.tudelft.sem.template.menu.domain.topping;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * A DDD value object representing a topping name in our domain.
 */
@EqualsAndHashCode
public class ToppingName implements Serializable {
    static final long serialVersionUID = 1231332191212312767L;

    @Getter
    private final transient String name;

    public ToppingName(String n) {
        // validate toppingName
        this.name = n;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
