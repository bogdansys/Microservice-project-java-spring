package nl.tudelft.sem.template.authentication.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtAuthenticationEntryPointTests {

    HttpServletRequest request;
    HttpServletResponse response;
    AuthenticationException authException;
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @BeforeEach
    void setup() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authException = mock(AuthenticationException.class);
        jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();

        doNothing().when(response).addHeader(anyString(), anyString());
        doNothing().when(response).sendError(anyInt(), anyString());
    }

    @Test
    void testCommenceNoError() throws ServletException, IOException {
        doNothing().when(response).sendError(anyInt(), anyString());

        jwtAuthenticationEntryPoint.commence(request, response, authException);
        verify(response, times(1))
                .addHeader(JwtRequestFilter.WWW_AUTHENTICATE_HEADER, JwtRequestFilter.AUTHORIZATION_AUTH_SCHEME);
        verify(response, times(1))
                .sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Test
    void testCommenceIOException() throws ServletException, IOException {
        doThrow(new IOException("")).when(response).sendError(anyInt(), anyString());

        assertThrows(IOException.class, () -> {
            jwtAuthenticationEntryPoint.commence(request, response, authException);
        });
    }
}
