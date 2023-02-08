package nl.tudelft.sem.template.authentication.domain.user;

/**
 * Exception to indicate the NetID is already in use.
 */
public class EmailAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;
    
    public EmailAlreadyInUseException(Email email) {
        super(email.toString());
    }
}
