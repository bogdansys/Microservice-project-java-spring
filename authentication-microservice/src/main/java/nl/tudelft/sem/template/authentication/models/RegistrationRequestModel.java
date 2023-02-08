package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private String email;
    private String password;
    private String role;
    private int storeId;
}