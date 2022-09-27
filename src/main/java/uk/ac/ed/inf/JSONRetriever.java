package uk.ac.ed.inf;

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
     * Internal class used to deserialize the CentralArea JSON file.
     */
    private static class CentralAreaPoint {
        public String name;
        public double longitude;
        public double latitude;
    }

    /**
     * Retrieves the central area border from the JSON file.
     * @param url The URL of the JSON file.
     * @return An array of points that make up the border of the central area.
     */
    public double[][] getCentralArea(URL url) {
        try {
            CentralAreaPoint[] temp = new ObjectMapper().readValue(url, CentralAreaPoint[].class);
            double[][] centralAreaBorder = new double[temp.length][2];
            for (int i = 0; i < temp.length; i++){
                centralAreaBorder[i][0] = temp[i].longitude;
                centralAreaBorder[i][1] = temp[i].latitude;
            }
            return centralAreaBorder;
        } catch (IOException e){
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
