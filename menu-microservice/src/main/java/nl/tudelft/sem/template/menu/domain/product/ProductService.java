package nl.tudelft.sem.template.menu.domain.product;

import nl.tudelft.sem.template.menu.domain.Allergen;
import nl.tudelft.sem.template.menu.domain.InvalidAllergenException;
import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.item.Item;
import nl.tudelft.sem.template.menu.domain.topping.InvalidToppingsException;
import nl.tudelft.sem.template.menu.domain.topping.ToppingService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A DDD service for adding a new product.
 */
@Service
public class ProductService {
    private final transient ProductRepository productRepository;
    private final transient ToppingService toppingService;

    /**
     * Instantiates a new ProductService.
     *
     * @param productRepository  the product repository
     * @param toppingService  the topping service
     */
    public ProductService(ProductRepository productRepository, ToppingService toppingService) {
        this.productRepository = productRepository;
        this.toppingService = toppingService;
    }

    /**
     * Add a new product.
     *
     * @param name    The Name of the product
     * @param price    The price of the product
     * @param list1    The allergens of the product
     * @param list2    The toppings of the product
     * @throws Exception if the product already exists
     */
    public Product addProduct(ProductName name, Price price, List<Integer> list1, List<Integer> list2) throws Exception {

        if (isStringUnique(name)) {
            List<Integer> invalidToppings = toppingService.getInvalidToppings(list2);
            if (invalidToppings.size() == 0) {
                // Create new product
                List<Allergen> convertedAllergens = verifyAllergens(list1);
                Product newProduct = new Product(name, price, convertedAllergens, list2);
                productRepository.save(newProduct);

                return newProduct;
            }
            throw new InvalidToppingsException(invalidToppings);
        }

        throw new ProductNameAlreadyInUseException(name);
    }

    /**
     * Lists all products in the database
     * @return list of products
     */
    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    public boolean isStringUnique(ProductName name) {
        return !productRepository.existsByName(name);
    }

    /**
     * Checks whether a product with the specified name exists in the database
     * @param names of the products to check
     * @return List of items or null
     */
    @SuppressWarnings("PMD")
    public List<Item> verifyProducts(List<String> names, String token) {
        String allergens = toppingService.getUserAllergens(token);
        return names.stream().map(name -> {
            ProductName n = new ProductName(name);
            return createVerifiedProduct(allergens, n);
        }).collect(Collectors.toList());
    }

    /**
     * Checks a products allergens against user allergens
     * @param allergens
     * @param userAllergens
     * @return true if the user is allergic to the product
     */
    private boolean checkAllergens(List<Allergen> allergens, String userAllergens) {
        boolean warning = false;
        for (Allergen a : allergens) {
            warning |= userAllergens.contains(a.toString());
        }
        return warning;
    }

    /**
     * Creates an Item representing a product
     * @param userAllergens
     * @param name of the product
     * @return product
     */
    private Item createVerifiedProduct(String userAllergens, ProductName name) {
        Optional<Product> optProduct = productRepository.findByName(name);
        if (optProduct.isEmpty()) return null;
        Product p = optProduct.get();
        boolean warning = checkAllergens(p.getAllergens(), userAllergens);
        return new Item(p.getName().getName(), p.getPrice().getValue(),
                p.getAllergens().stream().map(a -> a.getValue()).collect(Collectors.toList()),
                warning);
    }

    /**
     * Checks whether a list of allergens has the correct values
     * @param allergens
     * @return list of converted allergens
     */
    public List<Allergen> verifyAllergens(List<Integer> allergens) throws InvalidAllergenException {
        List<Allergen> convertedAllergens = new ArrayList<>();

        for (Integer a : allergens) {
            convertedAllergens.add(new Allergen(a));
        }

        return convertedAllergens;
    }
}
