package nl.tudelft.sem.template.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.order.domain.product.BasicProductInfo;

import java.util.List;

/**
 * Basic model for making an order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOrderModel {
    public int storeId;

    // Product names of the order
    // key -> product name
    // value -> List of topping names of the product
    public List<BasicProductInfo> products;
}
