package nl.tudelft.sem.template.authentication.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the Email value object.
 */
@Converter
public class AllergenAttributeConverter implements AttributeConverter<Allergens, String> {

    @Override
    public String convertToDatabaseColumn(Allergens attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public Allergens convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new Allergens();
        }
        return new Allergens(dbData);
    }


}

