package nl.tudelft.sem.template.authentication.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import nl.tudelft.sem.template.authentication.domain.providers.TimeProvider;
import nl.tudelft.sem.template.authentication.domain.user.Role;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;

import static nl.tudelft.sem.template.authentication.authentication.JwtTokenGenerator.JWT_TOKEN_VALIDITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtTokenVerifierTests {

    private transient JwtTokenVerifier jwtTokenVerifier;
    private transient TimeProvider timeProvider;
    private transient Instant mockedTime = Instant.parse("2023-12-31T13:25:34.00Z");

    private final String secret = "testSecret123";
    private String email = "email@gmail.com";
    private String token;

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        timeProvider = mock(TimeProvider.class);
        when(timeProvider.getCurrentTime()).thenReturn(mockedTime);

        jwtTokenVerifier = new JwtTokenVerifier();
        this.injectSecret(secret);

        Role role = new UserRole();

        token = Jwts.builder()
                .claim("role", role.getRole())
                .claim("email", email)
                .setIssuedAt(new Date(mockedTime.toEpochMilli()))
                .setExpiration(new Date(mockedTime.toEpochMilli() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = jwtTokenVerifier.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(jwtTokenVerifier, secret);
    }

    @Test
    void testValidateToken(){
        assertThat(jwtTokenVerifier.validateToken(token)).isTrue();
    }

    @Test
    void testGetEmailFromToken(){
        assertThat(jwtTokenVerifier.getNetIdFromToken(token)).isEqualTo(email);
    }

    @Test
    void testGetRoleFromToken(){
        assertThat(jwtTokenVerifier.getRoleFromToken(token)).isEqualTo("USER");
    }

    @Test
    void testGetExpirationDateFromToken(){
        assertThat(jwtTokenVerifier.getExpirationDateFromToken(token))
                .isEqualTo(new Date(mockedTime.toEpochMilli() + JWT_TOKEN_VALIDITY));
    }



}
