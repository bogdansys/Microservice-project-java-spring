package nl.tudelft.sem.template.authentication.controllers;

import nl.tudelft.sem.template.authentication.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.authentication.JwtUserDetailsService;
import nl.tudelft.sem.template.authentication.domain.user.*;
import nl.tudelft.sem.template.authentication.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class AuthenticationController {

    private final transient AuthenticationManager authenticationManager;

    private final transient JwtTokenGenerator jwtTokenGenerator;

    private final transient JwtUserDetailsService jwtUserDetailsService;

    private final transient RegistrationService registrationService;

    private final transient PasswordHashingService passwordHashingService;

    private final transient UserRepository userRepository;

    private final transient AuthManager authManager;


    /**
     * Instantiates a new UsersController.
     *
     * @param authenticationManager the authentication manager
     * @param jwtTokenGenerator     the token generator
     * @param jwtUserDetailsService the user service
     * @param registrationService   the registration service
     */
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenGenerator jwtTokenGenerator,
                                    JwtUserDetailsService jwtUserDetailsService,
                                    RegistrationService registrationService, PasswordHashingService passwordHashingService,
                                    UserRepository userRepository,
                                    AuthManager authManager) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.registrationService = registrationService;
        this.passwordHashingService = passwordHashingService;
        this.userRepository = userRepository;
        this.authManager = authManager;
    }

    /**
     * Endpoint for authentication.
     *
     * @param request The login model
     * @return JWT token if the login is successful
     * @throws Exception if the user does not exist or the password is incorrect
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseModel> authenticate(@RequestBody AuthenticationRequestModel request) throws Exception {
        validateInput(request);
        authenticateUser(request);
        return generateToken(request);
    }

    /**
     * validates user input for login.
     */
    private void validateInput(AuthenticationRequestModel request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is null");
        }
    }

    /**
     * Authenticates the user.
     * @param request
     */
    private void authenticateUser(AuthenticationRequestModel request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }
    }

    /**
     * Generates a JWT token.
     * @param request
     * @return
     */
    private ResponseEntity<AuthenticationResponseModel> generateToken(AuthenticationRequestModel request) {
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getEmail());
        Email email = new Email(request.getEmail());
        final Role role = userRepository.findByEmail(email).get().getRole();
        final String jwtToken = jwtTokenGenerator.generateToken(userDetails, role);
        return ResponseEntity.ok(new AuthenticationResponseModel(jwtToken));
    }



    @PostMapping("/addAllergen")
    public ResponseEntity addAllergen(@RequestBody AddAllergenRequestModel request) {
        //checks if user is a customer, if not, they cannot add allergens
        if(!authManager.getRole().equals("USER")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only users are able to get allergens");
        }

        if (request.getAllergenId() < 1 || request.getAllergenId() > 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong allergen value");
        }

        String email = authManager.getEmail();
        Email emailObject = new Email(email);
        int allergenId = request.getAllergenId();
        UserRole userRole = new UserRole(userRepository, emailObject);
        return userRole.addAllergen(allergenId);
    }

    /**
     * endpoint for deleting allergen
     *
     * @param request
     * @return
     */
    @PostMapping("/removeAllergen")
    public ResponseEntity removeAllergen(@RequestBody AddAllergenRequestModel request) {
        //checks if user is a customer, if not, they cannot remove allergens
        if(!authManager.getRole().equals("USER")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only users are able to remove allergens");
        }

        String email = authManager.getEmail();
        Email emailObject = new Email(email);
        UserRole userRole = new UserRole(userRepository, emailObject);
        return userRole.removeAllergen(request.getAllergenId());
    }

    /**
     * endpoint for deleting a user
     */
    @PostMapping("/deleteUserByAdmin")
    public ResponseEntity deleteUser(@RequestBody DeleteUserRequestModel request) {
        Role role = authManager.getRoleObject();
        String roleString = role.getRole();
        if (!roleString.equals("ADMIN")) {
            return ResponseEntity.ok(new AllUsersResponseModel(null));
        }
        Email emailObject = new Email(request.getEmail());
        AdminRole admin = new AdminRole(userRepository, authManager.getEmailObject());
        return admin.deleteUser(emailObject);
    }


    /**
     * Endpoint for registration.
     *
     * @param request The registration model
     * @return 200 OK if the registration is successful
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request) throws Exception {
        try {
            Email email = new Email(request.getEmail());
            Password password = new Password(request.getPassword());
            Role role = Role.createRole(request.getRole());
            if (role.getRole().equals("STORE_OWNER")) {
                role = (StoreOwnerRole) role;
                ((StoreOwnerRole) role).setStoreId(request.getStoreId());
            }
            registrationService.registerUser(email, password, role);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok("User registered");
    }

    /**
     * endpoint for seeing all users
     */
    @GetMapping("/users")
    public ResponseEntity<AllUsersResponseModel> getUsers() {
        Role role = authManager.getRoleObject();
        String roleString = role.getRole();
        if (!roleString.equals("ADMIN")) {
            return ResponseEntity.ok(new AllUsersResponseModel(null));
        }
        AdminRole admin = new AdminRole(userRepository, authManager.getEmailObject());
        return admin.getAllUsers();
    }

    /**
     * endpoint that returns all alergens of a user
     *
     * @return
     */
    @GetMapping("/getAllergens")
    public ResponseEntity<AllergenResponseModel> getAllergens() {

        //checks if user is a customer, if not, they cannot get allergens
        if(!authManager.getRole().equals("USER")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only users are able to get allergens");
        }

        Email email = new Email(authManager.getEmail());
        Optional<AppUser> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user doesn't exist");
        }
        UserRole role = new UserRole(userRepository, email);
        return role.getAllergens(email);
    }


    /**
     * Endpoint for changing password.
     * requires both old and new passwords andmake sure the old password is correct
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) throws Exception {
        String email = request.getEmail();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        UserRole userRole = new UserRole(userRepository, new Email(email));
        return userRole.changePassword(email, oldPassword, newPassword, authenticationManager, passwordHashingService);
    }

    /**
     * endpoint for obtaining a store id
     * only store owners can use this endpoint, and it returns the store id of the store owner
     */
    @GetMapping("/getStoreId")
    public ResponseEntity<StoreIdResponseModel> getStoreId() {
        Email email = new Email(authManager.getEmail());
        Role role = authManager.getRoleObject();
        return StoreOwnerRole.getStoreIdFromEmail(userRepository, email, role);
    }

}
