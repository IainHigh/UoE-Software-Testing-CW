package uk.ac.ed.inf;

import IO.FileWriter;
import IO.FlightPathPoint;
import IO.RestAPIDataSingleton;
import OrderInformation.Order;
import OrderInformation.OrderOutcome;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The main class for the PizzaDrone program.
 */
public class PizzaDrone {
    private static List<FlightPathPoint> allDirectionsFollowed;
    private static List<FlightPathPoint> currentDirectionsFollowed;
    private static int remainingMoves;
    private static Order[] orders;

    private static Instant startTime;

    /**
     * This is the main method of the program which will be called when the program is run.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        allDirectionsFollowed = new ArrayList<>();
        remainingMoves = Constants.MAX_MOVES;

        // Validate and parse the command line arguments.
        String date, restAPIUrl;
        if (validateInput(args)) {
            date = args[0];
            restAPIUrl = args[1];
        } else {
            throw new IllegalArgumentException("Invalid input arguments.");
        }

        // Set up the URLs for the RestAPIDataSingleton
        try {
            RestAPIDataSingleton.getInstance().setURLs(
                    new URL(restAPIUrl + Constants.CENTRAL_AREA_URL_SLUG),
                    new URL(restAPIUrl + Constants.NO_FLY_ZONES_URL_SLUG),
                    new URL(restAPIUrl + Constants.RESTAURANTS_URL_SLUG),
                    new URL(restAPIUrl + Constants.ORDERS_WITH_DATE_URL_SLUG + date));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Get the orders, determine the outcome for invalid orders and sort the valid orders by the distance from
        // Appleton Tower.
        orders = RestAPIDataSingleton.getInstance().getOrders();
        ArrayList<Order> validOrders = validateAndSortOrders();

        // Calculate the full path around all the valid orders and back to Appleton Tower.
        calculatePath(validOrders);

        // Write the output to the required files.
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
     * Go through the orders and determine the initial outcome. If the order is valid, add it to the list of valid orders.
     * Sort the valid orders by the number of moves from Appleton Tower to the restaurant.
     * This ensures that we start by delivering the orders with the fewest moves from Appleton Tower.
     * Which means that we will be able to deliver more orders.
     *
     * @return The sorted list of valid orders.
     */
    private static ArrayList<Order> validateAndSortOrders() {
        // Validate the orders and store the valid orders in a new list - validOrders.
        ArrayList<Order> validOrders = new ArrayList<>();
        for (Order order : orders) {
            OrderOutcome outcome = order.validateOrder();
            if (outcome == OrderOutcome.ValidButNotDelivered) {
                validOrders.add(order);
            }
        }

        // Sort the orders by the restaurants distance from appleton tower so that the closest restaurant is first.
        validOrders.sort(Comparator.comparingDouble(o -> o.getRestaurant().getNumberOfMovesFromAppletonTower()));
        return validOrders;
    }

    /**
     * Calculate the full path around all the valid orders and back to Appleton Tower.
     * Record this path in the allDirectionsFollowed list.
     *
     * @param validOrders The sorted list of valid orders.
     */
    private static void calculatePath(ArrayList<Order> validOrders) {
        startTime = Clock.systemDefaultZone().instant();

        // Starting at appleton tower, deliver the orders starting with those with the fewest moves from appleton tower.
        LngLat currentLocation = Constants.APPLETON_TOWER;

        for (int i = 0; i < validOrders.size(); i++) {
            Order order = validOrders.get(i);

            LngLat nextLocation = (i != validOrders.size() - 1) ?
                    validOrders.get(i + 1).getRestaurant().getLngLat() : null;

            currentDirectionsFollowed = new ArrayList<>();

            // Move to the restaurant.
            CompassDirection[] route = currentLocation.routeTo(order.getRestaurant().getLngLat(), Constants.APPLETON_TOWER);
            currentLocation = followRoute(currentLocation, route, order.getOrderNo());

            // If making this journey would result in the drone running out of battery, then don't make the journey.
            if (remainingMoves < 0) break;

            // Move to appleton tower to pick up the next order.
            route = currentLocation.routeTo(Constants.APPLETON_TOWER, nextLocation);
            currentLocation = followRoute(currentLocation, route, order.getOrderNo());

            // If making this journey would result in the drone running out of battery, then don't make the journey.
            if (remainingMoves < 0) break;

            allDirectionsFollowed.addAll(currentDirectionsFollowed);
            order.setValidOrderToDelivered();
        }
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
        Instant currentTime;
        remainingMoves -= route.length;

        for (CompassDirection direction : route) {
            currentTime = Clock.systemDefaultZone().instant();
            LngLat nextLocation = currentLocation.nextPosition(direction);

            currentDirectionsFollowed.add(new FlightPathPoint(
                    orderNo,
                    currentLocation.lng(),
                    currentLocation.lat(),
                    direction.getAngle(),
                    nextLocation.lng(),
                    nextLocation.lat(),
                    (int) (currentTime.toEpochMilli() - startTime.toEpochMilli())));

            currentLocation = nextLocation;
        }
        return currentLocation;
    }

    /**
     * This method uses the FileWriter class to write to all the required files.
     *
     * @param date The date of the orders - used to name the files.
     */
    private static void writeToOutputFiles(String date) {
        // Write the results to the JSON and GEOJSON files.
        FileWriter fileWriter = new FileWriter(date);
        fileWriter.writeToDroneGEOJSON(allDirectionsFollowed);
        fileWriter.writeToFlightpathJSON(allDirectionsFollowed);
        fileWriter.writeToDeliveriesJSON(orders);
    }
}
