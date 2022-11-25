package OrderInformation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * A singleton class for storing the data retrieved from the REST API.
 * Stored in a singleton class so that it can be accessed from anywhere in the program. And so that it is only retrieved
 * once.
 */
public final class OrderRetriever {

    /**
     * Accessor method for the restaurants.
     *
     * @return The array of all restaurants which was accessed.
     */
    public static Restaurant[] getRestaurants(URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(url, Restaurant[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Accessor method for the orders.
     *
     * @return The array of all orders accessed.
     */
    public static Order[] getOrders(URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(url, Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
