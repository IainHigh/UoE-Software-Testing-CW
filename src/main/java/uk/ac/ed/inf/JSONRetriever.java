package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

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
     * Internal class used to deserialize the CentralArea JSON file.
     */
    private static class CentralAreaPoint {
        public double longitude;
        public double latitude;
    }

    /**
     * Retrieves the central area border from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of coordinates that make up the border of the central area.
     */
    public double[][] getCentralArea(URL url) {
        try {
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // Fail on unknown properties is set to false because we don't need to store the name of the point, only
            // the coordinates.
            CentralAreaPoint[] centralAreaObjectArray = objectMapper.readValue(url, CentralAreaPoint[].class);
            return Arrays.stream(centralAreaObjectArray)
                     .map(x -> new double[]{x.longitude, x.latitude})
                     .toArray(double[][]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Internal class used to deserialize the NoFlyZones JSON file.
     */
    private static class NoFlyZone {
        public double[][] coordinates;
    }

    /**
     * Retrieves the no-fly zones from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of no-fly zones, where each no-fly zone is an array of coordinates.
     */
    public double[][][] getNoFlyZones(URL url) {
        try {
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // Fail on unknown properties is set to false because we don't need to store the name of the point, only
            // the coordinates.
            NoFlyZone[] noFlyZoneObjectArray = objectMapper.readValue(url, NoFlyZone[].class);
            return Arrays.stream(noFlyZoneObjectArray)
                    .map(x -> x.coordinates)
                    .toArray(double[][][]::new);
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
