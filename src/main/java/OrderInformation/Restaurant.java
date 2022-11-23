package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ed.inf.Constants;
import uk.ac.ed.inf.LngLat;

/**
 * Record to represent a restaurant.
 * Stores the name, longitude, latitude, and number of moves required to return to appleton tower.
 */
public class Restaurant {
    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("menu")
    private Menu[] menu;

    private int numberOfMovesFromAppletonTower;

    /**
     * Accessor method for the menu.
     *
     * @return the array of menu records for this restaurant.
     */
    public Menu[] getMenu() {
        return menu;
    }

    /**
     * Converts and returns the longitude and latitude of the restaurant as a LngLat object.
     *
     * @return the LngLat object representing the longitude and latitude of the restaurant.
     */
    public LngLat getLngLat() {
        return new LngLat(longitude, latitude);
    }

    /**
     * If the number of moves to return to appleton tower has not yet been calculated, calculates it.
     * Otherwise, just an accessor method for this value.
     *
     * @return the number of moves required to return to appleton tower.
     */
    public int getNumberOfMovesFromAppletonTower() {
        if (numberOfMovesFromAppletonTower == 0) {
            this.numberOfMovesFromAppletonTower = this.getLngLat().numberOfMovesTo(Constants.APPLETON_TOWER);
        }
        return numberOfMovesFromAppletonTower;
    }
}
