package nl.tudelft.sem.template.order.simpleClasses;

import nl.tudelft.sem.template.order.domain.store.StoreName;
import nl.tudelft.sem.template.order.domain.store.StoreNameAttributeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class StoreNameAttributeConverterTest {
    private transient StoreNameAttributeConverter storeNameAttributeConverter;

    @BeforeEach
    void setup() {
        storeNameAttributeConverter = new StoreNameAttributeConverter();
    }

    @Test
    void testConvertToDatabaseColumn() {
        assertThat(storeNameAttributeConverter.convertToDatabaseColumn(new StoreName("test"))).isEqualTo("test");
    }

    @Test
    void testConvertToEntityAttribute() {
        String expected = storeNameAttributeConverter.convertToEntityAttribute("test").toString();
        assertThat(expected).isEqualTo("test");
    }
}
