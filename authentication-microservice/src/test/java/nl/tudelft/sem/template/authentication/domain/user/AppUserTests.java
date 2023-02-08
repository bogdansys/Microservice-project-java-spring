package nl.tudelft.sem.template.authentication.domain.user;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class AppUserTests {

    @Test
    void appUserConstructorStoreOwner(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new StoreOwnerRole();
        AppUser u = new AppUser(e, pass, r);
        assertThat(u).isNotNull();
    }

    @Test
    void appUserConstructorNotStoreOwner(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new AdminRole();
        AppUser u = new AppUser(e, pass, r);
        assertThat(u).isNotNull();
    }

    @Test
    void appUserStoreIdStoreOwner(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        StoreOwnerRole r = new StoreOwnerRole();
        r.setStoreId(1);
        AppUser u = new AppUser(e, pass, r);
        assertThat(u.getStoreId()).isEqualTo(r.getStoreId());
    }

    @Test
    void appUserStoreIdNotStoreOwner(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new AdminRole();
        AppUser u = new AppUser(e, pass, r);
        assertThat(u.getStoreId()).isEqualTo(-1);
    }

    @Test
    void appUserGetEmail(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new AdminRole();
        AppUser u = new AppUser(e, pass, r);
        assertThat(u.getEmail().toString()).isEqualTo("abc@gmail.com");
    }

    @Test
    void appUserGetPassword(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new AdminRole();
        AppUser u = new AppUser(e, pass, r);
        assertThat(u.getPassword().toString()).isEqualTo("abcd");
    }

    @Test
    void appUserSetPassword(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new AdminRole();
        AppUser u = new AppUser(e, pass, r);
        assertThat(u.getPassword().toString()).isEqualTo("abcd");
        u.setPassword(new HashedPassword("pass"));
        assertThat(u.getPassword().toString()).isEqualTo("pass");
    }

    @Test
    void appUserAllergens(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new AdminRole();
        AppUser u = new AppUser(e, pass, r);

        assertThat(u.getAllergens().toString()).isEqualTo("");
        u.getAllergens().addAllergen(1);
        assertThat(u.getAllergens().toString()).isEqualTo("1,");
        u.getAllergens().addAllergen(2);
        assertThat(u.getAllergens().toString()).isEqualTo("1,2,");
        assertThat(u.getAllergens().hasAllergen(1)).isTrue();
        assertThat(u.getAllergens().hasAllergen(2)).isTrue();
        u.getAllergens().removeAllergen(1);
        assertThat(u.getAllergens().toString()).isEqualTo("2,");
        assertThat(u.getAllergens().hasAllergen(2)).isTrue();
        assertThat(u.getAllergens().hasAllergen(1)).isFalse();
    }



    @Test
    void appUserGetRole(){
        Email e = new Email("abc@gmail.com");
        HashedPassword pass = new HashedPassword("abcd");
        Role r = new AdminRole();
        AppUser u = new AppUser(e, pass, r);

        assertThat(u.getRole()).isEqualTo(r);
    }

}
