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
public final class RestAPIDataSingleton {
    private static RestAPIDataSingleton instance;
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

    public void setURLs(URL restaurantsURL, URL ordersURL) {
        restaurants = deserializeRestaurants(restaurantsURL);
        orders = deserializeOrders(ordersURL);
    }

    private Restaurant[] deserializeRestaurants(URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(url, Restaurant[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Order[] deserializeOrders(URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(url, Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
