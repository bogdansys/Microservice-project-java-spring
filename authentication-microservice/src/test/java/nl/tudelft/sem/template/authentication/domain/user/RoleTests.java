package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.models.AllergenResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoleTests {

    @Mock
    private transient UserRepository userRepository;

    @Mock
    private transient AuthenticationManager authenticationManager;

    @BeforeEach
    void setup(){
        authenticationManager = mock(AuthenticationManager.class, Mockito.RETURNS_DEEP_STUBS);
        userRepository = mock(UserRepository.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Test
    void testAdminGetRole(){
        Email e = new Email("abc@gmail.com");
        Role r = new AdminRole(userRepository, e);
        assertThat(r.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void testUserGetRole(){
        Email e = new Email("abc@gmail.com");
        Role r = new UserRole(userRepository, e);
        assertThat(r.getRole()).isEqualTo("USER");
    }

    @Test
    void testStoreOwnerGetRole(){
        Email e = new Email("abc@gmail.com");
        Role r = new StoreOwnerRole(1, userRepository, e);
        assertThat(r.getRole()).isEqualTo("STORE_OWNER");
    }

    @Test
    void testAdminToString(){
        Email e = new Email("abc@gmail.com");
        Role r = new AdminRole(userRepository, e);
        assertThat(r.toString()).isEqualTo("ADMIN");
    }

    @Test
    void testUserToString(){
        Email e = new Email("abc@gmail.com");
        Role r = new UserRole(userRepository, e);
        assertThat(r.toString()).isEqualTo("USER");
    }

    @Test
    void testStoreOwnerToString(){
        Email e = new Email("abc@gmail.com");
        Role r = new StoreOwnerRole(1, userRepository, e);
        assertThat(r.toString()).isEqualTo("STORE_OWNER");
    }

    @Test
    void testCreateRoleAdmin(){
        Role r = Role.createRole("ADMIN");
        assertThat(r.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void testCreateRoleUser(){
        Role r = Role.createRole("USER");
        assertThat(r.getRole()).isEqualTo("USER");
    }

    @Test
    void testCreateRoleStoreOwner(){
        Role r = Role.createRole("STORE_OWNER");
        assertThat(r.getRole()).isEqualTo("STORE_OWNER");
    }

    @Test
    void testCreateRoleOther(){
        Exception e = assertThrows(ResponseStatusException.class, () -> {
            Role.createRole("TEST");
        });
        assertThat(e.getMessage()).isEqualTo("400 BAD_REQUEST \"Role is not valid\"");
    }

    @Test
    void testAuthenticateDisabledException(){
        String email = "abc@gmail.com";
        String pass = "pass";
        Role r = new AdminRole(userRepository, new Email(email));

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pass)))
                .thenThrow(new DisabledException(""));

        Exception e = assertThrows(ResponseStatusException.class, () ->
                r.authenticate(email, pass, authenticationManager));
        assertThat(e.getMessage()).contains("401 UNAUTHORIZED \"USER_DISABLED\"");
    }

    @Test
    void testAuthenticateInvalidCredentials(){
        String email = "abc@gmail.com";
        String pass = "pass";
        Role r = new AdminRole(userRepository, new Email(email));

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pass)))
                .thenThrow(new BadCredentialsException(""));

        Exception e = assertThrows(ResponseStatusException.class, () ->
                r.authenticate(email, pass, authenticationManager));
        assertThat(e.getMessage()).contains("401 UNAUTHORIZED \"INVALID_CREDENTIALS\"");
    }

    @Test
    void testAuthenticateWorks(){
        String email = "abc@gmail.com";
        String pass = "pass";
        Role r = new AdminRole(userRepository, new Email(email));

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pass)))
                .thenReturn(any(Authentication.class));

        assertThat(r.authenticate(email, pass, authenticationManager)).isTrue();
    }

    @Test
    void testGetAllergensEmpty(){
        Email e = new Email("abc@gmail.com");
        Role r = new AdminRole(userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("pass"), r);
        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
        ResponseEntity<AllergenResponseModel> request = r.getAllergens(e);
        assertThat(request.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(request.getBody().getAllergen()).isEqualTo("");
    }

    @Test
    void testGetAllergensFull(){
        Email e = new Email("abc@gmail.com");
        Role r = new AdminRole(userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("pass"), r);
        u.getAllergens().addAllergen(1);
        u.getAllergens().addAllergen(5);
        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
        ResponseEntity<AllergenResponseModel> request = r.getAllergens(e);
        assertThat(request.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(request.getBody().getAllergen()).isEqualTo("1,5,");
    }

    @Test
    void testGetAllergensNull(){
        Email e = new Email("abc@gmail.com");
        Role r = new AdminRole(userRepository, e);
        AppUser mockUser = mock(AppUser.class);
        when(mockUser.getAllergens()).thenReturn(null);
        when(userRepository.findByEmail(e)).thenReturn(Optional.of(mockUser));
        ResponseEntity<AllergenResponseModel> request = r.getAllergens(e);
        assertThat(request.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(request.getBody().getAllergen()).isNull();
    }

    @Test
    void testAuthenticateUnsuccessful(){
        String email = "abc@gmail.com";
        String oldpass = "pass";
        String newpass = "passnew";
        PasswordHashingService passwordHashingService = mock(PasswordHashingService.class);
        Role r = new AdminRole(userRepository, new Email(email));
        AppUser u = new AppUser(new Email(email), new HashedPassword("pass"), r);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, oldpass)))
                .thenThrow(new DisabledException(""));

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            r.changePassword(email, oldpass, newpass,
                    authenticationManager, passwordHashingService);
        });
        assertThat(e.getMessage()).contains("400 BAD_REQUEST");
    }

//    @Test
//    void testAuthenticateSuccessful(){
//        String email = "abc@gmail.com";
//        Email e = new Email(email);
//        String oldpass = "pass";
//        String newpass = "passnew";
//        PasswordHashingService passwordHashingService = mock(PasswordHashingService.class);
//        Role r = new AdminRole(userRepository, e);
//        Role spyR = Mockito.spy(r);
//        AppUser u = mock(AppUser.class);
//        doNothing().when(u).setPassword(new HashedPassword(anyString()));
//
//        doReturn(true).when(spyR).authenticate(email, oldpass, authenticationManager);
//        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
//        when(passwordHashingService.hash(new Password(newpass))).thenReturn(new HashedPassword(anyString()));
//        doNothing().when(userRepository).delete(u);
//        when(userRepository.save(u)).thenReturn(u);
//
//        ResponseEntity<String> response = spyR.changePassword(email, oldpass, newpass,
//                authenticationManager, passwordHashingService);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void testFailingUserRepository(){
        String email = "abc@gmail.com";
        String oldpass = "pass";
        String newpass = "passnew";
        PasswordHashingService passwordHashingService = mock(PasswordHashingService.class);
        Role r = new AdminRole(userRepository, new Email(email));
        AppUser u = new AppUser(new Email(email), new HashedPassword("pass"), r);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, oldpass)))
                .thenReturn(any(Authentication.class));
        when(userRepository.findByEmail(new Email(email))).thenReturn(Optional.of(u));
        when(passwordHashingService.hash(new Password(newpass))).thenReturn(new HashedPassword("pass"));
        doThrow(new IllegalArgumentException("")).when(userRepository).delete(u);
        when(userRepository.save(u)).thenReturn(u);

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            r.changePassword(email, oldpass, newpass,
                    authenticationManager, passwordHashingService);
        });
        assertThat(e.getMessage()).contains("400 BAD_REQUEST");
    }

}
