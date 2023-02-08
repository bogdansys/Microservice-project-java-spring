package nl.tudelft.sem.template.order.model;

import lombok.Data;

import java.util.List;

/**
 * Model representing a verify request.
 */
@Data
public class VerifyItemModel {
    private List<String> names;

    public VerifyItemModel(List<String> names) {
        this.names = names;
    }
}
