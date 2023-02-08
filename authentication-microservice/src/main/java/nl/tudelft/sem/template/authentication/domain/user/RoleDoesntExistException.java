package nl.tudelft.sem.template.authentication.domain.user;

/**
 * exception for when a role doesn't exist.
 */

public class RoleDoesntExistException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for RoleDoesntExistException.
     *
     * @param role the role that doesn't exist
     */
    public RoleDoesntExistException(String role) {
        super("Role " + role + " doesn't exist "+" " +
                "You Should use: USER / ADMIN / STORE_OWNER");
    }

}
