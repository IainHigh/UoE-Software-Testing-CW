package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent a restaurant menu item.
 */
public class Menu {
    private final String name;
    private final int priceInPence;

    /**
     * Constructor to create a Menu item.
     *
     * @param name         the name of the menu item.
     * @param priceInPence the price of the menu item in pence.
     */
    public Menu(@JsonProperty("name") String name,
                @JsonProperty("priceInPence") int priceInPence) {
        this.name = name;
        this.priceInPence = priceInPence;
    }

    /**
     * Accessor method for the name of the menu item.
     *
     * @return the name of the menu item.
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor method for the price of the menu item in pence.
     *
     * @return the price of the menu item in pence.
     */
    public int getPriceInPence() {
        return priceInPence;
    }
}
