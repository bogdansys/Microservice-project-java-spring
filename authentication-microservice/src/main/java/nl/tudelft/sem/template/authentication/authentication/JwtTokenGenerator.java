package nl.tudelft.sem.template.authentication.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import nl.tudelft.sem.template.authentication.domain.providers.TimeProvider;
import nl.tudelft.sem.template.authentication.domain.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Generator for JWT tokens.
 */
@Component
public class JwtTokenGenerator {

    /**
     * Time in milliseconds the JWT token is valid for.
     */
    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    @Value("${jwt.secret}")  // automatically loads jwt.secret from resources/application.properties
    private transient String jwtSecret;

    /**
     * Time provider to make testing easier.
     */
    private final transient TimeProvider timeProvider;

    @Autowired
    public JwtTokenGenerator(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * Generate a JWT token for the provided user.
     *
     * @param userDetails The user details
     * @return the JWT token
     */
    public String generateToken(UserDetails userDetails, Role role) {
        return Jwts.builder()
                .claim("role", role.getRole())
                .claim("email", userDetails.getUsername())
                .setIssuedAt(new Date(timeProvider.getCurrentTime().toEpochMilli()))
                .setExpiration(new Date(timeProvider.getCurrentTime().toEpochMilli() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }


}
