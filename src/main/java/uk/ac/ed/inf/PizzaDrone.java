// TODO: ADD COMMENTS TO CLASSES!
// TODO: Add Javadoc to all methods
// TODO: Credit card check in more detail
// TODO: write the report
// TODO: Ticks

package uk.ac.ed.inf;

import IO.FileWriterSingleton;
import IO.FlightPathPoint;
import IO.RestAPIDataSingleton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PizzaDrone {


    private static List<FlightPathPoint> allDirectionsFollowed;
    private static List<FlightPathPoint> currentDirectionsFollowed;

    private static List<double[]> pathWayCoordinates;
    private static List<double[]> currentPathWayCoordinates;

    private static int ticksSinceStartOfCalculation;
    private static int maxMoves;

    private static Order[] orders;

    /**
     * This is the main method of the program which will be called when the program is run.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        allDirectionsFollowed = new ArrayList<>();
        pathWayCoordinates = new ArrayList<>();

        maxMoves = Constants.MAX_MOVES;
        ticksSinceStartOfCalculation = 0;

        // Validate and parse the command line arguments.
        String date, restAPIUrl;
        if (validateInput(args)) {
            date = args[0];
            restAPIUrl = args[1];
        } else {
            throw new IllegalArgumentException("Invalid input arguments.");
        }

        try {
            RestAPIDataSingleton.getInstance().setURLs(
                    new URL(restAPIUrl + Constants.CENTRAL_AREA_URL_SLUG),
                    new URL(restAPIUrl + Constants.NO_FLY_ZONES_URL_SLUG),
                    new URL(restAPIUrl + Constants.RESTAURANTS_URL_SLUG),
                    new URL(restAPIUrl + Constants.ORDERS_WITH_DATE_URL_SLUG + date));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        orders = RestAPIDataSingleton.getInstance().getOrders();

        ArrayList<Order> validOrders = validateOrders();
        sortValidOrders(validOrders);

        // Starting at appleton tower, deliver the orders starting with those with the fewest moves from appleton tower.
        LngLat currentLocation = Constants.APPLETON_TOWER;
        for (int i = 0; i < validOrders.size(); i++) {
            Order order = validOrders.get(i);

            LngLat nextLocation;
            if (i == (validOrders.size()) - 1){
                nextLocation = Constants.APPLETON_TOWER;
            }
            else {
                Order nextOrder = validOrders.get(i + 1);
                nextLocation = new LngLat(nextOrder.restaurantOrderedFrom.longitude, nextOrder.restaurantOrderedFrom.latitude);
            }
            currentLocation = calculateNextRoute(currentLocation, order, nextLocation);

            // If making this journey would result in the drone running out of battery, then don't make the journey.
            if (maxMoves < 0) break;

            System.out.println(maxMoves);
            allDirectionsFollowed.addAll(currentDirectionsFollowed);
            pathWayCoordinates.addAll(currentPathWayCoordinates);
            order.outcome = OrderOutcome.Delivered;
        }
        writeToOutputFiles(date);
    }

    /**
     * This method validates the input from the command line.
     *
     * @param args The command line arguments.
     */
    private static boolean validateInput(String[] args) {

        // Check that there are exactly two arguments - the date and the URL.
        if (args.length != 2) {
            System.err.println("Expected 2 arguments, got " + args.length);
            return false;
        }

        // Check that the first argument follows the pattern "YYYY-MM-DD".
        if (!args[0].matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.err.println("Expected first argument to be a date in the format YYYY-MM-DD, got " + args[0]);
            return false;
        }

        // Check if the input date is in the range of between 2023-01-01 and 2023-05-31
        String[] dateParts = args[0].split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);
        if (year != 2023 || month < 1 || month > 5 || day < 1 || day > 31
                || (month == 2 && day > 28) || (month == 4 && day > 30)) {
            System.err.println("Expected first argument to be a date in the range 2023-01-01 to 2023-05-31, got " + args[0]);
            return false;
        }

        // If the URL ends with a slash, remove it.
        if (args[1].endsWith("/")) {
            args[1] = args[1].substring(0, args[1].length() - 1);
        }

        // Check that the second argument is a valid URL. And the test JSON can be accessed.
        try {
            new URL(args[1] + Constants.TEST_URL_SLUG);
        } catch (MalformedURLException e) {
            System.err.println("Second argument is not a valid URL, got: " + args[1]);
            return false;
        }
        return true;
    }

    /**
     * This method follows a route and records the directions followed.
     *
     * @param currentLocation The current location of the drone.
     * @param route           The route to follow.
     * @param orderNo         The order number.
     * @return The new location of the drone after following the route.
     */
    private static LngLat followRoute(LngLat currentLocation, CompassDirection[] route, String orderNo) {
        double[] coordinate = new double[2];
        coordinate[0] = currentLocation.lng();
        coordinate[1] = currentLocation.lat();
        currentPathWayCoordinates.add(coordinate);
        for (CompassDirection direction : route) {
            FlightPathPoint flightPathPoint = new FlightPathPoint();
            flightPathPoint.orderNumber = orderNo;
            flightPathPoint.fromLongitude = currentLocation.lng();
            flightPathPoint.fromLatitude = currentLocation.lat();
            flightPathPoint.angle = direction.getAngle();
            flightPathPoint.ticksSinceStartOfCalculation = ticksSinceStartOfCalculation++;

            coordinate = new double[2];
            currentLocation = currentLocation.nextPosition(direction);
            maxMoves--;
            coordinate[0] = currentLocation.lng();
            coordinate[1] = currentLocation.lat();
            currentPathWayCoordinates.add(coordinate);

            flightPathPoint.toLongitude = currentLocation.lng();
            flightPathPoint.toLatitude = currentLocation.lat();
            currentDirectionsFollowed.add(flightPathPoint);
        }
        return currentLocation;
    }

    private static void sortValidOrders(ArrayList<Order> validOrders) {
        // Sort the orders by the restaurants distance from appleton tower so that the closest restaurant is first.
        Restaurant[] restaurants = RestAPIDataSingleton.getInstance().getRestaurants();
        for (Restaurant r : restaurants) {
            LngLat restaurantLocation = new LngLat(r.longitude, r.latitude);
            r.numberOfMovesFromAppletonTower = restaurantLocation.numberOfMovesTo(Constants.APPLETON_TOWER);
        }

        validOrders.sort(Comparator.comparingDouble(o -> o.restaurantOrderedFrom.numberOfMovesFromAppletonTower));
    }

    private static ArrayList<Order> validateOrders() {
        // Validate the orders and store the valid orders in a new list - validOrders.
        Restaurant[] restaurants = RestAPIDataSingleton.getInstance().getRestaurants();
        for (Restaurant r : restaurants) {
            LngLat restaurantLocation = new LngLat(r.longitude, r.latitude);
            r.numberOfMovesFromAppletonTower = restaurantLocation.numberOfMovesTo(Constants.APPLETON_TOWER);
        }

        ArrayList<Order> validOrders = new ArrayList<>();
        for (Order order : orders) {
            OrderOutcome outcome = order.validateOrder(restaurants);
            order.outcome = outcome;
            if (outcome == OrderOutcome.ValidButNotDelivered) {
                validOrders.add(order);
            }
        }
        return validOrders;
    }

    private static LngLat calculateNextRoute(LngLat currentLocation, Order order, LngLat nextRestaurantLocation) {
        CompassDirection[] hover = {CompassDirection.HOVER};
        // Reset the current directions and coordinates for the next journey.
        currentDirectionsFollowed = new ArrayList<>();
        currentPathWayCoordinates = new ArrayList<>();

        LngLat restaurantLocation = new LngLat(order.restaurantOrderedFrom.longitude,
                order.restaurantOrderedFrom.latitude);

        // Move to the restaurant.
        currentLocation = followRoute(currentLocation, currentLocation.routeTo(restaurantLocation, Constants.APPLETON_TOWER),
                order.orderNo);
        // Hover at the restaurant to pick up the pizza.
        currentLocation = followRoute(currentLocation, hover, order.orderNo);

        // Move to appleton tower.
        currentLocation = followRoute(currentLocation, currentLocation.routeTo(Constants.APPLETON_TOWER, nextRestaurantLocation),
                order.orderNo);
        // Hover at appleton tower to drop off the pizza.
        currentLocation = followRoute(currentLocation, hover, order.orderNo);
        return currentLocation;

    }

    private static void writeToOutputFiles(String date) {
        // Write the results to the JSON and GEOJSON files.
        FileWriterSingleton.setDate(date);
        FileWriterSingleton.getInstance().writeToDroneGEOJSON(pathWayCoordinates);
        FileWriterSingleton.getInstance().writeToFlightpathJSON(allDirectionsFollowed);
        FileWriterSingleton.getInstance().writeToDeliveriesJSON(orders);
    }

}
