package OrderInformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent a pizza in an order.
 */
public class Pizza {
    @JsonProperty("name")
    private String name;

    @JsonProperty("priceInPence")
    private int priceInPence;
    
    @JsonCreator
    public Pizza(@JsonProperty("name") String name, @JsonProperty("priceInPence") int priceInPence) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (priceInPence <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        this.name = name;
        this.priceInPence = priceInPence;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getPriceInPence() {
        return priceInPence;
    }
}
