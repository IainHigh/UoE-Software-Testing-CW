package OrderInformation;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public Restaurant(@JsonProperty("name") String name, @JsonProperty("location") Location location,
            @JsonProperty("openingDays") String[] openingDays, @JsonProperty("menu") Menu[] menu) {

        // Name cannot be null or empty
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        // Location cannot be null
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        // Opening days can only be a list consisting of the strings "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" each at most once.
        if (!validOpenDays(openingDays)) {
            throw new IllegalArgumentException("Invalid opening days");
        }

        // Menu cannot be null
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null");
        }
        
        this.name = name;
        this.location = location;
        this.openingDays = openingDays;
        this.menu = menu;
    }

    private boolean validOpenDays(String[] openingDays) {
        // Define the valid days in order
        List<String> validDays = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", 
                                            "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        int lastIndex = -1; // Track the last valid index
        for (String day : openingDays) {
            int currentIndex = validDays.indexOf(day);
            if (currentIndex == -1 || currentIndex <= lastIndex) {
                // Day is invalid or out of order
                return false;
            }
            lastIndex = currentIndex;
        }
        return true;
    }


    private int numberOfMovesFromAppletonTower;

    /**
     * Inner class to represent the location with longitude and latitude.
     */
    public static class Location {
        @JsonProperty("lng")
        private double longitude;

        @JsonProperty("lat")
        private double latitude;

        @JsonCreator
        public Location(@JsonProperty("lng") double longitude, @JsonProperty("lat") double latitude) {
            // Longitude must be between -180 and 180
            if (longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Longitude must be between -180 and 180");
            }
            // Latitude must be between -90 and 90
            if (latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("Latitude must be between -90 and 90");
            }
            this.longitude = longitude;
            this.latitude = latitude;
        }

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
     * Mutator method for the number of moves from Appleton Tower.
     *
     * @param noOfMoves The number of moves from Appleton Tower.
     */
    public void setNumberOfMovesFromAppleton(int noOfMoves) {
        if (noOfMoves > 0) {
            this.numberOfMovesFromAppletonTower = noOfMoves;
        } else {
            System.err.println("Number of moves must be greater than zero.");
            throw new IllegalArgumentException();
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

    /**
     * Accessor method for the name of the restaurant.
     *
     * @return The name of the restaurant.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Accessor method for the opening days of the restaurant.
     *
     * @return The opening days of the restaurant.
     */
    public String[] getOpeningDays() {
        return this.openingDays;
    }
}
