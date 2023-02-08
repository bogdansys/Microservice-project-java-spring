package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.models.AllergenResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public abstract class Role{

    // this is the decorator
    transient UserRepository userRepository;
    transient Email emailObject;


    public String getRole() {
        return "Role not assigned";
    }

    public Role(UserRepository userRepository, Email emailObject) {
        this.userRepository = userRepository;
        this.emailObject = emailObject;
    }

    public Role() {}

    /**
     * user functionality that returns allergens of a specific user
     * all users can have allergens
     *
     * @param emailObject
     * @return
     */
    public ResponseEntity<AllergenResponseModel> getAllergens(Email emailObject) {
        Optional<AppUser> user = userRepository.findByEmail(emailObject);
        Allergens allergen = user.get().getAllergens();
        if (allergen == null) {
            return ResponseEntity.ok(new AllergenResponseModel());
        }
        return ResponseEntity.ok(new AllergenResponseModel(allergen.toString()));
    }

    /**
     * creating a role.
     * helper function to make code easier to read
     *
     * @param role
     * @return
     */
    public static Role createRole(String role) {
        if (role.equals("ADMIN")) {
            return new AdminRole();
        } else if (role.equals("STORE_OWNER")) {
            return new StoreOwnerRole();
        }
        if (!role.equals("USER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is not valid");
        }
        return new UserRole();
    }

    /**
     * change password
     *
     * @return
     */
    public ResponseEntity<String> changePassword(String email, String oldPassword,
                                                 String newPassword, AuthenticationManager authenticationManager,
                                                 PasswordHashingService passwordHashingService) {
        try {
            if (authenticate(email, oldPassword, authenticationManager)) {
                Email emailObject = new Email(email);
                HashedPassword hashedPassword = passwordHashingService.hash(new Password(newPassword));
                Optional<AppUser> user = userRepository.findByEmail(emailObject);
                user.get().setPassword(hashedPassword);
                AppUser temp = user.get();
                userRepository.delete(user.get());
                userRepository.save(temp);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authentication failed");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    public boolean authenticate(String email, String password, AuthenticationManager authenticationManager) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password));  // to be reformated later
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }
        return true;
    }


    public String toString() {
        return getRole();
    }

}
