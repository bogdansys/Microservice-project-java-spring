package nl.tudelft.sem.template.menu.controllers;

import nl.tudelft.sem.template.menu.authentication.AuthManager;
import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.item.Item;
import nl.tudelft.sem.template.menu.domain.topping.Topping;
import nl.tudelft.sem.template.menu.domain.topping.ToppingName;
import nl.tudelft.sem.template.menu.domain.topping.ToppingService;
import nl.tudelft.sem.template.menu.model.AddToppingModel;
import nl.tudelft.sem.template.menu.model.VerifyItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Main Topping Controller used for interacting with Toppings
 */
@RestController
public class ToppingController {

    private final transient AuthManager authManager;

    private final transient ToppingService toppingService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager    Spring Security component used to authenticate and authorize the user
     * @param toppingService ...
     */
    @Autowired
    public ToppingController(AuthManager authManager, ToppingService toppingService) {
        this.authManager = authManager;
        this.toppingService = toppingService;
    }

    /**
     * Endpoint for adding a topping.
     *
     * @param request The addTopping model
     * @return 200 OK if the registration is successful
     */
    @PostMapping("/addTopping")
    public ResponseEntity<Integer> addTopping(@RequestBody AddToppingModel request) {

        if (!authManager.getRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to add a topping");
        }

        try {
            ToppingName toppingName = new ToppingName(request.getName());
            Price toppingPrice = new Price(request.getPrice());
            Topping t = toppingService.addTopping(toppingName, toppingPrice, request.getAllergens());
            return ResponseEntity.ok(t.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Gets all topping names.
     *
     * @return all the topping names in a list
     */
    @GetMapping("/listAllToppingNames")
    public ResponseEntity<List<String>> listAllToppingNames() {
        List<Topping> list = toppingService.listAllToppings();
        List<String> ret = list.stream().map(Topping::toUserString).collect(Collectors.toList());

        return ResponseEntity.ok(ret);
    }

    /**
     * Gets all toppings.
     *
     * @return all the toppings in a list
     */
    @GetMapping("/listAllToppings")
    public ResponseEntity<List<Topping>> listAllToppings() {
        return ResponseEntity.ok(toppingService.listAllToppings());
    }

    /**
     * Verifies all the toppings from the list
     *
     * @return valid toppings with additional fields
     */
    @PostMapping("/verifyToppings")
    public ResponseEntity<List<Item>> verifyToppings(@RequestBody VerifyItemModel request, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(toppingService.verifyToppings(request.getNames(), token));
    }

}
