package nl.tudelft.sem.template.order.domain.store;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DDD entity representing a store
 */
@Entity
@Table(name = "stores")
@NoArgsConstructor
public class Store implements Serializable {
    static final long serialVersionUID = 1231231233213312L;

    /**
     * Identifier for the store.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @Getter
    @Setter // used for testing
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    @Convert(converter = StoreNameAttributeConverter.class)
    @Getter
    private StoreName name;

    /**
     * Create new store.
     *
     * @param name The Name for the new store
     */
    public Store(StoreName name) {
        this.name = name;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Store other = (Store) o;
        return id == (other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return this.name.toString();
    }
}
