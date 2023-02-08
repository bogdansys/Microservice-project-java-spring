package nl.tudelft.sem.template.menu.domain;

import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the Price value object.
 */
@Converter
public class PriceAttributeConverter implements AttributeConverter<Price, Double> {

    @Override
    public Double convertToDatabaseColumn(Price attribute) {
        return attribute.getValue();
    }

    @SneakyThrows
    @Override
    public Price convertToEntityAttribute(Double dbData) {
        return new Price(dbData);
    }

}

