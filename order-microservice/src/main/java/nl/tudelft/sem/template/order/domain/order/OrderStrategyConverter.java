package nl.tudelft.sem.template.order.domain.order;


import nl.tudelft.sem.template.order.domain.coupon.ApplyBOGOCouponStrategy;
import nl.tudelft.sem.template.order.domain.coupon.ApplyDiscountCouponStrategy;
import nl.tudelft.sem.template.order.domain.coupon.PricingStrategy;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the ActivationCode value object
 */
@Converter
public class OrderStrategyConverter implements AttributeConverter<PricingStrategy, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(PricingStrategy attribute) {
        return attribute.toString();
    }

    @Override
    public PricingStrategy convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }
        String[] pieces = dbData.split(SEPARATOR);
        if(pieces.length==0){
            return null;
        }

        if(pieces[0].equals("ApplyBOGOCouponStrategy")){
            return new ApplyBOGOCouponStrategy();
        }
        // here we can't save Double.parseDouble(s[1]) in a variable
        // because pmd doesn't like it
        if(pieces[0].equals("ApplyDiscountCouponStrategy")){
            return new ApplyDiscountCouponStrategy(Double.parseDouble(pieces[1]));
        }
        return new BasicPricingStrategy();
    }

}


