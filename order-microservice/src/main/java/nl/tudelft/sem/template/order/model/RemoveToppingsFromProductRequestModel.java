package nl.tudelft.sem.template.order.model;

import lombok.Data;

import java.util.List;

@Data
public class RemoveToppingsFromProductRequestModel {
    Long productId;
    List<String> toppings;
}
