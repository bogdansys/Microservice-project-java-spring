package nl.tudelft.sem.template.menu.model;

import lombok.Data;

import java.util.List;

/**
 * Model representing an add topping request.
 */
@Data
public class AddToppingModel {
    private String name;
    private List<Integer> allergens;
    private double price;
}