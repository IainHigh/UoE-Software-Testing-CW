package uk.ac.ed.inf;

import uk.ac.ed.inf.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PizzaDrone {
    //TODO: Add Javadoc comments to all methods. Tidy up the code.
    static List<double[]> pathWayCoords = new ArrayList<>();
    public static void main(String[] args) {
        //TODO: Add validation for the input arguments.
        String restAPIUrl = args[0];
        String date = args[1];

        // Since the drone can only carry one order at a time we get all orders, sort them by distance and then use a
        // greedy algorithm to get the minimal cost first.
        var retriever = new JSONRetriever();
        var validator = new OrderValidator();
        Order[] orders;

        try {
            orders = retriever.getOrders(new URL(restAPIUrl + "/orders/" + date));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        ArrayList<Order> validOrders = new ArrayList<>();

        for (Order order : orders) {
            OrderOutcome outcome = validator.validateOrder(order);
            if (outcome == OrderOutcome.VALID_BUT_NOT_DELIVERED) {
                validOrders.add(order);
            }
            System.out.println(order.orderNo + " : " + outcome);
        }

        // Sort the orders by the number of moves to appleton tower.
        validOrders.sort((o1, o2) -> {
            LngLat o1LngLat = new LngLat(o1.restaurantOrderedFrom.longitude, o1.restaurantOrderedFrom.latitude);
            LngLat o2LngLat = new LngLat(o2.restaurantOrderedFrom.longitude, o2.restaurantOrderedFrom.latitude);
            return Double.compare(o1LngLat.numberOfMovesTo(Constants.APPLETON_TOWER),
                    o2LngLat.numberOfMovesTo(Constants.APPLETON_TOWER));
        });

        LngLat currentLocation = Constants.APPLETON_TOWER;
        for (Order order : validOrders) {
            LngLat restaurantLocation = new LngLat(order.restaurantOrderedFrom.longitude,
                    order.restaurantOrderedFrom.latitude);

            currentLocation = followRoute(currentLocation, currentLocation.routeTo(restaurantLocation));
            // Do some null hover move here TODO
            currentLocation = followRoute(currentLocation, currentLocation.routeTo(Constants.APPLETON_TOWER));
            // Do some null hover move here TODO
        }
        prepareFile();
    }

    private static void prepareFile() {
        File myObj = new File("outputFiles/route2.geojson");
        myObj.delete();
        File file = new File("outputFiles/route2.geojson");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Write all of the pathWayCoords to the geojson file as a LineString
        try {
            FileWriter myWriter = new FileWriter("outputFiles/route2.geojson");
            myWriter.write("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"LineString\"," +
                    " " +
                    "\"coordinates\": [");
            for (int i = 0; i < pathWayCoords.size(); i++) {
                myWriter.write(Arrays.toString(pathWayCoords.get(i)));
                if (i != pathWayCoords.size() - 1) {
                    myWriter.write(",");
                }
            }
            myWriter.write("]}}");
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static LngLat followRoute(LngLat currentLocation, CompassDirection[] route) {
        double[] coords = new double[2];
        coords[0] = currentLocation.lng();
        coords[1] = currentLocation.lat();
        pathWayCoords.add(coords);
        for (CompassDirection direction : route) {
            coords = new double[2];
            currentLocation = currentLocation.nextPosition(direction);
            coords[0] = currentLocation.lng();
            coords[1] = currentLocation.lat();
            pathWayCoords.add(coords);
        }
        return currentLocation;
    }
}
