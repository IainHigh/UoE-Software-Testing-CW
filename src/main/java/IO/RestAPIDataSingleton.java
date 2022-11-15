package IO;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.LngLat;
import uk.ac.ed.inf.Order;
import uk.ac.ed.inf.Restaurant;

import java.io.IOException;
import java.net.URL;

public final class RestAPIDataSingleton {
    private static RestAPIDataSingleton instance;
    private LngLat[] centralAreaBorder;
    private LngLat[][] noFlyZones;

    private Restaurant[] restaurants;

    private Order[] orders;

    private static <T> T deserialize(URL url, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(url, clazz);
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
     *
     * @param url The URL of the JSON file.
     * @return An array of no-fly zones, where each no-fly zone is an array of coordinates.
     */
    public LngLat[][] getNoFlyZones(URL url) {
        //TODO: Tidy this up :)
        NoFlyZone[] noFlyZoneObjectArray = deserialize(url, NoFlyZone[].class);

        // Convert the Array of NoFlyZones to a 2d array of LngLat
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
     * Pass in the URLs of the central area border and the no-fly zones.
     *
     * @param centralAreaUrl The URL of the central area border.
     * @param noFlyZonesUrl  The URL of the no-fly zones.
     */
    public void setURLs(URL centralAreaUrl, URL noFlyZonesUrl, URL restaurantsURL, URL ordersURL) {
        centralAreaBorder = deserialize(centralAreaUrl, LngLat[].class);
        noFlyZones = getNoFlyZones(noFlyZonesUrl);
        restaurants = deserialize(restaurantsURL, Restaurant[].class);
        orders = deserialize(ordersURL, Order[].class);
    }

    /**
     * @return The instance of the singleton.
     */
    public static RestAPIDataSingleton getInstance() {
        if (instance == null) {
            instance = new RestAPIDataSingleton();
        }
        return instance;
    }

    /**
     * Accessor method for the central area border.
     *
     * @return The central area border.
     */
    public LngLat[] getCentralAreaBorder() {
        return centralAreaBorder;
    }

    /**
     * Accessor method for the no-fly zones.
     *
     * @return The no-fly zones.
     */
    public LngLat[][] getNoFlyZones() {
        return noFlyZones;
    }

    public Restaurant[] getRestaurants() {
        return restaurants;
    }

    public Order[] getOrders() {
        return orders;
    }
}
