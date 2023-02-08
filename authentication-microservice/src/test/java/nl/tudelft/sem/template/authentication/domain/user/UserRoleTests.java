package nl.tudelft.sem.template.authentication.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserRoleTests {

    @Mock
    private transient UserRepository userRepository;

    @BeforeEach
    void setup(){
        userRepository = mock(UserRepository.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Test
    void userAddAllergenEmpty(){
        Email e = new Email("email@email.com");
        UserRole r = new UserRole(userRepository, e);

        when(userRepository.findByEmail(e)).thenReturn(Optional.empty());
        ResponseEntity response = r.addAllergen(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void userAddAllergenUserExists(){
        Email e = new Email("email@email.com");
        UserRole r = new UserRole(userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("hash"), r);

        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
        doNothing().when(userRepository).delete(u);
        when(userRepository.save(u)).thenReturn(u);

        ResponseEntity response = r.addAllergen(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Allergen added");
    }

    @Test
    void userRemoveAllergenEmpty(){
        Email e = new Email("email@email.com");
        UserRole r = new UserRole(userRepository, e);

        when(userRepository.findByEmail(e)).thenReturn(Optional.empty());
        ResponseEntity response = r.removeAllergen(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void userRemoveAllergenUserExists(){
        Email e = new Email("email@email.com");
        UserRole r = new UserRole(userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("hash"), r);
        u.getAllergens().addAllergen(2);

        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
        doNothing().when(userRepository).delete(u);
        when(userRepository.save(u)).thenReturn(u);

        ResponseEntity response = r.removeAllergen(2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Allergen removed");
    }
}
