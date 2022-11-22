package IO;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.LngLat;
import uk.ac.ed.inf.Order;
import uk.ac.ed.inf.Restaurant;

import java.io.IOException;
import java.net.URL;

/**
 * A singleton class for storing the data retrieved from the REST API.
 * Stored in a singleton class so that it can be accessed from anywhere in the program. And so that it is only retrieved
 * once.
 */
public final class RestAPIDataSingleton {
    private static RestAPIDataSingleton instance;
    private LngLat[] centralAreaBorder;
    private LngLat[][] noFlyZones;

    private Restaurant[] restaurants;

    private Order[] orders;

    /**
     * Public method for getting the instance of the singleton.
     * Synchronised to ensure that only one instance is created.
     *
     * @return The instance of the singleton.
     */
    public static synchronized RestAPIDataSingleton getInstance() {
        if (instance == null) {
            instance = new RestAPIDataSingleton();
        }
        return instance;
    }

    /**
     * Pass in the URLs of the central area border and the no-fly zones.
     * Once URLs are passed in the data is retrieved from the REST API.
     * Then the data is stored in the singleton and can be accessed from anywhere in the program without having to
     * access the REST API again.
     *
     * @param centralAreaUrl The URL of the JSON file containing the coordinates of the central area border.
     * @param noFlyZonesUrl  The URL of the JSON file containing the list of no-fly zones.
     * @param restaurantsURL The URL of the JSON file containing the list of restaurants.
     * @param ordersURL      The URL of the JSON file containing the list of orders.
     */
    public void setURLs(URL centralAreaUrl, URL noFlyZonesUrl, URL restaurantsURL, URL ordersURL) {
        centralAreaBorder = deserialize(centralAreaUrl, LngLat[].class);
        noFlyZones = deserializeNoFlyZone(noFlyZonesUrl);
        restaurants = deserialize(restaurantsURL, Restaurant[].class);
        orders = deserialize(ordersURL, Order[].class);
    }

    /**
     * Generic method for deserializing a JSON file into a Java object.
     *
     * @param url   The URL of the JSON file.
     * @param clazz The class of the Java object to deserialize the JSON file into.
     * @return The Java object.
     */
    private <T> T deserialize(URL url, Class<T> clazz) {
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
     * Since the no-fly zones are stored in the JSON file as a list of lists of coordinates, this method deserializes
     * the file into a list of NoFlyZone objects. Then converts the NoFlyZone objects into an array of LngLat arrays.
     *
     * @param url The URL of the JSON file.
     * @return An array of no-fly zones, where each no-fly zone is an array of LngLat points.
     */
    private LngLat[][] deserializeNoFlyZone(URL url) {
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
     * Accessor method for the central area border.
     *
     * @return The central area border represented by an array of LngLat points.
     */
    public LngLat[] getCentralAreaBorder() {
        return centralAreaBorder;
    }

    /**
     * Accessor method for the no-fly zones.
     *
     * @return An array of no-fly zones where each individual no-fly zone is an array of LngLat points.
     */
    public LngLat[][] getNoFlyZones() {
        return noFlyZones;
    }

    /**
     * Accessor method for the restaurants.
     *
     * @return The array of all restaurants which was accessed.
     */
    public Restaurant[] getRestaurants() {
        return restaurants;
    }

    /**
     * Accessor method for the orders.
     *
     * @return The array of all orders accessed.
     */
    public Order[] getOrders() {
        return orders;
    }
}
