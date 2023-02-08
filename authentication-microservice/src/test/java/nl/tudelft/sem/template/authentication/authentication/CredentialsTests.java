package nl.tudelft.sem.template.authentication.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CredentialsTests {

    @Test
    void testCredentialsConstructorFull(){
        Credentials c = new Credentials("name", "role");
        assertThat(c.name).isEqualTo("name");
        assertThat(c.role).isEqualTo("role");
    }

    @Test
    void testCredentialsConstructorNull(){
        Credentials c = new Credentials(null, null);
        assertThat(c.name).isNull();
        assertThat(c.role).isNull();
    }
}
