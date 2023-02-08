package nl.tudelft.sem.template.menu.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.menu.authentication.AuthManager;
import nl.tudelft.sem.template.menu.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.menu.controllers.ProductController;
import nl.tudelft.sem.template.menu.domain.item.Item;
import nl.tudelft.sem.template.menu.domain.product.ProductService;
import nl.tudelft.sem.template.menu.domain.topping.ToppingRepository;
import nl.tudelft.sem.template.menu.domain.topping.ToppingService;
import nl.tudelft.sem.template.menu.model.AddProductModel;
import nl.tudelft.sem.template.menu.model.AllergenResponseModel;
import nl.tudelft.sem.template.menu.domain.product.ProductRepository;
import nl.tudelft.sem.template.menu.model.VerifyItemModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Mock
    private transient RestTemplate mockRestTemplate;

    @Autowired
    private transient ProductRepository productRepo;

    @Autowired
    private transient ToppingRepository toppingRepo;

    @Test
    public void listAllProductNames() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(get("/listAllProductNames")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("[]");

    }

    @Test
    public void listAllProducts() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(get("/listAllProducts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("[]");

    }

    @Test
    public void verifyProducts() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");

        HttpHeaders authentication = new HttpHeaders();
        authentication.setContentType(MediaType.APPLICATION_JSON);
        authentication.set("Authorization", "Bearer Token");

        when(mockRestTemplate
                .exchange("http://localhost:8081/getAllergens", HttpMethod.GET,
                        new HttpEntity<>(null, authentication),
                        AllergenResponseModel.class))
                .thenReturn(new ResponseEntity<AllergenResponseModel>(new AllergenResponseModel(""), HttpStatus.OK));

        ToppingService ts = new ToppingService(null, mockRestTemplate);
        ProductService ps = new ProductService(productRepo, ts);
        ProductController pc = new ProductController(mockAuthenticationManager, ps);


        VerifyItemModel vim = new VerifyItemModel();
        List<String> l = new ArrayList<>();
        l.add("pizza");
        vim.setNames(l);
        String response = pc.verifyProducts(vim, "Bearer Token").getBody().toString();


        assertThat(response).isEqualTo("[null]");
    }

    @Test
    @Transactional
    public void verifyProducts2() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");

        HttpHeaders authentication = new HttpHeaders();
        authentication.setContentType(MediaType.APPLICATION_JSON);
        authentication.set("Authorization", "Bearer Token");

        when(mockRestTemplate
                .exchange("http://localhost:8081/getAllergens", HttpMethod.GET,
                        new HttpEntity<>(null, authentication),
                        AllergenResponseModel.class))
                .thenReturn(new ResponseEntity<AllergenResponseModel>(new AllergenResponseModel(""), HttpStatus.OK));

        ToppingService ts = new ToppingService(toppingRepo, mockRestTemplate);
        ProductService ps = new ProductService(productRepo, ts);
        ProductController pc = new ProductController(mockAuthenticationManager, ps);

        AddProductModel apm = new AddProductModel();
        apm.setName("pizza");
        apm.setPrice(10);
        apm.setAllergens(new ArrayList<>());
        apm.setToppings(new ArrayList<>());

        pc.addProduct(apm);


        VerifyItemModel vim = new VerifyItemModel();
        List<String> l = new ArrayList<>();
        l.add("pizza");
        vim.setNames(l);
        List<Item> response = pc.verifyProducts(vim, "Bearer Token").getBody();

        Item expectedItem = new Item("pizza", 10.0, new ArrayList<>(), false);
        List<Item> expected = new ArrayList<>();
        expected.add(expectedItem);

        assertThat(response).isEqualTo(expected);
    }

}
