package uk.ac.ed.inf;

import OrderInformation.Order;
import OrderInformation.OrderRetriever;
import OrderInformation.Restaurant;
import Output.FileWriter;
import Output.FlightPathPoint;
import RouteCalculation.AreaSingleton;
import RouteCalculation.CompassDirection;
import RouteCalculation.LngLat;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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
            System.err.println("Invalid input. Unable to continue.");
            throw new IllegalArgumentException("Invalid input. Unable to continue.");
        }

        URL noFlyURL, centralAreaURL, restaurantsURL, ordersURL;
        try {
            noFlyURL = new URL(restAPIUrl + Constants.NO_FLY_ZONES_URL_SLUG);
            centralAreaURL = new URL(restAPIUrl + Constants.CENTRAL_AREA_URL_SLUG);
            restaurantsURL = new URL(restAPIUrl + Constants.RESTAURANTS_URL_SLUG);
            ordersURL = new URL(restAPIUrl + Constants.ORDERS_WITH_DATE_URL_SLUG + date);
        } catch (MalformedURLException e) {
            System.err.println("Can't create URL from given string.");
            e.printStackTrace();
            return;
        }

        // Set up the URLs for the AreaSingleton.
        AreaSingleton.getInstance().setURLs(centralAreaURL, noFlyURL);

        // Retrieve the restaurants and orders.
        Restaurant[] restaurants = OrderRetriever.getRestaurants(restaurantsURL);
        orders = OrderRetriever.getOrders(ordersURL);
        if (orders.length == 0) {
            System.err.println("No orders found for the given date.");
            return;
        }

        // Determine the outcome for invalid orders and sort the valid orders by the distance from Appleton Tower.
        ArrayList<Order> validOrders = validateAndSortOrders(restaurants, date);

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

        // Check that there are a valid number of arguments: the date, the URL, and (optionally) the rng seed.
        if (args.length != 2 && args.length != 3) {
            System.err.println("Expected 2 or 3 arguments, got " + args.length);
            return false;
        }
        if (args.length == 3) {
            System.out.println("Please note we do not use random number generation, so the seed is ignored.");
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
        
        // if (year != 2023 || month < 1 || month > 5 || day < 1 || day > 31
        //         || (month == 2 && day > 28) || (month == 4 && day > 30)) {
        //     System.err.println("Expected first argument to be a date in the range 2023-01-01 to 2023-05-31, got " + args[0]);
        //     return false;
        // }
        // Change the year to be between 2023-09-01 and 2024-01-28
        if (year == 2023){
            if (month < 9 || month > 12 || day < 1 || day > 31
                    || (month == 9 && day >30) || (month == 1 && day > 30)){
                // September, April, June, November have 30 days (4, 6, 9, 11)
                System.err.println("Expected first argument to be a date in the range 2023-09-01 to 2024-01-28, got " + args[0]);
                return false;
            }
        }
        else if (year == 2024){
            if (month != 1 || day < 1 || day > 28){
                System.err.println("Expected first argument to be a date in the range 2023-09-01 to 2024-01-28, got " + args[0]);
                return false;
            }
        }
        else {
            System.err.println("Expected first argument to be a date in the range 2023-09-01 to 2024-01-28, got " + args[0]);
            return false;
        }

        // If the URL ends with a slash, remove it.
        if (args[1].endsWith("/")) {
            args[1] = args[1].substring(0, args[1].length() - 1);
        }

        // Check that the second argument is a valid URL. And the test JSON can be accessed.
        try {
            URL testURL = new URL(args[1] + Constants.TEST_URL_SLUG);
            new ObjectMapper().readValue(testURL, Object.class);
        } catch (Exception e) {
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
     * @param restaurants The array of all restaurants that are accessed from the server.
     * @param date        The command line argument for the date. Used to validate that the order is for the correct date.
     * @return The sorted list of valid orders.
     */
    private static ArrayList<Order> validateAndSortOrders(Restaurant[] restaurants, String date) {
        // Validate the orders and store the valid orders in a new list - validOrders.
        ArrayList<Order> validOrders = new ArrayList<>();
        for (Order order : orders) {
            order.validateOrder(restaurants, date);
            if (order.isValid()) {
                validOrders.add(order);

                // If we haven't yet calculated the number of moves to appleton tower for this restaurant, calculate it.
                if (order.getRestaurant().getNumberOfMovesFromAppletonTower() == 0) {
                    LngLat rLocation = new LngLat(order.getRestaurant().getLongitude(),
                            order.getRestaurant().getLatitude());
                    order.getRestaurant().setNumberOfMovesFromAppleton(rLocation.numberOfMovesTo(Constants.APPLETON_TOWER));
                }
            }
        }

        // Sort the orders by the number of moves from appleton tower so that the closest restaurant is first.
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
        startTime = Instant.now(); // Using Instant.now() instead of Clock.systemDefaultZone().instant()

        // Starting at appleton tower, deliver the orders starting with those with the fewest moves from appleton tower.
        LngLat currentLocation = Constants.APPLETON_TOWER;

        for (int i = 0; i < validOrders.size(); i++) {
            Order order = validOrders.get(i);

            LngLat nextLocation = (i != validOrders.size() - 1)
                    ? new LngLat(validOrders.get(i + 1).getRestaurant().getLongitude(),
                    validOrders.get(i + 1).getRestaurant().getLatitude())
                    : null;

            currentDirectionsFollowed = new ArrayList<>();

            // Move to the restaurant to pick up the order.
            CompassDirection[] route = currentLocation.routeTo(new LngLat(order.getRestaurant().getLongitude(),
                    order.getRestaurant().getLatitude()), Constants.APPLETON_TOWER);
            currentLocation = followRoute(currentLocation, route, order.getOrderNo());

            // If making this journey would result in the drone running out of battery, then don't make the journey.
            if (remainingMoves < 0) break;

            // Move to appleton tower to deliver the order.
            route = currentLocation.routeTo(Constants.APPLETON_TOWER, nextLocation);
            currentLocation = followRoute(currentLocation, route, order.getOrderNo());

            // If making this journey would result in the drone running out of battery, then don't make the journey.
            if (remainingMoves < 0) break;

            // If we still have battery, add the directions followed to the list of all directions followed.
            // And set the order as delivered.
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
            currentTime = Instant.now(); // Using Instant.now() instead of Clock.systemDefaultZone().instant()
            LngLat nextLocation = currentLocation.nextPosition(direction);

            currentDirectionsFollowed.add(new FlightPathPoint(
                    orderNo,
                    currentLocation.getLng(),
                    currentLocation.getLat(),
                    direction.getAngle(),
                    nextLocation.getLng(),
                    nextLocation.getLat(),
                    (int) (currentTime.toEpochMilli() - startTime.toEpochMilli())));

            currentLocation = nextLocation;
        }
        return currentLocation;
    }

    /**
     * This method uses the FileWriter class to write to all the required files.
     *
     * @param date The date of the orders which is used to name the files.
     */
    private static void writeToOutputFiles(String date) {
        // Write the results to the JSON and GEOJSON files.
        FileWriter fileWriter = new FileWriter(date);
        fileWriter.writeToDroneGEOJSON(allDirectionsFollowed);
        fileWriter.writeToFlightpathJSON(allDirectionsFollowed);
        fileWriter.writeToDeliveriesJSON(Arrays.stream(orders).map(Order::toJson).toArray(String[]::new));
    }
}
