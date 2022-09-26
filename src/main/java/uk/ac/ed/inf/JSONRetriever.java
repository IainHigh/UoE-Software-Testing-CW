package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;

class CentralAreaPoint {
    public String name;
    public double longitude;
    public double latitude;
}

public class JSONRetriever {
    /**
     * Retrieves the orders from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of orders.
     */
    public Order[] getOrders(URL url) {

        try {
            return new ObjectMapper().readValue(url, Order[].class);
        } catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Retrieves the central area border from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of points that make up the border of the central area.
     */
    public CentralAreaPoint[] getCentralArea(URL url) {

        try {
            return new ObjectMapper().readValue(url, CentralAreaPoint[].class);
        } catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Retrieves the restaurants from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of restaurants.
     */
    public Restaurant[] getRestaurants(URL url) {

        try {
            return new ObjectMapper().readValue(url, Restaurant[].class);
        } catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
