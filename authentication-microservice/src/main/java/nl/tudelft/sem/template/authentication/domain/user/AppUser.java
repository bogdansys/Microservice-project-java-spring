package nl.tudelft.sem.template.authentication.domain.user;

import javax.persistence.*;

import lombok.NoArgsConstructor;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
public class AppUser {

    //make every user type decorate it s own jwt token
    /**
     * Identifier for the application user.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "email", nullable = false, unique = true)
    @Convert(converter = EmailAttributeConverter.class)
    private Email email;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword password;

    @Column(name = "role", nullable = false)
    @Convert(converter = RoleAttributeConverter.class)
    private Role role;

    @Column(name = "allergens")
    @Convert(converter = AllergenAttributeConverter.class)
    private Allergens allergens;

    @Column
    private int storeId;

    /**
     * Create new application user.
     *
     * @param password The password for the new user
     */
    public AppUser(Email email, HashedPassword password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        if (role.getRole().equals("STORE_OWNER")) {
            this.storeId = ((StoreOwnerRole) role).storeId;
        } else {
            this.storeId = -1;
        }
        this.allergens = new Allergens();
    }

    public Email getEmail() {
        return email;
    }

    public HashedPassword getPassword() {
        return password;
    }

    /**
     * sets the password
     *
     * @param password
     */
    public void setPassword(HashedPassword password) {
        this.password = password;
    }

    public Allergens getAllergens() {
        return allergens;
    }

    public Role getRole() {
        return role;
    }


    public int getStoreId() {
        return storeId;
    }

    @Override
    public String toString() {
        return "AppUser{"
                + "id=" + id
                + ", email=" + email.toString()
                + ", password=" + password.toString()
                + ", role=" + role.getRole()
                + ", allergens=" + allergens.toString()
                + ", storeId=" + storeId
                + '}';
    }
}
