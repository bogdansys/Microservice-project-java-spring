package nl.tudelft.sem.template.authentication.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static nl.tudelft.sem.template.authentication.authentication.JwtRequestFilter.AUTHORIZATION_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtRequestFilterTests {

    HttpServletRequest request;
    HttpServletResponse response;
    FilterChain chain;
    JwtTokenVerifier jwtTokenVerifier;
    JwtRequestFilter jwtRequestFilter;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    void setup() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        jwtTokenVerifier = mock(JwtTokenVerifier.class);
        jwtRequestFilter = new JwtRequestFilter(jwtTokenVerifier);

        System.setErr(new PrintStream(errContent));

        doNothing().when(response).addHeader(anyString(), anyString());
        doNothing().when(response).sendError(anyInt(), anyString());
    }

    @Test
    void testInvalidAuthHeader() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer Token Test");
        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertThat(errContent.toString()).contains("Invalid authorization header");
    }

    @Test
    void testValidateTokenThrowsCantParse() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer Token");
        when(jwtTokenVerifier.validateToken(anyString()))
                .thenThrow(new IllegalArgumentException(""));

        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertThat(errContent.toString()).contains("Unable to parse JWT token");
    }

    @Test
    void testValidateTokenThrowsExpired() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer Token");
        Header h = new DefaultHeader();
        Claims c = new DefaultClaims();
        when(jwtTokenVerifier.validateToken(anyString()))
                .thenThrow(new ExpiredJwtException(h, c, ""));

        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertThat(errContent.toString()).contains("JWT token has expired.");
    }

    @Test
    void testValidateTokenCorrect() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer Token");
        when(jwtTokenVerifier.validateToken(anyString()))
                .thenReturn(true);
        when(jwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("abc@gmail.com");
        when(jwtTokenVerifier.getRoleFromToken(anyString())).thenReturn("USER");
        jwtRequestFilter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

}
