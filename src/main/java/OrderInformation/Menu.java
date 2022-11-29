package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Record to represent a restaurant menu item.
 *
 * @param name         The name of the menu item.
 * @param priceInPence The price of the menu item in pence.
 */
record Menu(@JsonProperty("name") String name,
            @JsonProperty("priceInPence") int priceInPence) {
}
