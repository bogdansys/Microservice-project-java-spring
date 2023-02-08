package nl.tudelft.sem.template.order.model;

import lombok.Data;

@Data
public class ApplyCouponModel {
    private String activationCode;

    private int orderId;

}

