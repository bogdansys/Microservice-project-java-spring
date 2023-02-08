package nl.tudelft.sem.template.menu.domain;

import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the Price value object.
 */
@Converter
public class AllergenAttributeConverter implements AttributeConverter<Allergen, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Allergen attribute) {
        return attribute.getValue();
    }

    @SneakyThrows
    @Override
    public Allergen convertToEntityAttribute(Integer dbData) {
        return new Allergen(dbData);
    }

}

