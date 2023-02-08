package nl.tudelft.sem.template.order.model;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponseModel {
    private Long id;
    private transient String name;
    private List<String> toppingNames;
    private double price;
    private List<Integer> allergens;
    private boolean isAllergic;

    public ProductResponseModel() {
    }

    public ProductResponseModel(Long id, String name, List<String> toppingNames, double price, List<Integer> allergens,
                                boolean isAllergic) {
        this.id = id;
        this.name = name;
        this.toppingNames = toppingNames;
        this.price = price;
        this.allergens = allergens;
        this.isAllergic = isAllergic;
    }
}
