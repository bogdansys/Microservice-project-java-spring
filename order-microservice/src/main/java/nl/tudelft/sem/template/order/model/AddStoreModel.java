package nl.tudelft.sem.template.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing an add store request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddStoreModel {
    private String name;
}
