package nl.tudelft.sem.template.menu.domain.topping;

import nl.tudelft.sem.template.menu.domain.Allergen;
import nl.tudelft.sem.template.menu.domain.InvalidAllergenException;
import nl.tudelft.sem.template.menu.domain.Price;
import nl.tudelft.sem.template.menu.domain.item.Item;
import nl.tudelft.sem.template.menu.model.AllergenResponseModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A DDD service for adding a new topping.
 */
@Service
public class ToppingService {
    private final transient ToppingRepository toppingRepository;
    private final transient RestTemplate restTemplate;

    /**
     * Instantiates a new ToppingService.
     *
     * @param toppingRepository  the topping repository
     */
    public ToppingService(ToppingRepository toppingRepository, RestTemplate restTemplate) {
        this.toppingRepository = toppingRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Add a new topping.
     *
     * @param name    The Name of the topping
     * @param price    The price of the topping
     * @param allergens    The Name of the topping
     * @throws Exception if the topping already exists
     */
    public Topping addTopping(ToppingName name, Price price, List<Integer> allergens) throws Exception {

        if (isNameUnique(name)) {
            List<Allergen> convertedAllergens = verifyAllergens(allergens);
            Topping newTopping = new Topping(name, price, convertedAllergens);
            toppingRepository.save(newTopping);
            return newTopping;
        }

        throw new ToppingNameAlreadyInUseException(name);
    }

    /**
     * Lists all toppings in the database
     * @return list of toppings
     */
    public List<Topping> listAllToppings() {
        return toppingRepository.findAll();
    }

    public boolean isNameUnique(ToppingName name) {
        return !toppingRepository.existsByName(name);
    }

    public List<Integer> getInvalidToppings(List<Integer> toppings) {
        List<Integer> l = toppingRepository.findAllById(toppings).stream().map(t -> t.getId()).collect(Collectors.toList());
        List<Integer> ret = new ArrayList<>(toppings);
        ret.removeAll(l);
        return ret;
    }

    /**
     * Checks whether a topping with the specified name exists in the database
     * @param names of the toppings to check
     * @return Item or null
     */
    @SuppressWarnings("PMD")
    public List<Item> verifyToppings(List<String> names, String token) {

        String allergens = getUserAllergens(token);

        return names.stream().map(name -> {
            ToppingName n = new ToppingName(name);
            Optional<Topping> optTopping = toppingRepository.findByName(n);
            if (optTopping.isEmpty()) {
                return null;
            } else {
                Topping p = optTopping.get();
                boolean warning = false;
                for (Allergen a : p.getAllergens()) {
                    boolean x = allergens.contains(a.toString());
                    warning = x ? true : warning;
                }
                return new Item(p.getName().getName(),
                        p.getPrice().getValue(),
                        p.getAllergens().stream().map(a -> a.getValue()).collect(Collectors.toList()),
                        warning);
            }
        }).collect(Collectors.toList());
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

    /**
     * Retrieves the allergens of a user based on a token
     * @param token auth
     * @return String of allergens
     */
    public String getUserAllergens(String token) {
        HttpHeaders authentication = new HttpHeaders();
        authentication.setContentType(MediaType.APPLICATION_JSON);
        authentication.set("Authorization", token);

        // It is missing authentication
        ResponseEntity<AllergenResponseModel> productInfoResponse = restTemplate
                .exchange("http://localhost:8081/getAllergens", HttpMethod.GET,
                        new HttpEntity<>(null, authentication),
                        AllergenResponseModel.class);

        return productInfoResponse.getBody().getAllergen();
    }

}
