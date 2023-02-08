package nl.tudelft.sem.template.order.domain.store;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the StoreName value object.
 */
@Converter
public class StoreNameAttributeConverter implements AttributeConverter<StoreName, String> {

    @Override
    public String convertToDatabaseColumn(StoreName attribute) {
        return attribute.toString();
    }

    @Override
    public StoreName convertToEntityAttribute(String dbData) {
        return new StoreName(dbData);
    }

}
