package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.domain.store.Store;
import nl.tudelft.sem.template.order.domain.store.StoreName;
import nl.tudelft.sem.template.order.domain.store.StoreService;
import nl.tudelft.sem.template.order.model.AddStoreModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class StoreController {

    private final transient AuthManager authManager;

    private final transient StoreService storeService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager  Spring Security component used to authenticate and authorize the user
     * @param storeService ...
     */
    @Autowired
    public StoreController(AuthManager authManager, StoreService storeService) {
        this.authManager = authManager;
        this.storeService = storeService;
    }

    /**
     * Endpoint for adding a store.
     *
     * @param request The addStore model
     * @return 200 OK if the registration is successful, and returns the id of the store
     * @throws ResponseStatusException if a store with this name already exists
     *                                 asserts that only an admin can add a store
     */
    @PostMapping("/addStore")
    public ResponseEntity<Integer> addStore(@RequestBody AddStoreModel request) {
        try {
            // Check that the user is an admin
            if (!authManager.getRole().equals("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admins can add stores");
            }

            StoreName storeName = new StoreName(request.getName());
            Store store = storeService.addStore(storeName);

            int storeId = store.getId();

            return ResponseEntity.ok().body(storeId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Gets all store names.
     *
     * @return all the store names in a list
     */
    @GetMapping("/listAllNames")
    public ResponseEntity<String> listAllStoreNames() {
        return ResponseEntity.ok(storeService.listAllStores().toString());
    }

    /**
     * Gets all stores.
     *
     * @return all the stores in a list
     */
    @GetMapping("/listAll")
    public ResponseEntity<List<Store>> listAllStores() {
        return ResponseEntity.ok(storeService.listAllStores());
    }
}
