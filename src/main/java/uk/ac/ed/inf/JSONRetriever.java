package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;

public class JSONRetriever {
    /**
     * Retrieves the orders from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of orders.
     */
    public Order[] getOrders(URL url) {
        try {
            return new ObjectMapper().readValue(url, Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the central area border from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of LngLat points that make up the border of the central area.
     */
    public LngLat[] getCentralArea(URL url) {
        try {
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(url, LngLat[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }
    }
}
