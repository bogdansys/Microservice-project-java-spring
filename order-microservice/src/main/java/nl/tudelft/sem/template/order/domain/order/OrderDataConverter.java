package nl.tudelft.sem.template.order.domain.order;

import javax.persistence.AttributeConverter;

public class OrderDataConverter implements AttributeConverter<OrderData, String> {


    @Override
    public String convertToDatabaseColumn(OrderData orderData) {
        return toString(orderData);
    }

    @Override
    public OrderData convertToEntityAttribute(String s) {
        OrderData orderData = fromString(s);
        return orderData;
    }

    public String toString(OrderData data) {
        if (data == null) {
            return null;
        }
        return data.getCustomerId().toString() + "," +
                data.getStoreId() + "," +
                data.getStatus().toString() + "," +
                data.getDeliveryTime().toString()  + "," +
                data.getPrice() + "," +
                data.getMoneySaved();
    }

    public OrderData fromString(String s) {
        if (s == null) {
            return null;
        }
        OrderData orderData = new OrderData();
        String[] strings = s.split(",");
        orderData.setCustomerId(strings[0]);
        orderData.setStoreId(Integer.parseInt(strings[1]));
        orderData.setStatus(Status.valueOf(strings[2]));
        orderData.setDeliveryTime(strings[3]);
        orderData.setPrice(Double.parseDouble(strings[4]));
        orderData.setMoneySaved(Double.parseDouble(strings[5]));
        return orderData;
    }


}
