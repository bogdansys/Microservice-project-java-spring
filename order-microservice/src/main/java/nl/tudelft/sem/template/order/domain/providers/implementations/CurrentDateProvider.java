package nl.tudelft.sem.template.order.domain.providers.implementations;

import nl.tudelft.sem.template.order.domain.providers.DateProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CurrentDateProvider implements DateProvider {
    /**
     * Gets the current date
     * @return the LocalDate object containing the current date
     */
    @Override
    public LocalDate getDate() {
        return LocalDate.now();
    }
}
