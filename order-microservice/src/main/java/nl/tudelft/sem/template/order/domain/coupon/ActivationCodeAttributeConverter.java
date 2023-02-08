package nl.tudelft.sem.template.order.domain.coupon;

import javax.persistence.AttributeConverter;

/**
 * JPA Converter for the ActivationCode value object
 */
public class ActivationCodeAttributeConverter implements AttributeConverter<ActivationCode, String> {

    @Override
    public String convertToDatabaseColumn(ActivationCode attribute) {
        return attribute.toString();
    }

    @Override
    public ActivationCode convertToEntityAttribute(String dbData) {
        return new ActivationCode(dbData);
    }
}
