package nl.tudelft.sem.template.authentication.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import nl.tudelft.sem.template.authentication.domain.providers.TimeProvider;
import nl.tudelft.sem.template.authentication.domain.user.AdminRole;
import nl.tudelft.sem.template.authentication.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtTokenGeneratorTests {
    private transient JwtTokenGenerator jwtTokenGenerator;
    private transient TimeProvider timeProvider;
    private transient Instant mockedTime = Instant.parse("2021-12-31T13:25:34.00Z");

    private final String secret = "testSecret123";

    private String email = "andy123";
    private UserDetails user;

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        timeProvider = mock(TimeProvider.class);
        when(timeProvider.getCurrentTime()).thenReturn(mockedTime);

        jwtTokenGenerator = new JwtTokenGenerator(timeProvider);
        this.injectSecret(secret);

        user = new User(email, "someHash", new ArrayList<>());
    }

    @Test
    public void generatedTokenHasCorrectIssuanceDate() {
        // Act
        Role role = new AdminRole();
        String token = jwtTokenGenerator.generateToken(user, role);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getIssuedAt()).isEqualTo(mockedTime.toString());
    }

    @Test
    public void generatedTokenHasCorrectExpirationDate() {
        // Act
        Role role = new AdminRole();
        String token = jwtTokenGenerator.generateToken(user, role);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getExpiration()).isEqualTo(mockedTime.plus(1, ChronoUnit.DAYS).toString());
    }

    @Test
    public void generatedTokenHasCorrectNetId() {
        // Act
        Role role = new AdminRole();
        String token = jwtTokenGenerator.generateToken(user, role);

        // Assert
        assertThat(getClaims(token).get("email")).isEqualTo(email);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setAllowedClockSkewSeconds(Integer.MAX_VALUE)
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = jwtTokenGenerator.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(jwtTokenGenerator, secret);
    }
}
