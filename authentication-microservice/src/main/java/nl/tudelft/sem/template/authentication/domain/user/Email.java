package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
public class Email {
    private final transient String emailValue;

    public Email(String email) {
        if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$") == false) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.emailValue = email;
    }

    @Override
    public String toString() {
        return emailValue;
    }
}
