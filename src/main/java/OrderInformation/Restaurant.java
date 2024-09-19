package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent a restaurant.
 * Stores the location, menu, and number of moves required to return to appleton tower.
 */
public class Restaurant {
    
    @JsonProperty("name")
    private String name;

    @JsonProperty("location")
    private Location location; // Inner class to hold longitude and latitude

    @JsonProperty("openingDays")
    private String[] openingDays; // Array to hold the opening days

    @JsonProperty("menu")
    private Menu[] menu;

    private int numberOfMovesFromAppletonTower;

    /**
     * Inner class to represent the location with longitude and latitude.
     */
    public static class Location {
        @JsonProperty("lng")
        private double longitude;

        @JsonProperty("lat")
        private double latitude;

        // Getters
        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }

    /**
     * Accessor method for the menu.
     *
     * @return The array of menu records for this restaurant.
     */
    public Menu[] getMenu() {
        return menu;
    }

    /**
     * Accessor method for the longitude.
     *
     * @return The longitude of the restaurant.
     */
    public double getLongitude() {
        return this.location.getLongitude();
    }

    /**
     * Accessor method for the latitude.
     *
     * @return The latitude of the restaurant.
     */
    public double getLatitude() {
        return this.location.getLatitude();
    }

    /**
     * Accessor method for the opening days.
     *
     * @return the array of opening days for this restaurant.
     */
    public String[] getOpeningDays() {
        return openingDays;
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
