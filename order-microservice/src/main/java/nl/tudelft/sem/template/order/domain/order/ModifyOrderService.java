package nl.tudelft.sem.template.order.domain.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.order.domain.item.Item;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.domain.product.ProductDoesNotExistException;
import nl.tudelft.sem.template.order.domain.product.ProductService;
import nl.tudelft.sem.template.order.framework.ProductMicroserviceAdapter;
import nl.tudelft.sem.template.order.model.OrderResponseModel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ModifyOrderService {
    private final transient OrderService orderService;
    private final transient ProductService productService;

    private final transient ProductMicroserviceAdapter productMicroserviceAdapter;

    private final transient AddToOrderService addToOrderService;

    private final transient OrderHelperService orderHelperService;

    public ModifyOrderService(OrderService orderService, ProductService productService,
                              ProductMicroserviceAdapter productMicroserviceAdapter,
                              AddToOrderService addToOrderService,
                              OrderHelperService orderHelperService) {
        this.orderHelperService = orderHelperService;
        this.addToOrderService = addToOrderService;
        this.orderService = orderService;
        this.productService = productService;
        this.productMicroserviceAdapter = productMicroserviceAdapter;
    }

    /**
     * Removes the given products from their orders
     * @param productIds the products
     * @return a list of response objects with the impacted orders
     * @throws InvalidProductsException when no products are provided to the method
     * @throws ProductDoesNotExistException when there is no product with the given id
     */
    public List<OrderResponseModel> removeProductsFromOrder(List<Long> productIds) throws Exception {
        if (productIds == null) {
            throw new InvalidProductsException();
        }

        Set<Order> impactedOrders = new HashSet<>(productIds.size());

        for (Long productId : productIds) {
            Product product = productService.getProduct(productId);
            impactedOrders.add(product.getOrder());
            removeProductFromOrder(productId);
        }

        List<OrderResponseModel> result = new ArrayList<>(productIds.size());
        for (Order order : impactedOrders) {
            result.add(orderHelperService.buildOrderResponse(order));
        }
        return result;
    }

    /**
     * Removes the given product from its order
     * @param productId the id of the product
     * @return a response object with the order
     * @throws ProductDoesNotExistException when there is no product with the given id
     */
    public OrderResponseModel removeProductFromOrder(Long productId) throws Exception {
        Product product = productService.getProduct(productId);
        Order order = product.getOrder();

        orderHelperService.checkStatus(order);

        OrderUtils.deleteProduct(product, order);
        productService.removeProduct(product);
        orderService.saveOrder(order);

        return orderHelperService.buildOrderResponse(order);
    }

    /**
     * Removes the given toppings from the given product
     * @param productId the product
     * @param toppings the names of the toppings
     * @param authorizationHeader header for making a request to the Product microservice
     * @return a response object with the order
     * @throws ProductDoesNotExistException when there is no product with the given id
     * @throws JsonProcessingException when the list of topping or product names cannot be sent to the Product microservice
     * @throws InvalidToppingsException when no toppings are provided to the method
     */
    public OrderResponseModel removeToppingsFromProduct(Long productId, List<String> toppings, String authorizationHeader) throws Exception {
        Product product = productService.getProduct(productId);

        if (toppings == null){
            throw new InvalidToppingsException(product.getName());
        }

        for (String toppingName : toppings){
            removeToppingFromProduct(productId, toppingName, authorizationHeader);
        }

        return orderHelperService.buildOrderResponse(product.getOrder());
    }

    /**
     * Removes the given topping from the given product
     * @param productId the product
     * @param toppingName the topping
     * @param authorizationHeader header for making a request to the Product microservice
     * @return a response object with the order
     * @throws ProductDoesNotExistException when there is no product with the given id
     * @throws JsonProcessingException when the list of topping or product names cannot be sent to the Product microservice
     */
    @SuppressWarnings("PMD")
    public OrderResponseModel removeToppingFromProduct(Long productId, String toppingName, String authorizationHeader)
            throws ProductDoesNotExistException, JsonProcessingException, OrderCancelledException, OrderIsAlreadyPaidException {
        Product product = productService.getProduct(productId);

        Order order = product.getOrder();

        orderHelperService.checkStatus(order);

        // Get the items for remaining toppings
        List<String> remainingToppingNames = new ArrayList<>(product.getToppingNames());
        remainingToppingNames.remove(toppingName);

        Item productItem = orderHelperService.getVerifiedProducts(List.of(product.getName()), authorizationHeader).get(0);

        product.setPrice(productItem.getPrice());
        product.setAllergens(productItem.getAllergens());
        product.setAllergic(productItem.getWarning());

        List<Item> listOfToppingItems = orderHelperService.getVerifiedToppings(remainingToppingNames, authorizationHeader);

        addToOrderService.addToppingListToProduct(product, listOfToppingItems);

        OrderUtils.calculatePrice(order);

        orderService.saveOrder(order);
        productService.saveProduct(product);

        return orderHelperService.buildOrderResponse(product.getOrder());
    }
}
