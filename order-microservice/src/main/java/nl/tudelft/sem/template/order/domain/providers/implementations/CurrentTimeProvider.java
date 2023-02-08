package nl.tudelft.sem.template.order.domain.providers.implementations;

import nl.tudelft.sem.template.order.domain.providers.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CurrentTimeProvider implements TimeProvider {

    /**
     * Returns the current time
     * @return the LocalDateTime object containing the current time
     */
    @Override
    public LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}
