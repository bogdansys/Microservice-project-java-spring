package nl.tudelft.sem.template.authentication.domain.user;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class EmailTests {

    @Test
    void validEmails(){
        Email e1 = new Email("abc@gmail.com");
        assertThat(e1).isNotNull();

        Email e2 = new Email("c@g.c");
        assertThat(e2).isNotNull();

        Email e3 = new Email("gmail@cgc.");
        assertThat(e3).isNotNull();

        Email e4 = new Email("email@gmail");
        assertThat(e4).isNotNull();
    }

    @Test
    void testEmailToString(){
        Email e1 = new Email("abc@gmail.com");
        assertThat(e1.toString()).isEqualTo("abc@gmail.com");

        Email e2 = new Email("c@g.c");
        assertThat(e2.toString()).isEqualTo("c@g.c");
    }

    @Test
    void testInvalidEmailNoAtSymbol(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new Email("cgc.com");
        });
        assertThat(e.getMessage()).isEqualTo("Invalid email");
    }

    @Test
    void testNothingBeforeAt(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new Email("@cgc.com");
        });
        assertThat(e.getMessage()).isEqualTo("Invalid email");
    }

    @Test
    void testNothingAfterAt(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new Email("gmail@");
        });
        assertThat(e.getMessage()).isEqualTo("Invalid email");
    }
}
