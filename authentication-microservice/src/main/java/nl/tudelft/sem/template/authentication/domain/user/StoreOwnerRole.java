package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.models.StoreIdResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@SuppressWarnings("PMD")

public class StoreOwnerRole extends Role {

    //concete decorator

    int storeId;

    public StoreOwnerRole(int storeId, UserRepository userRepository, Email emailObject) {
        super(userRepository, emailObject);
        this.storeId = storeId;
    }

    @SuppressWarnings("PMD")
    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public StoreOwnerRole() {
        super();
    }

    public static ResponseEntity<StoreIdResponseModel> getStoreIdFromEmail(UserRepository userRepository, Email emailObject, Role role) {
        //checks if user exists in user table
        Optional<AppUser> user = userRepository.findByEmail(emailObject);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        //if the user isn't a store owner, they don't have a store id, so they are unauthorized
        if (!role.getRole().equals("STORE_OWNER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is unauthorized");
        }
        //Note: to avoid unnecessary overhead by communicating across microservices, the validity
        //of the storeId is checked when used in the Order microservice. This can be changed if desired.
        Integer storeId = user.get().getStoreId();
        return ResponseEntity.ok(new StoreIdResponseModel(storeId));
    }

    @Override
    public String getRole() {
        return "STORE_OWNER";
    }
}
