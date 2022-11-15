package IO;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.LngLat;
import uk.ac.ed.inf.Order;
import uk.ac.ed.inf.Restaurant;

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
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(url, Order[].class);
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
    public LngLat[][] getNoFlyZones(URL url) {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Fail on unknown properties is set to false because we don't need to store the name of the point, only
        // the coordinates.
        NoFlyZone[] noFlyZoneObjectArray;

        try {
            noFlyZoneObjectArray = objectMapper.readValue(url, NoFlyZone[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Convert the Array of NoFlyZones to a 2-d array of LngLat
        LngLat[][] noFlyZoneArray = new LngLat[noFlyZoneObjectArray.length][];
        for (int i = 0; i < noFlyZoneObjectArray.length; i++) {
            noFlyZoneArray[i] = new LngLat[noFlyZoneObjectArray[i].coordinates.length];
            for (int j = 0; j < noFlyZoneObjectArray[i].coordinates.length; j++) {
                noFlyZoneArray[i][j] = new LngLat(noFlyZoneObjectArray[i].coordinates[j][0], noFlyZoneObjectArray[i].coordinates[j][1]);
            }
        }
        return noFlyZoneArray;
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
