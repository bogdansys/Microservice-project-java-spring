package nl.tudelft.sem.template.order.domain.order;

/**
 * suspend pmd issues
 */
@SuppressWarnings("PMD")
public class OrderData {

    public OrderData() {

    }

    public OrderData(String customerId, int storeId, Status status, String deliveryTime, double price, double moneySaved) {
        this.customerId = customerId;
        this.storeId = storeId;
        this.status = status;
        this.deliveryTime = deliveryTime;
        this.price = price;
        this.moneySaved = moneySaved;
    }

    private String customerId;
    private int storeId;
    private Status status;

    // Delivery Time of the order, with format "uuuu/MM/dd HH:mm:ss"
    private String deliveryTime;
    private double price;

    /**
     * The amount of money saved while the coupon is applied
     * 0 if no coupon is applied
     */

    private double moneySaved;

    /**
     * getters And setters.
     *
     * @return
     */

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }


    public double getMoneySaved() {
        return moneySaved;
    }

    public void setMoneySaved(double moneySaved) {
        this.moneySaved = moneySaved;
    }

    /**
     * for dataBase column
     *
     * @return
     */


    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
