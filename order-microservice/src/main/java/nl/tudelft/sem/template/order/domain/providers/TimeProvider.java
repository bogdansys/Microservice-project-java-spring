package nl.tudelft.sem.template.order.domain.providers;

import java.time.LocalDateTime;

public interface TimeProvider {
    /**
     * Returns a time
     * @return the LocalDateTime storing the time
     */
    LocalDateTime getTime();
}
