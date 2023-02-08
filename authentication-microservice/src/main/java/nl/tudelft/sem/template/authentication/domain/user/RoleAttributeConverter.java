package nl.tudelft.sem.template.authentication.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RoleAttributeConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute.getRole();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData.equals("USER")) {
            return new UserRole();
        } else if (dbData.equals("ADMIN")) {
            return new AdminRole();
        } else if (dbData.equals("STORE_OWNER")) {
            return new StoreOwnerRole();
        } else {
            throw new IllegalArgumentException("Unknown value: " + dbData);
        }
    }
}
