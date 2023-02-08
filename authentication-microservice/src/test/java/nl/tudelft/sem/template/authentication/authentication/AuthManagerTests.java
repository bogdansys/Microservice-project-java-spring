package nl.tudelft.sem.template.authentication.authentication;

import nl.tudelft.sem.template.authentication.domain.user.Email;
import nl.tudelft.sem.template.authentication.domain.user.Role;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthManagerTests {
    private transient AuthManager authManager;

    @BeforeEach
    public void setup() {
        authManager = new AuthManager();
    }

    @Test
    public void getEmailTestNoError() {
        String expected = "user123@gmail.com";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                new Credentials(expected, "USER")
                , List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String actual = authManager.getEmail();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getEmailNoToken() {
        SecurityContextHolder.getContext().setAuthentication(null);

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authManager.getEmail();
        });
        assertThat(e.getMessage()).contains("401 UNAUTHORIZED \"This user doesn't exist\"");
    }

    @Test
    public void getEmailObjectTestNoError() {
        String expected = "user123@gmail.com";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                new Credentials(expected, "USER")
                , List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Email email = authManager.getEmailObject();

        assertThat(email).isEqualTo(new Email(expected));
    }

    @Test
    public void getEmailObjectNoToken() {
        SecurityContextHolder.getContext().setAuthentication(null);

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authManager.getEmailObject();
        });
        assertThat(e.getMessage()).contains("401 UNAUTHORIZED");
    }

    @Test
    public void getRoleTestNoError() {
        String expected = "user123@gmail.com";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                new Credentials(expected, "USER")
                , List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String actual = authManager.getRole();

        assertThat(actual).isEqualTo("USER");
    }

    @Test
    public void getRoleTestNoToken() {
        SecurityContextHolder.getContext().setAuthentication(null);

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authManager.getRole();
        });
        assertThat(e.getMessage()).contains("401 UNAUTHORIZED");
    }

    @Test
    public void getRoleObjectTestNoError() {
        String expected = "user123@gmail.com";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                new Credentials(expected, "USER")
                , List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Role actual = authManager.getRoleObject();

        assertThat(actual.getClass()).isEqualTo(UserRole.class);
    }

    @Test
    public void getRoleObjectTestNoToken() {
        SecurityContextHolder.getContext().setAuthentication(null);

        Exception e = assertThrows(ResponseStatusException.class, () -> {
            authManager.getRoleObject();
        });
        assertThat(e.getMessage()).contains("401 UNAUTHORIZED");
    }
}
