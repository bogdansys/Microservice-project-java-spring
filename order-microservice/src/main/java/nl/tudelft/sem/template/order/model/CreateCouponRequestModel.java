package nl.tudelft.sem.template.order.model;

import lombok.Data;

/**
 * Model representing a request to create a coupon
 */
@Data
public class CreateCouponRequestModel {
    private String activationCode;
    private String couponType;
    private double discountPercent;
    private String expirationDate;
}
