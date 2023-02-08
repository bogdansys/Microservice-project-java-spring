package nl.tudelft.sem.template.menu.domain.topping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.menu.domain.Allergen;
import nl.tudelft.sem.template.menu.domain.AllergenAttributeConverter;
import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.PriceAttributeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A DDD entity representing a topping
 */
@Entity
@Table(name = "toppings")
@NoArgsConstructor
public class Topping implements Serializable {
    static final long serialVersionUID = 123132321122678674L;

    /**
     * Identifier for the topping.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @Getter
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    @Convert(converter = ToppingNameAttributeConverter.class)
    @Getter
    private ToppingName name;

    @Column(name = "price", nullable = false)
    @Convert(converter = PriceAttributeConverter.class)
    @Getter
    private Price price;

    @Column(name = "allergens")
    @ElementCollection(targetClass = Allergen.class)
    @Convert(converter = AllergenAttributeConverter.class)
    @Getter
    private List<Allergen> allergens;


    /**
     * Create new topping.
     *
     * @param name The Name for the new topping
     * @param price The Price for the new topping
     * @param allergens The Allergens of the new topping
     */
    public Topping(ToppingName name, Price price, List<Allergen> allergens) {
        this.name = name;
        this.price = price;
        this.allergens = allergens;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Topping other = (Topping) o;
        if (name == null || other.name == null) return false;
        return name.getName().equals(other.name.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name.getName());
    }

    @Override
    public String toString() {
        return "Topping{" +
                "id=" + id +
                ", name=" + name +
                ", price=" + price +
                ", allergens=" + allergens.stream().map(a -> a.getValue()).collect(Collectors.toList()) +
                '}';
    }

    public String toUserString() {
        String allergens = this.allergens.size() == 0 ?
                "Allergens: none" :
                "Allergens: " + this.allergens
                        .stream().map(a -> a.getValue())
                        .collect(Collectors.toList())
                        .toString()
                        .replace("[", "")
                        .replace("]", "");

        return this.id + " " + this.name + " - " + allergens + "; " + "Price: " + this.price.getValue();
    }
}