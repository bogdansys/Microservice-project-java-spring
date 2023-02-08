package nl.tudelft.sem.template.order.domain.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.order.domain.item.Item;
import nl.tudelft.sem.template.order.domain.product.BasicProductInfo;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.domain.product.ProductDoesNotExistException;
import nl.tudelft.sem.template.order.domain.product.ProductService;
import nl.tudelft.sem.template.order.framework.ProductMicroserviceAdapter;
import nl.tudelft.sem.template.order.model.OrderResponseModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddToOrderService {
    private final transient OrderService orderService;
    private final transient ProductService productService;

    private final transient ProductMicroserviceAdapter productMicroserviceAdapter;

    private final transient OrderHelperService orderHelperService;

    public AddToOrderService(OrderService orderService, ProductService productService,
                             ProductMicroserviceAdapter productMicroserviceAdapter,
                             OrderHelperService orderHelperService) {
        this.orderHelperService = orderHelperService;
        this.orderService = orderService;
        this.productService = productService;
        this.productMicroserviceAdapter = productMicroserviceAdapter;
    }

    /**
     * Saves the given products to the given order
     *
     * @param orderId             the id of the order
     * @param products            the list of products
     * @param authorizationHeader header for making a request to the Product microservice
     * @return a response object with the order
     * @throws OrderDoesNotExistException when no order has the given id
     * @throws JsonProcessingException    when the list of product names cannot be sent to the Product microservice
     * @throws InvalidProductsException   when no products are provided to the method
     */
    @SuppressWarnings("PMD")
    public OrderResponseModel addProductsToOrder(int orderId, List<BasicProductInfo> products, String authorizationHeader)
            throws Exception {
        Order order = orderService.getOrder(orderId);
        orderHelperService.checkStatus(order);
        if (products == null) throw new InvalidProductsException();
        // Construct order and product object, and relate them
        List<Product> orderProducts = handleAddingProduct(order, products, authorizationHeader);
        if (orderProducts == null) return orderHelperService.buildOrderResponse(order);
        if (!orderProducts.isEmpty()) {
            orderProducts = productService.saveProducts(orderProducts);
            OrderUtils.addProducts(orderProducts, order);
            orderService.saveOrder(order);
        }
        return orderHelperService.buildOrderResponse(order);
    }

    @SuppressWarnings("PMD")
    private List<Product> handleAddingProduct(Order order, List<BasicProductInfo> products, String authorizationHeader) throws Exception{
        List<Product> orderProducts = new ArrayList<>(products.size());
        //Retrieve the list of validated product items
        HttpHeaders headers = orderHelperService.getAuthHeaders(authorizationHeader);
        List<String> productNames = products.stream().map(BasicProductInfo::getName).collect(Collectors.toList());
        List<Item> productsInfo = productMicroserviceAdapter.verifyProducts(productNames, headers);
        if (productsInfo == null) return null;
        List<List<String>> toppingNames = products.stream().map(BasicProductInfo::getToppings).collect(Collectors.toList());
        //Assign all valid products to the order
        for (int i = 0; i < productsInfo.size(); i++) {
            try {
                if (productsInfo.get(i) == null) continue;
                Item productItem = productsInfo.get(i);
                List<Item> toppingsInfo = productMicroserviceAdapter.verifyToppings(toppingNames.get(i), headers);
                Product product = new Product(productItem.getName(), new ArrayList<>(),
                        productItem.getPrice(), productItem.getAllergens(), order, productItem.getWarning());
                addToppingListToProduct(product, toppingsInfo);
                orderProducts.add(product);
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
            }
        }
        return orderProducts;
    }

    /**
     * Saves the given toppings to the given product
     * @param productId the id of the product
     * @param toppings the list of toppings
     * @param authorizationHeader header for making a request to the Product microservice
     * @return a response object with the order
     * @throws ProductDoesNotExistException when no product has the given id
     * @throws InvalidToppingsException when no toppings are provided to the method
     * @throws JsonProcessingException when the list of topping or product names cannot be sent to the Product microservice
     */
    public OrderResponseModel addToppingsToProduct(Long productId, List<String> toppings, String authorizationHeader)
            throws Exception {
        Product product = productService.getProduct(productId);

        if (toppings == null) {
            throw new InvalidToppingsException(product.getName());
        }

        //Subtract old price from order
        Order order = product.getOrder();

        orderHelperService.checkStatus(order);

        List<Item> listOfToppingItems = orderHelperService.getVerifiedToppings(toppings, authorizationHeader);

        addToppingListToProduct(product, listOfToppingItems);

        productService.saveProduct(product);

        // Add the new price to order
        OrderUtils.calculatePrice(order);

        orderService.saveOrder(order);

        return orderHelperService.buildOrderResponse(product.getOrder());
    }

    /**
     * Adds the given topping items to the given product, and updates the product
     * @param product the product
     * @param toppingsInfo the topping items
     */
    void addToppingListToProduct(Product product, List<Item> toppingsInfo){
        if (toppingsInfo == null) {
            return;
        }
        if (toppingsInfo.isEmpty()) {
            product.setToppingNames(new ArrayList<>());
            return;
        }

        //Assign all valid toppings to the product
        List<String> toppings = product.getToppingNames() == null ? new ArrayList<>() : new ArrayList<>(product.getToppingNames());

        List<Integer> newAllergens = new ArrayList<>(product.getAllergens());

        double price = product.getPrice();

        boolean allergic = product.isAllergic();

        // Toppings info
        toppingsInfo = toppingsInfo.stream().filter(Objects::nonNull).collect(Collectors.toList());

        for (Item toppingItem : toppingsInfo) {
            toppings.add(toppingItem.getName());

            price += toppingItem.getPrice();

            List<Integer> toppingAllergens = toppingItem.getAllergens()
                    .stream().filter(val -> !product.getAllergens().contains(val)).collect(Collectors.toList());

            newAllergens.addAll(toppingAllergens);

            allergic = allergic || toppingItem.getWarning();
        }

        product.setToppingNames(toppings);
        product.setPrice(price);
        product.setAllergens(newAllergens);
        product.setAllergic(allergic);
    }
}
