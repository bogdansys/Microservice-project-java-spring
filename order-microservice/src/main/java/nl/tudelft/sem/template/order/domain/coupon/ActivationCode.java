package nl.tudelft.sem.template.order.domain.coupon;

import lombok.Getter;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A DDD value object representing the activation code of a coupon
 * Note: Implements Serializable for the JSON conversion with the database
 */
public class ActivationCode implements Serializable {
    static final long serialVersionUID = 12312312321L;

    @Getter
    private final transient String activationCodeSequence;

    public ActivationCode(String activationCode){
        this.activationCodeSequence = activationCode;
    }

    /**
     * Checks if the activation code is a valid, meaning there has to be 6 characters, with the first
     * four being alphabetical letters and the last two being numbers
     * @return a boolean if the activation code is valid
     */
    public boolean isActivationCodeValid(){
        Pattern pattern = Pattern.compile("^[a-zA-Z]{4}[0-9]{2}$");
        Matcher matcher = pattern.matcher(activationCodeSequence);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return activationCodeSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivationCode)) return false;

        ActivationCode that = (ActivationCode) o;

        return getActivationCodeSequence().equals(that.getActivationCodeSequence());
    }

    @Override
    public int hashCode() {
        return getActivationCodeSequence().hashCode();
    }
}
