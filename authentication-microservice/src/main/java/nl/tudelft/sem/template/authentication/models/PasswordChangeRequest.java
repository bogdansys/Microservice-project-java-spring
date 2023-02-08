package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

/**
 * Model representing an authentication request.
 */

@Data
public class PasswordChangeRequest {
    private String email;
    private String oldPassword;
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}