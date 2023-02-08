package nl.tudelft.sem.template.menu.domain.product;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the ProductName value object.
 */
@Converter
public class ProductNameAttributeConverter implements AttributeConverter<ProductName, String> {

    @Override
    public String convertToDatabaseColumn(ProductName attribute) {
        return attribute.toString();
    }

    @Override
    public ProductName convertToEntityAttribute(String dbData) {
        return new ProductName(dbData);
    }

}

