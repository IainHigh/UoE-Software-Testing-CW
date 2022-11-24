package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setNumberOfMovesFromAppleton(int noOfMoves) {
        this.numberOfMovesFromAppletonTower = noOfMoves;
    }

    public int getNumberOfMovesFromAppletonTower() {
        return this.numberOfMovesFromAppletonTower;
    }
}
