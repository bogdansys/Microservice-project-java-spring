package nl.tudelft.sem.template.order.domain.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.order.domain.item.Item;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.framework.ProductMicroserviceAdapter;
import nl.tudelft.sem.template.order.model.OrderResponseModel;
import nl.tudelft.sem.template.order.model.ProductResponseModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderHelperService {
    private final transient ProductMicroserviceAdapter productMicroserviceAdapter;

    public OrderHelperService(ProductMicroserviceAdapter productMicroserviceAdapter) {
        this.productMicroserviceAdapter = productMicroserviceAdapter;
    }

    /**
     * Creates a request for validating the given products
     * @param productNames the products
     * @param authorizationHeader header for making a request to the Product microservice
     * @return the validated product items
     * @throws JsonProcessingException when the list of product names cannot be sent to the Product microservice
     */
    public List<Item> getVerifiedProducts(List<String> productNames, String authorizationHeader)
        throws JsonProcessingException {
        //Retrieve the list of validated product items
        HttpHeaders headers = getAuthHeaders(authorizationHeader);

        return productMicroserviceAdapter.verifyProducts(productNames, headers);
    }

    /**
     * Creates a request for validating the given toppings
     * @param toppings the topping names
     * @param authorizationHeader header for making a request to the Product microservice
     * @return the validated topping items
     * @throws JsonProcessingException when the list of product names cannot be sent to the Product microservice
     */
    public List<Item> getVerifiedToppings(List<String> toppings, String authorizationHeader)
        throws JsonProcessingException {
        if (toppings == null || toppings.isEmpty()) {
            return new ArrayList<>();
        }

        HttpHeaders headers = getAuthHeaders(authorizationHeader);

        return productMicroserviceAdapter.verifyToppings(toppings, headers);
    }

    /**
     * Creates a response object from an order
     *
     * @param order the order
     * @return the response object
     */
    public OrderResponseModel buildOrderResponse(Order order) {
        List<ProductResponseModel> listOfProductResponses = new ArrayList<>();

        for (Product product : order.getProducts()) {
            listOfProductResponses.add(buildProductResponse(product));
        }

        return new OrderResponseModel(
                order.getOrderId(), order.getData().getCustomerId(), order.getData().getStoreId(), listOfProductResponses,
                order.getData().getStatus(), order.getData().getDeliveryTime(), order.getData().getPrice()
        );
    }

    /**
     * Creates a response object from a product
     *
     * @param product the product
     * @return the response object
     */
    public ProductResponseModel buildProductResponse(Product product) {
        return new ProductResponseModel(
                product.getId(), product.getName(), product.getToppingNames(),
                product.getPrice(), product.getAllergens(), product.isAllergic()
        );
    }

    public HttpHeaders getAuthHeaders(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorizationHeader);

        return headers;
    }

    public void checkStatus(Order order) throws OrderIsAlreadyPaidException, OrderCancelledException {
        if(order.getData().getStatus().equals(Status.PAID)){
            throw new OrderIsAlreadyPaidException();
        }
        if(order.getData().getStatus().equals(Status.CANCELLED)){
            throw new OrderCancelledException(order.getOrderId());
        }
    }
}
