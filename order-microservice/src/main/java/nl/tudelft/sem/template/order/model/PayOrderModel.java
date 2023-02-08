package nl.tudelft.sem.template.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderModel {

    // ID of the order
    private int orderId;

    // Amount of time it takes to deliver the food (so, deliveryTime = arrivingTime - payTime)
    private long deliveryTime;
}
