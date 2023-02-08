package nl.tudelft.sem.template.order.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.order.domain.item.Item;
import nl.tudelft.sem.template.order.model.VerifyItemModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductMicroserviceAdapter {
    private final transient RestTemplate restTemplate;

    public ProductMicroserviceAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends request to the verifyProducts endpoint of the Product microservice with the given products
     * @param listWithTheProductsToAdd list of the product names to be verified
     * @param headers the headers of the request
     * @return the verified product items
     * @throws JsonProcessingException when the list of product names cannot be put into the request
     */
    public List<Item> verifyProducts(List<String> listWithTheProductsToAdd, HttpHeaders headers) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String stringOfTheList = objectMapper.writeValueAsString(new VerifyItemModel(listWithTheProductsToAdd));
        ResponseEntity<List<Item>> responseEntity = restTemplate
                .exchange("http://localhost:8084/verifyProducts", HttpMethod.POST,
                        new HttpEntity<>(stringOfTheList, headers),
                        new ParameterizedTypeReference<>() {});
        return responseEntity.getBody();
    }

    /**
     * Sends request to the verifyToppings endpoint of the Product microservice with the given toppings
     * @param listWithTheToppingsToAdd list of the topping names to be verified
     * @param headers the headers of the request
     * @return the verified topping items
     * @throws JsonProcessingException when the list of topping names cannot be put into the request
     */
    public List<Item> verifyToppings(List<String> listWithTheToppingsToAdd, HttpHeaders headers) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String stringOfTheList = objectMapper.writeValueAsString(new VerifyItemModel(listWithTheToppingsToAdd));
        ResponseEntity<List<Item>> responseEntity = restTemplate
                .exchange("http://localhost:8084/verifyToppings", HttpMethod.POST,
                        new HttpEntity<>(stringOfTheList, headers),
                        new ParameterizedTypeReference<>() {});
        return responseEntity.getBody();
    }
}
