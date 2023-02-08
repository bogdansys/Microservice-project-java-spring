package nl.tudelft.sem.template.order.domain.coupon;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * JPA Converter for an expiration date
 */
@Converter
public class ExpirationDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        return attribute.format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        return LocalDate.parse(dbData);
    }
}
