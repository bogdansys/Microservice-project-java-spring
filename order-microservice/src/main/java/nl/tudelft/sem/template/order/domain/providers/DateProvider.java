package nl.tudelft.sem.template.order.domain.providers;

import java.time.LocalDate;

public interface DateProvider {

    /**
     * Gets a date
     * @return LocalDate object storing date
     */
    LocalDate getDate();
}
