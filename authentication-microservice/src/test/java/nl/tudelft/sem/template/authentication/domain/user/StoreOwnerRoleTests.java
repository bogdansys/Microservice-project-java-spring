package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.models.StoreIdResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StoreOwnerRoleTests {

    @Mock
    private transient UserRepository userRepository;

    @BeforeEach
    void setup(){
        userRepository = mock(UserRepository.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Test
    void storeOwnerGetStoreIdEmpty(){
        Email e = new Email("email@email.com");
        StoreOwnerRole r = new StoreOwnerRole(2, userRepository, e);

        when(userRepository.findByEmail(e)).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            StoreOwnerRole.getStoreIdFromEmail(userRepository, e, r);
        });
        assertThat(exception.getMessage()).contains("400 BAD_REQUEST");
        assertThat(exception.getMessage()).contains("User does not exist");
    }

    @Test
    void storeOwnerGetStoreNotStoreOwnerRole(){
        Email e = new Email("email@email.com");
        UserRole r = new UserRole(userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("hash"), r);

        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            StoreOwnerRole.getStoreIdFromEmail(userRepository, e, r);
        });
        assertThat(exception.getMessage()).contains("400 BAD_REQUEST");
        assertThat(exception.getMessage()).contains("User is unauthorized");
    }

    @Test
    void storeOwnerGetStoreIdCorrect(){
        Email e = new Email("email@email.com");
        StoreOwnerRole r = new StoreOwnerRole(2, userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("hash"), r);

        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
        ResponseEntity<StoreIdResponseModel> response = StoreOwnerRole.getStoreIdFromEmail(userRepository, e, r);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStoreId()).isEqualTo(2);
    }
}
