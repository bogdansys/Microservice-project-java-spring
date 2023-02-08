package nl.tudelft.sem.template.menu.domain.topping;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the ToppingName value object.
 */
@Converter
public class ToppingNameAttributeConverter implements AttributeConverter<ToppingName, String> {

    @Override
    public String convertToDatabaseColumn(ToppingName attribute) {
        return attribute.toString();
    }

    @Override
    public ToppingName convertToEntityAttribute(String dbData) {
        return new ToppingName(dbData);
    }

}

