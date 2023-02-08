package nl.tudelft.sem.template.menu.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthManagerTests {
    private transient AuthManager authManager;

    @BeforeEach
    public void setup() {
        authManager = new AuthManager();
    }

    @Test
    public void getEmailTest() {
        // Arrange
        String expected = "user123@gmail.com";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                new Credentials(expected, "USER")
                , List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        String actual = authManager.getEmail();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getRoleTest() {
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
}