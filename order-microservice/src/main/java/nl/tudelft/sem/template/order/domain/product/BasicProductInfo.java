package nl.tudelft.sem.template.order.domain.product;

import lombok.Data;

import java.util.List;

@Data
public class BasicProductInfo {
    String name;

    List<String> toppings;

    public BasicProductInfo() {
    }

    /**
     * Constructor for tests
     * @param name the name of the product
     * @param toppings the topping names for the product
     */
    public BasicProductInfo(String name, List<String> toppings) {
        this.name = name;
        this.toppings = toppings;
    }
}
