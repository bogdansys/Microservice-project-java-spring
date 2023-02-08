package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.coupon.ActivationCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ActivationCodeTest {

    @Test
    void constructActivationCode(){
        ActivationCode code1 = new ActivationCode("ABDO04");
        assertThat(code1).isNotNull();
        ActivationCode code2 = new ActivationCode("Y03MMMM");
        assertThat(code2).isNotNull();
    }

    @Test
    void getActivationCodeSequence(){
        ActivationCode code1 = new ActivationCode("ABCD04");
        assertThat(code1.getActivationCodeSequence()).isEqualTo("ABCD04");
    }

    @Test
    void activationCodeToString(){
        ActivationCode code1 = new ActivationCode("ABCD04");
        assertThat(code1.toString()).isEqualTo("ABCD04");
    }

    @Test
    void testValidActivationCode(){
        ActivationCode code1 = new ActivationCode("ABCD04");
        assertThat(code1.isActivationCodeValid()).isTrue();
        ActivationCode code2 = new ActivationCode("YYYY19");
        assertThat(code2.isActivationCodeValid()).isTrue();
        ActivationCode code3 = new ActivationCode("mali99");
        assertThat(code3.isActivationCodeValid()).isTrue();
    }

    @Test
    void testInvalidActivationCode(){
        ActivationCode code1 = new ActivationCode("ABCD041");
        assertThat(code1.isActivationCodeValid()).isFalse();
        ActivationCode code2 = new ActivationCode("AB0D04");
        assertThat(code2.isActivationCodeValid()).isFalse();
        ActivationCode code3 = new ActivationCode("04ABCD");
        assertThat(code3.isActivationCodeValid()).isFalse();
    }

    @Test
    void testHashCode() {
        ActivationCode code1 = new ActivationCode("ABCD04");
        ActivationCode code2 = new ActivationCode("ABED08");

        assertThat(code1.hashCode()).isEqualTo(code1.hashCode());
        assertThat(code2.hashCode()).isNotEqualTo(code1.hashCode());
    }
}
