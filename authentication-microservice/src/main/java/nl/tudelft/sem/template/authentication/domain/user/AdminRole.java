package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.models.AllUsersResponseModel;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRole extends Role {

    //concrete decorator

    public AdminRole(UserRepository userRepository, Email emailObject) {
        super(userRepository, emailObject);
    }

    public AdminRole() {
        super();
    }

    /**
     * admin functionality that deletes a user using the email;
     *
     * @param emailObject
     * @return
     */
    public ResponseEntity deleteUser(Email emailObject) { // email object to delete
        Optional<AppUser> user = userRepository.findByEmail(emailObject);
        if (user.isPresent()) {
            AppUser appUser = user.get();
            userRepository.delete(appUser);
            return ResponseEntity.ok("User deleted");
        } else {
            return ResponseEntity.ok("User not found");
        }
    }

    /**
     * admin functionality that returns all users in the database.
     * mostly for debug purposes
     *
     * @return
     */
    public ResponseEntity<AllUsersResponseModel> getAllUsers() {
        List<AppUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.ok(new AllUsersResponseModel());
        }
        ArrayList<String> usersList = new ArrayList<>();
        for (AppUser user : users) {
            usersList.add(user.toString());
        }
        return ResponseEntity.ok(new AllUsersResponseModel(usersList));
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

}
