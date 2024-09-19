package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent a pizza in an order.
 */
public class Pizza {
    @JsonProperty("name")
    private String name;

    @JsonProperty("priceInPence")
    private int priceInPence;

    // Getters
    public String getName() {
        return name;
    }

    public int getPriceInPence() {
        return priceInPence;
    }
}
