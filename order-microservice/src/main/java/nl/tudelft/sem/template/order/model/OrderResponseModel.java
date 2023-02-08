package nl.tudelft.sem.template.order.model;

import lombok.Data;
import nl.tudelft.sem.template.order.domain.order.Status;

import java.util.LinkedList;
import java.util.List;

@Data
public class OrderResponseModel {
    private int orderId;
    private String customerId;
    private int storeId;
    private List<ProductResponseModel> products = new LinkedList<>();
    private Status status;
    private String deliveryTime;
    private double price;

    public OrderResponseModel() {
    }

    public OrderResponseModel(int orderId, String customerId, int storeId, List<ProductResponseModel> products,
                              Status status, String deliveryTime, double price) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.products = products;
        this.status = status;
        this.deliveryTime = deliveryTime;
        this.price = price;
    }
}


