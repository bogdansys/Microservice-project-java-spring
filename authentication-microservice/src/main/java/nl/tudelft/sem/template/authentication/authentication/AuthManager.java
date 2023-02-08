package nl.tudelft.sem.template.authentication.authentication;

import nl.tudelft.sem.template.authentication.domain.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authentication Manager.
 */
@Component
public class AuthManager {

    private transient String errorMessage = "This user doesn't exist";

    /**
     * Interfaces with spring security to get the name of the user in the current context.
     *
     * @return The name of the user.
     */
    public String getEmail() {
        try {
            Credentials c = (Credentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            return c.name;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
    }

    public Email getEmailObject() {
        try {
            Credentials c = (Credentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            return new Email(c.name);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
    }

    public String getRole() {
        try {
            Credentials c = (Credentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            return c.role;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
    }

    public Role getRoleObject() {
        try {
            Credentials c = (Credentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            if (c.role.equals("ADMIN")){
                return new AdminRole();
            }
            if (c.role.equals("USER")){
                return new UserRole();
            }
            return new StoreOwnerRole();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }

    }

}
