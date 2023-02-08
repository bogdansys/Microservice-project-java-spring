package nl.tudelft.sem.template.order.integration;

import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.order.controllers.CouponController;
import nl.tudelft.sem.template.order.domain.coupon.ApplyCouponService;
import nl.tudelft.sem.template.order.domain.coupon.CouponRepository;
import nl.tudelft.sem.template.order.domain.coupon.CreateCouponService;
import nl.tudelft.sem.template.order.domain.order.OrderRepository;
import nl.tudelft.sem.template.order.domain.order.OrderService;
import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import nl.tudelft.sem.template.order.domain.providers.TimeProvider;
import nl.tudelft.sem.template.order.domain.store.StoreRepository;
import nl.tudelft.sem.template.order.framework.StoreIdAdapter;
import nl.tudelft.sem.template.order.model.CreateCouponRequestModel;
import nl.tudelft.sem.template.order.model.DeleteCouponModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient CouponRepository couponRepository;

    @Mock
    private transient StoreRepository storeRepository;

    @Mock
    private transient OrderRepository orderRepository;

    @Mock
    private transient StoreIdAdapter mockStoreIdAdapter;

    @Mock
    private transient DateProvider mockDateProvider;

    @Mock
    private transient CreateCouponService createCouponService;

    @Mock
    private transient TimeProvider mockTimeProvider;

    private transient ApplyCouponService applyCouponService;

    private transient OrderService orderService;

    private transient CouponController couponController;

    @BeforeEach
    void setup() {
        orderService = new OrderService(orderRepository, mockTimeProvider);
        createCouponService = new CreateCouponService(couponRepository, storeRepository, mockDateProvider);
        applyCouponService = new ApplyCouponService(couponRepository, mockDateProvider, orderRepository);
        couponController = new CouponController(mockAuthenticationManager, createCouponService,
                applyCouponService, mockStoreIdAdapter);
    }

    //TODO: fully mock the services

    /**
     * Checks that the coupon repository is empty by default
     */
    @Test
    public void getCouponsEmpty() {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.

        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("ADMIN");

        String response = couponController.getCoupons().getBody();
        assertThat(response).isEqualTo("[]");

    }

    /**
     * Checks that a user (customer) cannot get coupons
     */
    @Test
    public void getCouponsUserRestricted() {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.

        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("USER");

        Exception e = assertThrows(ResponseStatusException.class, () ->
                couponController.getCoupons());
        assertThat(e.getMessage()).isEqualTo("401 UNAUTHORIZED \"User is not authorized to access all coupons\"");
    }

    /**
     * Checks that a user (customer) cannot add a coupon
     */
    @Test
    public void addCouponUserRestricted() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("USER");

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCD04");
        rm.setCouponType("DISCOUNT");
        rm.setDiscountPercent(20.0);
        rm.setExpirationDate("2023-01-01");

        Exception e = assertThrows(ResponseStatusException.class, () ->
                couponController.addCoupon(rm, "Bearer Token"));
        assertThat(e.getMessage()).isEqualTo("401 UNAUTHORIZED \"User is not authorized to add a coupon\"");
        assertThat(couponRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Checks that a valid coupon can be added as an admin
     */
    @Test
    public void addCouponAdminValidCoupon() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("ADMIN");

        when(mockDateProvider.getDate()).thenReturn(LocalDate.parse("2022-01-01"));

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCD04");
        rm.setCouponType("discount");
        rm.setDiscountPercent(20.0);
        rm.setExpirationDate("2023-01-01");

        String addResponse = couponController.addCoupon(rm, "Bearer Token").getBody();
        assertThat(addResponse).contains("id=1");

        String getResponse = couponController.getCoupons().getBody();
        assertThat(getResponse).isEqualTo("[Coupon{id=1, activationCode=ABCD04, couponType=DISCOUNT, " +
                "discountPercent=20.0, expirationDate=2023-01-01, storeid=-1}]");
        assertThat(couponRepository.findAll().size()).isEqualTo(1);
    }

    /**
     * Checks that a coupon cannot be added with an invalid activation code
     * Note: in future this should be tested in the service tests
     */
    @Test
    public void addCouponInvalidCode() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("ADMIN");

        when(mockDateProvider.getDate()).thenReturn(LocalDate.parse("2022-01-01"));

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCDP4");
        rm.setCouponType("discount");
        rm.setDiscountPercent(20.0);
        rm.setExpirationDate("2020-01-01");

        Exception e = assertThrows(ResponseStatusException.class, () -> couponController.addCoupon(rm, "Bearer Token"));
        assertThrows(Exception.class, () -> couponController.addCoupon(rm, "Bearer Token"));
        assertThat(e.getMessage()).contains("400 BAD_REQUEST");
        assertThat(couponRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Checks that a coupon cannot be added with an invalid coupon type
     * Note: in future this should be tested in the service tests
     */
    @Test
    public void addCouponInvalidType() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("ADMIN");

        when(mockDateProvider.getDate()).thenReturn(LocalDate.parse("2022-01-01"));

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCD04");
        rm.setCouponType("discount");
        rm.setDiscountPercent(20.0);
        rm.setExpirationDate("2020-01-01");

        Exception e = assertThrows(ResponseStatusException.class, () ->
                couponController.addCoupon(rm, "Bearer Token"));
        assertThat(e.getMessage()).contains("400 BAD_REQUEST");
        assertThat(couponRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Checks that a coupon cannot be added with an invalid discount percentage
     * Note: in future this should be tested in the service tests
     */
    @Test
    public void addCouponInvalidDiscountPercentage() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("ADMIN");

        when(mockDateProvider.getDate()).thenReturn(LocalDate.parse("2022-01-01"));

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCD04");
        rm.setCouponType("discount");
        rm.setDiscountPercent(101.0);
        rm.setExpirationDate("2020-01-01");

        Exception e = assertThrows(ResponseStatusException.class, () ->
                couponController.addCoupon(rm, "Bearer Token"));
        assertThat(e.getMessage()).contains("400 BAD_REQUEST");
        assertThat(couponRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Checks that a valid coupon can be added through the controller as a store owner
     */
    @Test
    @Transactional
    public void addCouponStoreOwnerValidCoupon() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("STORE_OWNER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("STORE_OWNER");

        when(mockStoreIdAdapter.getUsersStoreId(anyString())).thenReturn(1);

        when(mockDateProvider.getDate()).thenReturn(LocalDate.parse("2022-01-01"));

        when(storeRepository.existsById(1)).thenReturn(true);

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCD04");
        rm.setCouponType("discount");
        rm.setDiscountPercent(20.0);
        rm.setExpirationDate("2023-01-01");

        String addResponse = couponController.addCoupon(rm, "Bearer Token").getBody();
        assertThat(addResponse).contains("id=1");
        assertThat(couponRepository.findAll().size()).isEqualTo(1);
    }

    /**
     * Checks that when a store owner with an invalid store tries to add a coupon, an exception is thrown
     */
    @Test
    @Transactional
    public void addCouponStoreOwnerInvalidStore() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("STORE_OWNER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("STORE_OWNER");

        when(mockStoreIdAdapter.getUsersStoreId(anyString())).thenReturn(1);

        when(mockDateProvider.getDate()).thenReturn(LocalDate.parse("2022-01-01"));

        when(storeRepository.existsById(1)).thenReturn(false);

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCD04");
        rm.setCouponType("discount");
        rm.setDiscountPercent(20.0);
        rm.setExpirationDate("2023-01-01");

        Exception e = assertThrows(ResponseStatusException.class, () ->
                couponController.addCoupon(rm, "Bearer Token"));
        assertThat(couponRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Checks that a user cannot delete a coupon
     */
    @Test
    public void deleteCouponsUserRestricted() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("USER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("USER");

        DeleteCouponModel dm = new DeleteCouponModel();
        dm.setCouponId(1);

        Exception e = assertThrows(ResponseStatusException.class, () ->
                couponController.deleteCoupon(dm));
        assertThat(e.getMessage()).isEqualTo("401 UNAUTHORIZED \"User is not authorized to delete coupons\"");
    }

    /**
     * Test to see that a coupon that exists is successfully deleted
     */
    @Test
    public void deleteCouponSuccess() {
        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("ADMIN");

        when(mockDateProvider.getDate()).thenReturn(LocalDate.parse("2022-01-01"));

        CreateCouponRequestModel rm = new CreateCouponRequestModel();
        rm.setActivationCode("ABCD04");
        rm.setCouponType("discount");
        rm.setDiscountPercent(20.0);
        rm.setExpirationDate("2023-01-01");

        String addResponse = couponController.addCoupon(rm, "Bearer Token").getBody();
        assertThat(addResponse).contains("id=1");
        assertThat(couponRepository.findAll().size()).isEqualTo(1);

        DeleteCouponModel dm = new DeleteCouponModel();
        dm.setCouponId(1);

        ResponseEntity response = couponController.deleteCoupon(dm);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(couponRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Checks that an error is thrown when a coupon doesn't exist that is to be deleted
     */
    @Test
    public void deleteCouponDoesntExist() {

        when(mockAuthenticationManager.getEmail()).thenReturn("ExampleUser@gmail.com");
        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser@gmail.com");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("ADMIN");

        DeleteCouponModel dm = new DeleteCouponModel();
        dm.setCouponId(1);

        Exception e = assertThrows(ResponseStatusException.class, () ->
                couponController.deleteCoupon(dm));
        assertThat(e.getMessage()).contains("400 BAD_REQUEST");
    }
}
