package nl.tudelft.sem.template.order.simpleClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.order.domain.item.Item;
import nl.tudelft.sem.template.order.framework.ProductMicroserviceAdapter;
import nl.tudelft.sem.template.order.model.VerifyItemModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ProductMicroserviceAdapterTest {
    @Mock
    private RestTemplate restTemplate;

    private transient ProductMicroserviceAdapter productMicroserviceAdapter;

    @BeforeEach
    void setup() {
        productMicroserviceAdapter = new ProductMicroserviceAdapter(restTemplate);
    }

    @Test
    public void testVerifyProducts() throws JsonProcessingException {
        String token = "token";
        HttpHeaders authentication = new HttpHeaders();
        authentication.setContentType(MediaType.APPLICATION_JSON);
        authentication.set("Authorization", token);

        String product = "testProduct";

        ObjectMapper objectMapper = new ObjectMapper();
        String stringOfTheList = objectMapper.writeValueAsString(new VerifyItemModel(List.of(product)));

        List<Item> result = new ArrayList<>();
        result.add(new Item("testInfo", 3d, null));

        when(restTemplate.exchange("http://localhost:8084/verifyProducts", HttpMethod.POST,
                new HttpEntity<>(stringOfTheList, authentication),
                new ParameterizedTypeReference<List<Item>>() {}))
                .thenReturn(ResponseEntity.ok(result));

        assertThat(productMicroserviceAdapter.verifyProducts(List.of(product), authentication))
                .isEqualTo(result);
        verify(restTemplate, times(1)).exchange("http://localhost:8084/verifyProducts", HttpMethod.POST,
                new HttpEntity<>(stringOfTheList, authentication),
                new ParameterizedTypeReference<List<Item>>() {});
    }

    @Test
    public void testVerifyToppings() throws JsonProcessingException {
        String token = "token";
        HttpHeaders authentication = new HttpHeaders();
        authentication.setContentType(MediaType.APPLICATION_JSON);
        authentication.set("Authorization", token);

        String topping = "testTopping";

        ObjectMapper objectMapper = new ObjectMapper();
        String stringOfTheList = objectMapper.writeValueAsString(new VerifyItemModel(List.of(topping)));

        List<Item> result = new ArrayList<>();
        result.add(new Item("testInfo", 3d, null));

        when(restTemplate.exchange("http://localhost:8084/verifyToppings", HttpMethod.POST,
                new HttpEntity<>(stringOfTheList, authentication),
                new ParameterizedTypeReference<List<Item>>() {}))
                .thenReturn(ResponseEntity.ok(result));

        assertThat(productMicroserviceAdapter.verifyToppings(List.of(topping), authentication))
                .isEqualTo(result);
        verify(restTemplate, times(1)).exchange("http://localhost:8084/verifyToppings", HttpMethod.POST,
                new HttpEntity<>(stringOfTheList, authentication),
                new ParameterizedTypeReference<List<Item>>() {});
    }
}
