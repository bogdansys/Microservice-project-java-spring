package nl.tudelft.sem.template.order.domain.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.template.order.domain.order.Order;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Table(name = "product")
@NoArgsConstructor
@Entity
public class Product implements Serializable {
    static final long serialVersionUID = 13243546576879L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "productId")
    @NotNull
    @Getter
    @Setter // only used for testing
    private Long id;

    @Getter
    @NotNull
    private String name;

    @ElementCollection
    @Getter
    @Setter
    private List<String> toppingNames;

    @Getter
    @Setter
    private double price;

    @ElementCollection
    @Getter
    @Setter
    private List<Integer> allergens;

    @Getter
    @Setter
    private boolean isAllergic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Getter
    // Cascade Types: https://www.baeldung.com/jpa-cascade-types
    @JsonIgnore
    private Order order;

    public Product(String name, List<String> toppingNames, double price,
                   List<Integer> allergens, Order order, boolean isAllergic) {
        this.name = name;
        this.toppingNames = toppingNames;
        this.price = price;
        this.allergens = allergens;
        this.order = order;
        this.isAllergic = isAllergic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product product = (Product) o;
        if (id != null && product.id != null) {
            return Objects.equals(id, product.id);
        }
        return price == product.getPrice() && isAllergic == product.isAllergic() && allergens.equals(product.getAllergens())
                && toppingNames.equals(product.getToppingNames());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : getName().hashCode();
    }

    @Override
    public String toString() {
        String toppings = this.toppingNames == null || this.toppingNames.size() == 0 ?
                "Toppings: none" :
                "Toppings: " + this.toppingNames.toString().replace("[", "").replace("]", "");

        String allergens = this.allergens == null || this.allergens.size() == 0 ?
                "Allergens: none" :
                "Allergens: " + this.allergens.toString().replace("[", "").replace("]", "");

        String price = "Price: " + this.price;

        if (!isAllergic) {
            return this.id + "; " + this.name + "; " + toppings + "; " + allergens + "; " + price;
        } else {
            String warning = "User may be allergic to this product";

            return this.id + "; " + this.name + "; " + toppings + "; " + warning + "; " + allergens + "; " + price;
        }
    }
}
