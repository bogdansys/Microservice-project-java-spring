package nl.tudelft.sem.template.menu.model;

import lombok.Data;

import java.util.List;

/**
 * Model representing an add product request.
 */
@Data
public class AddProductModel {
    private String name;
    private double price;
    private List<Integer> allergens;
    private List<Integer> toppings;
}