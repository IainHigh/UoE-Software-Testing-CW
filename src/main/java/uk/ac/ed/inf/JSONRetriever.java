package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)

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
     * @return An array of coordinates that make up the border of the central area.
     */
    public double[][] getCentralArea(URL url) {
        try {
            var objectMapper = new ObjectMapper();
            LngLat[] centralAreaObjectArray = objectMapper.readValue(url, LngLat[].class);
            return Arrays.stream(centralAreaObjectArray)
                     .map(x -> new double[]{x.lng(), x.lat()})
                     .toArray(double[][]::new);
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
