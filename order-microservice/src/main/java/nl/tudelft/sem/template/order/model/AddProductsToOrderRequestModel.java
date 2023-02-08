package nl.tudelft.sem.template.order.model;

import lombok.Data;
import nl.tudelft.sem.template.order.domain.product.BasicProductInfo;

import java.util.List;

@Data
public class AddProductsToOrderRequestModel {
    public int orderId;
    public List<BasicProductInfo> products;
}
