package OrderInformation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * Class to retrieve and deserialize the restaurants and orders from the REST API.
 */
public final class OrderRetriever {

    /**
     * Retrieves and deserializes the restaurants from the REST API.
     *
     * @param url The URL of the JSON file containing the list of restaurants.
     * @return An array of Restaurant objects.
     */
    public static Restaurant[] getRestaurants(URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(url, Restaurant[].class);
        } catch (IOException e) {
            System.err.println("Error retrieving restaurants from REST API.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves and deserializes the orders from the REST API.
     *
     * @param url The URL of the JSON file containing the list of orders.
     * @return An array of Order objects.
     */
    public static Order[] getOrders(URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(url, Order[].class);
        } catch (IOException e) {
            System.err.println("Error retrieving orders from REST API.");
            throw new RuntimeException(e);
        }
    }
}
