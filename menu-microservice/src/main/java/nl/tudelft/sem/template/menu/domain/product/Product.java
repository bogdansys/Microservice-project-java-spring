package nl.tudelft.sem.template.menu.domain.product;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.menu.domain.Allergen;
import nl.tudelft.sem.template.menu.domain.AllergenAttributeConverter;
import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.PriceAttributeConverter;

/**
 * A DDD entity representing a product such as Pizza
 */
@Entity
@Table(name = "products")
@NoArgsConstructor
public class Product implements Serializable {
    static final long serialVersionUID = 1231231432678674L;

    /**
     * Identifier for the product.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @Getter
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    @Convert(converter = ProductNameAttributeConverter.class)
    @Getter
    private ProductName name;

    @Column(name = "toppings")
    @ElementCollection(targetClass = Integer.class)
    @Getter
    private List<Integer> toppings;

    @Column(name = "allergens")
    @ElementCollection(targetClass = Allergen.class)
    @Convert(converter = AllergenAttributeConverter.class)
    @Getter
    private List<Allergen> allergens;

    @Column(name = "price", nullable = false)
    @Convert(converter = PriceAttributeConverter.class)
    @Getter
    private Price price;

    /**
     * Create new product.
     *
     * @param name The Name for the new product
     * @param price The Price of the product
     * @param allergens The allergens of the product
     * @param toppings The toppings of the product
     */
    public Product(ProductName name, Price price, List<Allergen> allergens, List<Integer> toppings) {
        this.name = name;
        this.price = price;
        this.allergens = allergens;
        this.toppings = toppings;
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
        Product other = (Product) o;
        if (name == null || other.name == null) {
            return false;
        }
        return name.getName().equals((other.name.getName()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name.getName());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name=" + name +
                ", toppings=" + toppings +
                ", allergens=" + allergens.stream().map(a -> a.getValue()).collect(Collectors.toList()) +
                ", price=" + price +
                '}';
    }

    public String toUserString() {
        String toppings = this.toppings.size() == 0 ? "Toppings: none" : "Toppings: " + this.toppings.toString().replace("[", "").replace("]", "");
        String allergens = this.allergens.size() == 0 ?
                "Allergens: none" :
                "Allergens: " + this.allergens
                        .stream().map(a -> a.getValue())
                        .collect(Collectors.toList())
                        .toString()
                        .replace("[", "")
                        .replace("]", "");

        return this.id + " " + this.name + " - " + toppings + "; " + allergens + "; " + "Price: " + this.price.getValue();
    }
}