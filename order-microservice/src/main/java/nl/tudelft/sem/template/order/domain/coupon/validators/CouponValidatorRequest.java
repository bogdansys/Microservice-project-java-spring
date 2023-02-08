package nl.tudelft.sem.template.order.domain.coupon.validators;

import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;


public class CouponValidatorRequest {
    private ActivationCode activationCode;
    private String couponType;
    private double discountPercent;
    private String expirationDate;
    private int storeid;

    public CouponValidatorRequest(ActivationCode activationCode, String couponType, double discountPercent, String expirationDate, int storeid) {
        this.activationCode = activationCode;
        this.couponType = couponType;
        this.discountPercent = discountPercent;
        this.expirationDate = expirationDate;
        this.storeid = storeid;
    }

    public ActivationCode getActivationCode() {
        return activationCode;
    }

    public String getCouponType() {
        return couponType;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public int getStoreid() {
        return storeid;
    }

    public void setActivationCode(ActivationCode activationCode) {
        this.activationCode = activationCode;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setStoreid(int storeid) {
        this.storeid = storeid;
    }

    public String getCouponTypeAsString() {
        return couponType.toString();
    }


}
