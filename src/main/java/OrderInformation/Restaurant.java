package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Record to represent a restaurant.
 * Stores the longitude, latitude, menu, and number of moves required to return to appleton tower.
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
     * Accessor method for the longitude.
     *
     * @return the longitude of the restaurant.
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Accessor method for the latitude.
     *
     * @return the latitude of the restaurant.
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Mutator method for the number of moves from Appleton Tower.
     *
     * @param noOfMoves The number of moves from Appleton Tower.
     */
    public void setNumberOfMovesFromAppleton(int noOfMoves) {
        if (noOfMoves > 0) {
            this.numberOfMovesFromAppletonTower = noOfMoves;
        } else {
            System.err.println("Number of moves must be greater than zero.");
        }
    }

    /**
     * Accessor method for the number of moves from Appleton Tower.
     *
     * @return The number of moves from Appleton Tower.
     */
    public int getNumberOfMovesFromAppletonTower() {
        return this.numberOfMovesFromAppletonTower;
    }
}
