package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.framework.StoreIdAdapter;
import nl.tudelft.sem.template.order.model.StoreIdResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class StoreIdAdapterTest {
    @Mock
    private RestTemplate restTemplate;

    private transient StoreIdAdapter storeIdAdapter;

    @BeforeEach
    void setup() {
        storeIdAdapter = new StoreIdAdapter(restTemplate);
    }

    @Test
    public void testGetUsersStoreId() {
        String token = "token";
        int storeId = 5;
        StoreIdResponseModel storeIdResponseModel = new StoreIdResponseModel(storeId);

        ResponseEntity<StoreIdResponseModel> result = ResponseEntity.ok(storeIdResponseModel);

        HttpHeaders authentication = new HttpHeaders();
        authentication.setContentType(MediaType.APPLICATION_JSON);
        authentication.set("Authorization", token);

        when(restTemplate.exchange("http://localhost:8081/getStoreId", HttpMethod.GET,
                new HttpEntity<>(null, authentication), StoreIdResponseModel.class))
                .thenReturn(result);

        assertThat(storeIdAdapter.getUsersStoreId(token)).isEqualTo(storeId);
        verify(restTemplate, times(1)).exchange("http://localhost:8081/getStoreId", HttpMethod.GET,
                new HttpEntity<>(null, authentication), StoreIdResponseModel.class);
    }
}
