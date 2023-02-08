package nl.tudelft.sem.template.authentication.domain.user;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class UserRole extends Role {

    // concrete decorator

    public UserRole(UserRepository userRepository, Email emailObject) {
        super(userRepository, emailObject);
    }

    public UserRole() {
        super();
    }

    public ResponseEntity addAllergen(int allergenId) {
        Optional<AppUser> user = userRepository.findByEmail(emailObject);
        if (user.isPresent()) {
            AppUser appUser = user.get();
            userRepository.delete(appUser);
            appUser.getAllergens().addAllergen(allergenId);
            userRepository.save(appUser);
            return ResponseEntity.ok("Allergen added");
        } else {
            return ResponseEntity.ok("User not found");
        }
    }

    public ResponseEntity removeAllergen(int allergenId) {
        Optional<AppUser> user = userRepository.findByEmail(emailObject);
        if (user.isPresent()) {
            AppUser appUser = user.get();
            userRepository.delete(appUser);
            appUser.getAllergens().removeAllergen(allergenId);
            userRepository.save(appUser);
            return ResponseEntity.ok("Allergen removed");
        } else {
            return ResponseEntity.ok("User not found");
        }
    }

    @Override
    public String getRole() {
        return "USER";
    }
}

