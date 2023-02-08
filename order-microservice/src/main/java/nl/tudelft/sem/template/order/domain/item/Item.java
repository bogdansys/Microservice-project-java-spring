package nl.tudelft.sem.template.order.domain.item;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Contains the information from the Product microservice to construct
 * a Product or a Topping. The name, price and its allergens.
 */
@NoArgsConstructor
public class Item implements Serializable {
    static final long serialVersionUID = 1231731432678674L;

    @Getter
    private String name;

    @Getter
    private List<Integer> allergens;

    @Getter
    private Boolean warning;

    @Getter
    private Double price;

    /**
     * Create new item to be sent to another microservice.
     *
     * @param name The Name for the new product
     * @param price The Price of the product
     * @param allergens The allergens of the product
     */
    public Item(String name, Double price, List<Integer> allergens) {
        this.name = name;
        this.price = price;
        this.allergens = allergens;
    }

    /**
     * Constructor used for tests
     * @param name The Name for the new product
     * @param allergens The allergens of the product
     * @param warning The warning of the product
     * @param price The Price of the product
     */
    public Item(String name, List<Integer> allergens, Boolean warning, Double price) {
        this.name = name;
        this.allergens = allergens;
        this.warning = warning;
        this.price = price;
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
        Item other = (Item) o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "Item{" +
                ", name=" + name +
                ", allergens=" + allergens +
                ", warning=" + warning +
                ", price=" + price +
                '}';
    }
}
