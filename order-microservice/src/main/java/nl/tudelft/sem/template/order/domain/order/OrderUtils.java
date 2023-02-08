package nl.tudelft.sem.template.order.domain.order;

import nl.tudelft.sem.template.order.domain.coupon.exceptions.CanNotApplyBOGOCouponWithSingleProductException;
import nl.tudelft.sem.template.order.domain.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("PMD")

public class OrderUtils {

    public static void pay(String deliveryDate, Order order) {
        // Set the time of delivery
        order.getData().setDeliveryTime(deliveryDate);
        // Activate the order
        order.getData().setStatus(Status.PAID);
    }

    @SuppressWarnings("PMD")
    public static double calculatePrice(Order order) {
        double totalPrice = order.getProducts().stream().map(Product::getPrice).mapToDouble(Double::doubleValue).sum();

        Stream<Double> stream = order.getProducts().stream().map(Product::getPrice);
        double newPrice;
        try {
            newPrice = order.getStrategy().computePrice(stream.collect(Collectors.toList()));
        } catch (CanNotApplyBOGOCouponWithSingleProductException e) {
            throw new RuntimeException(e);
        }
        newPrice = Math.round((newPrice) * 100.0) / 100.0; //round to 2 decimals

        order.getData().setMoneySaved(Math.round((//round to 2 decimal places
                totalPrice - newPrice) * 100.0) / 100.0);
        order.getData().setPrice(newPrice);
        return newPrice;
    }

    /**
     * Deletes a product from the order.
     *
     * @param product the id of the product we want to eliminate
     * @return whether the product was being deleted or not
     */
    public static boolean deleteProduct(Product product, Order order) {

        if (product == null) {
            return false;
        }

        if (order == null) {
            return false;
        }

        if (!order.getProducts().contains(product)) {
            return false;
        }

        if (!order.getProducts().contains(product)) {
            return false;
        }

        List<Product> newProducts = new ArrayList<>(order.getProducts());

        newProducts.remove(product);
        order.setProducts(newProducts);

        calculatePrice(order);

        return true;
    }

    public static boolean addProduct(Product product, Order order) {
        if (product != null) {
            if (!order.getProducts().contains(product)) {
                if (product.getName() != null) {
                    order.getProducts().add(product);
                    calculatePrice(order);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean addProducts(List<Product> products, Order order) {
        if (products == null) {
            return false;
        }

        if (order == null) {
            return false;
        }

        if (products.isEmpty()) {
            return false;
        }

        for (Product product : products) {
            addProduct(product, order);
        }

        calculatePrice(order);
        return true;
    }

    public static boolean deleteProducts(List<Product> products, Order order) {
        if (products == null) {
            return false;
        }

        if (products.isEmpty()) {
            return false;
        }

        if (order == null) {
            return false;
        }

        for (Product product : products) {
            if (!order.getProducts().contains(product)) {
                return false;
            }
        }

        products = products.stream().filter(product -> order.getProducts().contains(product)).collect(Collectors.toList());

        order.getProducts().removeAll(products);

//        price -= products.stream().mapToDouble(Product::getPrice).sum();

        calculatePrice(order);
        return true;
    }

}
