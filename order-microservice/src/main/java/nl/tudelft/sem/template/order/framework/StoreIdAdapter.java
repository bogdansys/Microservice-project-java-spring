package nl.tudelft.sem.template.order.framework;

import nl.tudelft.sem.template.order.model.StoreIdResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * The type Store id adapter.
 */
@Service
public class StoreIdAdapter {
    private final transient RestTemplate restTemplate;

    /**
     * Instantiates a new Store id adapter.
     *
     * @param restTemplate the rest template, usually autowired
     */
    @Autowired
    public StoreIdAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gets the store id of the current user (should be a store owner, else it returns -1).
     *
     * @param token the authentication token
     * @return the store id
     */
    public int getUsersStoreId(String token) {
        HttpHeaders authentication = new HttpHeaders();
        authentication.setContentType(MediaType.APPLICATION_JSON);
        authentication.set("Authorization", token);

        //request storeId from user microservice given token
        ResponseEntity<StoreIdResponseModel> storeIdResponse = restTemplate
                .exchange("http://localhost:8081/getStoreId", HttpMethod.GET,
                        new HttpEntity<>(null, authentication), StoreIdResponseModel.class);

        StoreIdResponseModel storeIdInfo = storeIdResponse.getBody();
        assert storeIdInfo != null;

        return storeIdInfo.getStoreId();
    }
}
