package nl.tudelft.sem.template.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.authentication.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.authentication.JwtUserDetailsService;
import nl.tudelft.sem.template.authentication.controllers.AuthenticationController;
import nl.tudelft.sem.template.authentication.domain.user.*;
import nl.tudelft.sem.template.authentication.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authentication.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder", "mockTokenGenerator", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UsersTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient JwtTokenGenerator mockJwtTokenGenerator;

    @Autowired
    private transient AuthenticationManager mockAuthenticationManager;

    @Autowired
    private transient UserRepository userRepository;

    @Mock
    private transient JwtUserDetailsService jwtUserDetailsService;

    @Mock
    private transient RegistrationService registrationService;


    @Mock
    private transient AuthManager authManager;

    @Mock
    private transient JwtTokenGenerator jwtTokenGenerator;



    private transient AuthenticationController authenticationController;



    @BeforeEach
    void setup(){
        authenticationController = new AuthenticationController(mockAuthenticationManager,
                mockJwtTokenGenerator, jwtUserDetailsService, registrationService, mockPasswordEncoder,
                userRepository, authManager);
    }

    @Test
    public void register_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final Email testUser = new Email("SomeUser@gmail.com");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");

        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setEmail(testUser.toString());
        model.setPassword(testPassword.toString());
        final Role testRole = new UserRole();
        model.setRole(testRole.getRole());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findByEmail(testUser).orElseThrow();

        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }

    @Test
    public void register_withExistingUser_throwsException() throws Exception {
        // Arrange
        final Email testUser = new Email("SomeUser@gmail.com");
        final Password newTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final Role testRole = new UserRole();

        AppUser existingAppUser = new AppUser(testUser, existingTestPassword, testRole);
        userRepository.save(existingAppUser);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setEmail(testUser.toString());
        model.setPassword(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByEmail(testUser).orElseThrow();

    }


    @Test
    public void login_withNonexistentUsername_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser@gmail.com";
        final String testPassword = "password123";

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && testPassword.equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setEmail(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && testPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any(), any());
    }

    @Test
    public void login_withInvalidPassword_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser@gmail.com";
        final String wrongPassword = "password1234";
        final String testPassword = "password123";
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final Role testRole = new UserRole();
        when(mockPasswordEncoder.hash(new Password(testPassword))).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && wrongPassword.equals(authentication.getCredentials())
        ))).thenThrow(new BadCredentialsException("Invalid password"));

        AppUser appUser = new AppUser(new Email(testUser), testHashedPassword, testRole);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setEmail(testUser);
        model.setPassword(wrongPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isUnauthorized());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && wrongPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any(), any());
    }

    @Test
    void authenticateWithNullEmail(){
        AuthenticationRequestModel request = mock(AuthenticationRequestModel.class);
        when(request.getEmail()).thenReturn(null);
        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authenticationController.authenticate(request);
        });
        assertThat(e.getMessage()).contains("Email or password is null");
    }

    @Test
    void authenticateWithNullPassword(){
        AuthenticationRequestModel request = mock(AuthenticationRequestModel.class);
        when(request.getEmail()).thenReturn("abcd@gmail.com");
        when(request.getPassword()).thenReturn(null);
        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authenticationController.authenticate(request);
        });
        assertThat(e.getMessage()).contains("Email or password is null");
    }

    @Test
    void authenticateThrowsDisabledException(){
        AuthenticationRequestModel request = mock(AuthenticationRequestModel.class);
        when(request.getEmail()).thenReturn("abcd@gmail.com");
        when(request.getPassword()).thenReturn("pass");
        when(mockAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException(""));

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authenticationController.authenticate(request);
        });
        assertThat(e.getMessage()).contains("USER_DISABLED");
    }

    @Test
    void authenticationSuccessful() throws Exception {
        AuthenticationRequestModel request = mock(AuthenticationRequestModel.class);
        when(request.getEmail()).thenReturn("abcd@gmail.com");
        when(request.getPassword()).thenReturn("pass");
        Email e = new Email("abcd@gmail.com");
        Role r = new UserRole(userRepository, e);
        when(mockAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("abcd@gmail.com", "pass")))
                .thenReturn(new AnonymousAuthenticationToken("key", "pass",
                        List.of(new SimpleGrantedAuthority("USER"))));

        UserDetails userDetails = new User("abcd@gmail.com", "pass",
                List.of(new SimpleGrantedAuthority("USER")));
        when(jwtUserDetailsService.loadUserByUsername("abcd@gmail.com")).thenReturn(userDetails);
        AppUser u = new AppUser(e, new HashedPassword("pass"), r);
        userRepository.save(u);

        when(jwtTokenGenerator.generateToken(userDetails, r)).thenReturn("token");
        ResponseEntity<AuthenticationResponseModel> response = authenticationController.authenticate(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void removeAllergenNotUser(){
        AddAllergenRequestModel request = mock(AddAllergenRequestModel.class);
        when(authManager.getRole()).thenReturn("ADMIN");
        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authenticationController.removeAllergen(request);
        });
        assertThat(e.getMessage()).contains("Only users are able to remove allergens");
    }

    @Test
    void removeAllergenValid(){
        AddAllergenRequestModel request = mock(AddAllergenRequestModel.class);
        when(request.getAllergenId()).thenReturn(1);
        when(authManager.getRole()).thenReturn("USER");
        when(authManager.getEmail()).thenReturn("abc@gmail.com");
        Email e = new Email("abc@gmail.com");
        AppUser user = new AppUser(e, new HashedPassword("pass"), new UserRole(userRepository, e));
        user.getAllergens().addAllergen(1);
        userRepository.save(user);

        ResponseEntity response = authenticationController.removeAllergen(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testAuthGetStoreId(){
        Email e = new Email("abc@gmail.com");
        Role r = new StoreOwnerRole(1, userRepository, e);
        when(authManager.getRoleObject()).thenReturn(r);
        when(authManager.getEmail()).thenReturn("abc@gmail.com");

        AppUser user = new AppUser(e, new HashedPassword("pass"), r);
        userRepository.save(user);

        ResponseEntity<StoreIdResponseModel> response = authenticationController.getStoreId();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStoreId()).isEqualTo(1);
    }

    @Test
    void testGetAllergensNotAUser(){
        when(authManager.getRole()).thenReturn("ADMIN");

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authenticationController.getAllergens();
        });
        assertThat(e.getMessage()).contains("Only users are able to get allergens");
    }

    @Test
    void testGetAllergensUserIsEmpty(){
        when(authManager.getRole()).thenReturn("USER");
        when(authManager.getEmail()).thenReturn("abc@gmail.com");
        Email e = new Email("abc@gmail.com");

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            authenticationController.getAllergens();
        });
        assertThat(exception.getMessage()).contains("This user doesn't exist");
    }

    @Test
    void testGetAllergensCorrectNoAllergens(){
        when(authManager.getRole()).thenReturn("USER");
        when(authManager.getEmail()).thenReturn("abc@gmail.com");
        Email e = new Email("abc@gmail.com");
        AppUser user = new AppUser(e, new HashedPassword("pass"), new UserRole(userRepository, e));
        userRepository.save(user);

        ResponseEntity<AllergenResponseModel> response = authenticationController.getAllergens();
        assertThat(response.getBody().getAllergen()).isEqualTo("");
    }

    @Test
    void testGetAllergensCorrectOneAllergen(){
        when(authManager.getRole()).thenReturn("USER");
        when(authManager.getEmail()).thenReturn("abc@gmail.com");
        Email e = new Email("abc@gmail.com");
        AppUser user = new AppUser(e, new HashedPassword("pass"), new UserRole(userRepository, e));
        user.getAllergens().addAllergen(1);
        userRepository.save(user);

        ResponseEntity<AllergenResponseModel> response = authenticationController.getAllergens();
        assertThat(response.getBody().getAllergen()).isEqualTo("1,");
    }

    @Test
    void testGetUsersNotAdmin(){
        Role r = new UserRole(userRepository, new Email("m@m.com"));
        when(authManager.getRoleObject()).thenReturn(r);

        ResponseEntity<AllUsersResponseModel> response = authenticationController.getUsers();
        assertThat(response.getBody().getUsers()).isNull();
    }

    @Test
    void testGetUsersAdmin(){
        Email e1 = new Email("m@m.com");
        Email e2 = new Email("m2@m.com");
        Role r1 = new AdminRole(userRepository, e1);
        Role r2 = new UserRole(userRepository, e2);
        AppUser userAdmin = new AppUser(e1, new HashedPassword("pass"), r1);
        AppUser userCustomer = new AppUser(e2, new HashedPassword("pass"), r2);
        when(authManager.getRoleObject()).thenReturn(r1);
        userRepository.save(userAdmin);
        userRepository.save(userCustomer);

        ResponseEntity<AllUsersResponseModel> response = authenticationController.getUsers();
        assertThat(response.getBody().getUsers()).hasSize(2);
    }

    @Test
    void testDeleteUserNotAdmin(){
        DeleteUserRequestModel request = mock(DeleteUserRequestModel.class);
        Role r = new UserRole(userRepository, new Email("m@m.com"));
        when(authManager.getRoleObject()).thenReturn(r);

        ResponseEntity<AllUsersResponseModel> response = authenticationController.deleteUser(request);
        assertThat(response.getBody().getUsers()).isNull();
    }

    @Test
    void testDeleteUsersAdmin(){
        DeleteUserRequestModel request = mock(DeleteUserRequestModel.class);
        when(request.getEmail()).thenReturn("m2@m.com");
        Email e1 = new Email("m@m.com");
        Email e2 = new Email("m2@m.com");
        Role r1 = new AdminRole(userRepository, e1);
        Role r2 = new UserRole(userRepository, e2);
        AppUser userAdmin = new AppUser(e1, new HashedPassword("pass"), r1);
        AppUser userCustomer = new AppUser(e2, new HashedPassword("pass"), r2);
        when(authManager.getRoleObject()).thenReturn(r1);
        userRepository.save(userAdmin);
        userRepository.save(userCustomer);

        ResponseEntity response = authenticationController.deleteUser(request);
        assertThat(response.getBody()).isEqualTo("User deleted");
        ResponseEntity<AllUsersResponseModel> responseGet = authenticationController.getUsers();
        assertThat(responseGet.getBody().getUsers()).hasSize(1);
    }

    @Test
    void testAddAllergenNonUser(){
        AddAllergenRequestModel request = mock(AddAllergenRequestModel.class);
        when(authManager.getRole()).thenReturn("ADMIN");

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            authenticationController.addAllergen(request);
        });
        assertThat(exception.getMessage()).contains("Only users are able to get allergens");
    }

    @Test
    void testAddAllergenUserCorrect(){
        AddAllergenRequestModel request = mock(AddAllergenRequestModel.class);
        when(authManager.getRole()).thenReturn("USER");
        when(authManager.getEmail()).thenReturn("abc@gmail.com");
        when(request.getAllergenId()).thenReturn(1);
        Email e = new Email("abc@gmail.com");
        Role r = new UserRole(userRepository, e);
        AppUser user = new AppUser(e, new HashedPassword("pass"), r);
        userRepository.save(user);

        ResponseEntity response = authenticationController.addAllergen(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Allergen added");
        ResponseEntity<AllergenResponseModel> responseGetAllergens = authenticationController.getAllergens();
        assertThat(responseGetAllergens.getBody().getAllergen()).isEqualTo("1,");
    }

//    @Test
//    void testChangePassword() throws Exception {
//        PasswordChangeRequest request = mock(PasswordChangeRequest.class);
//        when(request.getEmail()).thenReturn("abc@gmail.com");
//        when(request.getNewPassword()).thenReturn("newpass");
//        when(request.getOldPassword()).thenReturn("oldpass");
//
//        Email e = new Email("abc@gmail.com");
//        Role r = new UserRole(userRepository, e);
//        when(authManager.getRoleObject()).thenReturn(r);
//        when(authManager.getEmail()).thenReturn("abc@gmail.com");
//
//        AppUser user = new AppUser(e, new HashedPassword("pass"), r);
//        userRepository.save(user);
//
//        ResponseEntity<String> response = authenticationController.changePassword(request);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
}
