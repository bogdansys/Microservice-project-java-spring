package nl.tudelft.sem.template.menu.domain;


/**
 * Exception to indicate that the allergen value is invalid
 */
public class InvalidAllergenException extends Exception {
    static final long serialVersionUID = -338751699349998L;

    public InvalidAllergenException(int allergen) {
        super(String.valueOf(allergen));
    }
}
