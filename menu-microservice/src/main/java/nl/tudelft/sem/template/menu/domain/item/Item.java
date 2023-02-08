package nl.tudelft.sem.template.menu.domain.item;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Contains the information to construct a Product or a Topping.
 * The name, price, warning and its allergens. This class is sent to the Order microservice
 * so that it can construct the products.
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
    public Item(String name, Double price, List<Integer> allergens, Boolean warning) {
        this.name = name;
        this.price = price;
        this.allergens = allergens;
        this.warning = warning;
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
