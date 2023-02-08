package nl.tudelft.sem.template.menu.model;

import lombok.Data;

import java.util.List;

/**
 * Model representing a verify request.
 */
@Data
public class VerifyItemModel {
    private List<String> names;
}

