package nl.tudelft.sem.template.menu.controllers;

import nl.tudelft.sem.template.menu.authentication.AuthManager;
import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.item.Item;
import nl.tudelft.sem.template.menu.domain.product.Product;
import nl.tudelft.sem.template.menu.domain.product.ProductName;
import nl.tudelft.sem.template.menu.domain.product.ProductService;
import nl.tudelft.sem.template.menu.model.AddProductModel;
import nl.tudelft.sem.template.menu.model.VerifyItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Main Product Controller used for interacting with Products
 */
@RestController
public class ProductController {

    private final transient AuthManager authManager;

    private final transient ProductService productService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager    Spring Security component used to authenticate and authorize the user
     * @param productService ...
     */
    @Autowired
    public ProductController(AuthManager authManager, ProductService productService) {
        this.authManager = authManager;
        this.productService = productService;
    }

    /**
     * Endpoint for adding a product.
     *
     * @param request The addProduct model
     * @return 200 OK if the registration is successful
     */
    @PostMapping("/addProduct")
    public ResponseEntity<Integer> addProduct(@RequestBody AddProductModel request) {

        if (!authManager.getRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to add a product");
        }

        try {
            ProductName productName = new ProductName(request.getName());
            Price productPrice = new Price(request.getPrice());
            Product p = productService.addProduct(productName, productPrice, request.getAllergens(), request.getToppings());
            return ResponseEntity.ok(p.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Gets all product names.
     *
     * @return all the product names in a list
     */
    @GetMapping("/listAllProductNames")
    public ResponseEntity<List<String>> listAllProductNames() {
        List<Product> list = productService.listAllProducts();
        List<String> ret = list.stream().map(Product::toUserString).collect(Collectors.toList());

        return ResponseEntity.ok(ret);
    }

    /**
     * Gets all products.
     *
     * @return all the products in a list
     */
    @GetMapping("/listAllProducts")
    public ResponseEntity<List<Product>> listAllProducts() {

        return ResponseEntity.ok(productService.listAllProducts());
    }

    /**
     * Verifies all the products from the list
     *
     * @return valid products with additional fields
     */
    @PostMapping("/verifyProducts")
    public ResponseEntity<List<Item>> verifyProducts(@RequestBody VerifyItemModel request,
                                                     @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(productService.verifyProducts(request.getNames(), token));
    }
}
