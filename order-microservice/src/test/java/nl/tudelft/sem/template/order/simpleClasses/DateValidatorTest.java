package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.DateValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DateValidatorTest {

    @Test
    void testValidDate(){
        boolean d1 = DateValidator.isValid("2025-10-09");
        boolean d2 = DateValidator.isValid("2020-12-31");
        boolean d3 = DateValidator.isValid("2022-12-12");
        boolean d4 = DateValidator.isValid("2000-01-01");
        boolean d5 = DateValidator.isValid("0001-01-01");
        boolean d6 = DateValidator.isValid("3000-01-01");

        assertThat(d1).isTrue();
        assertThat(d2).isTrue();
        assertThat(d3).isTrue();
        assertThat(d4).isTrue();
        assertThat(d5).isTrue();
        assertThat(d6).isTrue();
    }

    @Test
    void testMonthTooHigh(){
        assertThat(DateValidator.isValid("2020-13-01")).isFalse();
    }

    @Test
    void testMonthTooLow(){
        assertThat(DateValidator.isValid("2020-00-01")).isFalse();
    }

    @Test
    void testDayTooHigh(){
        assertThat(DateValidator.isValid("2020-02-32")).isFalse();
    }

    @Test
    void testDayTooLow(){
        assertThat(DateValidator.isValid("2020-12-00")).isFalse();
    }

    @Test
    void wrongFormatUsesSlashes(){
        assertThat(DateValidator.isValid("2022/01/01")).isFalse();
    }

    @Test
    void wrongFormatUsesDots(){
        assertThat(DateValidator.isValid("2022.01.01")).isFalse();
    }

    @Test
    void wrongFormatUsesNothing(){
        assertThat(DateValidator.isValid("20220101")).isFalse();
    }

    @Test
    void testMonthFirst(){
        assertThat(DateValidator.isValid("12-31-2022")).isFalse();
    }

    @Test
    void testDayFirst(){
        assertThat(DateValidator.isValid("31-12-2022")).isFalse();
    }

    @Test
    void testYearInMiddle(){
        assertThat(DateValidator.isValid("12-2022-12")).isFalse();
    }
}
