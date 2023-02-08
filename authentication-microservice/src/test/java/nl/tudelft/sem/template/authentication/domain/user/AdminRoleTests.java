package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.models.AllUsersResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


public class AdminRoleTests {

    @Mock
    private transient UserRepository userRepository;

    @BeforeEach
    void setup(){
        userRepository = mock(UserRepository.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Test
    void adminUserNotDeleted(){
        Email e = new Email("email@email.com");
        AdminRole r = new AdminRole(userRepository, e);

        when(userRepository.findByEmail(e)).thenReturn(Optional.empty());
        ResponseEntity response = r.deleteUser(e);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void adminUserDeleted(){
        Email e = new Email("email@email.com");
        AdminRole r = new AdminRole(userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("hash"), r);

        when(userRepository.findByEmail(e)).thenReturn(Optional.of(u));
        doNothing().when(userRepository).delete(u);
        ResponseEntity response = r.deleteUser(e);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User deleted");
    }

    @Test
    void adminAllUsersEmpty(){
        Email e = new Email("email@email.com");
        AdminRole r = new AdminRole(userRepository, e);
        List<AppUser> list = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(list);
        ResponseEntity<AllUsersResponseModel> response = r.getAllUsers();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUsers()).isNull();
    }

    @Test
    void adminAllUsersSizeOne(){
        Email e = new Email("email@email.com");
        AdminRole r = new AdminRole(userRepository, e);
        AppUser u = new AppUser(e, new HashedPassword("hash"), r);
        List<AppUser> list = new ArrayList<>();
        list.add(u);

        when(userRepository.findAll()).thenReturn(list);
        ResponseEntity<AllUsersResponseModel> response = r.getAllUsers();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUsers()).hasSize(1);
        assertThat(response.getBody().getUsers().contains(u.toString())).isTrue();
    }
}
