package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        allDirectionsFollowed = new ArrayList<>();
        pathWayCoordinates = new ArrayList<>();

        maxMoves = Constants.MAX_MOVES;
        ticksSinceStartOfCalculation = 0;

        // Validate and parse the command line arguments.
        String date, restAPIUrl;
        if (validateInput(args)){
            date = args[0];
            restAPIUrl = args[1];
        }
        else {
            System.err.println("Error in input arguments using default values");
            date = "2023-01-01";
            restAPIUrl = "https://ilp-rest.azurewebsites.net/test";
        }


        setUpOrdersAndFlyZones(date, restAPIUrl);
        ArrayList<Order> validOrders = validateOrders(restAPIUrl);

        // Starting at appleton tower, deliver the orders starting with those with the fewest moves from appleton tower.
        LngLat currentLocation = Constants.APPLETON_TOWER;
        for (Order order : validOrders) {
            calculateNextRoute(currentLocation, order);
            if (maxMoves > 0){
                // If we're able to follow the current route, add the directions and coordinates to the list of all
                // directions and coordinates.
                allDirectionsFollowed.addAll(currentDirectionsFollowed);
                pathWayCoordinates.addAll(currentPathWayCoordinates);
                order.outcome = OrderOutcome.DELIVERED;
            }
        }
        writeToOutputFiles(date);
    }

    /**
     * This method validates the input from the command line.
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

        // Check that the second argument is a valid URL. And the test JSON can be accessed.
        try {
            new URL(args[1] + "/test");
        } catch (MalformedURLException e) {
            System.err.println("Second argument is not a valid URL, got: " + args[1]);
            System.err.println("Trying with default URL: https://ilp-rest.azurewebsites.net/");
            try {
                new URL("https://ilp-rest.azurewebsites.net/test");
            } catch (MalformedURLException e1) {
                System.err.println("Could not access the test JSON at default URL");
                System.err.println("Please check your internet connection and try again");
                throw new RuntimeException(e);
            }
            System.err.println("Successfully accessed the test JSON at default URL");
            return false;
        }
        return true;
    }

    /**
     * This method follows a route and records the directions followed.
     * @param currentLocation The current location of the drone.
     * @param route The route to follow.
     * @param orderNo The order number.
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
            flightPathPoint.angle = direction != null ? direction.getAngle() : -1;
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

    private static void setUpOrdersAndFlyZones(String date, String restAPIUrl) {
        // Set up the FlyZoneSingleton with the URLs of the central area border and the no-fly zones.
        try {
            FlyZoneSingleton.getInstance().setURLs(new URL(restAPIUrl + "/centralarea"), new URL(restAPIUrl + "/noflyzones"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Get the orders from the JSON file and store them in an array.
        var retriever = new JSONRetriever();
        try {
            orders = retriever.getOrders(new URL(restAPIUrl + "/orders/" + date));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<Order> validateOrders(String restAPIUrl) {
        // Validate the orders and store the valid orders in a new list - validOrders.
        var validator = new OrderValidator();
        ArrayList<Order> validOrders = new ArrayList<>();
        for (Order order : orders) {
            OrderOutcome outcome;
            try {
                outcome = validator.validateOrder(order, new URL(restAPIUrl + "/restaurants"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            order.outcome = outcome;
            if (outcome == OrderOutcome.VALID_BUT_NOT_DELIVERED){
                validOrders.add(order);
            }
        }

        // Sort the orders by the number of moves to appleton tower.
        validOrders.sort((o1, o2) -> {
            LngLat o1LngLat = new LngLat(o1.restaurantOrderedFrom.longitude, o1.restaurantOrderedFrom.latitude);
            LngLat o2LngLat = new LngLat(o2.restaurantOrderedFrom.longitude, o2.restaurantOrderedFrom.latitude);
            return Double.compare(o1LngLat.numberOfMovesTo(Constants.APPLETON_TOWER),
                    o2LngLat.numberOfMovesTo(Constants.APPLETON_TOWER));
        });

        return validOrders;
    }

    private static void writeToOutputFiles(String date){
        // Write the results to the JSON and GEOJSON files.
        FileWriterSingleton.setDate(date);
        FileWriterSingleton.getInstance().writeToDroneGEOJSON(pathWayCoordinates);
        FileWriterSingleton.getInstance().writeToFlightpathJSON(allDirectionsFollowed);
        FileWriterSingleton.getInstance().writeToDeliveriesJSON(orders);
    }

    private static void calculateNextRoute(LngLat currentLocation, Order order) {
        CompassDirection[] hover = {null};
        // Reset the current directions and coordinates for the next journey.
        currentDirectionsFollowed = new ArrayList<>();
        currentPathWayCoordinates = new ArrayList<>();

        LngLat restaurantLocation = new LngLat(order.restaurantOrderedFrom.longitude,
                order.restaurantOrderedFrom.latitude);

        if (restaurantLocation.inNoFlyZone()){
            return;
        }

        // Move to the restaurant.
        currentLocation = followRoute(currentLocation, currentLocation.routeTo(restaurantLocation), order.orderNo);
        // Hover at the restaurant to pick up the pizza.
        currentLocation = followRoute(currentLocation, hover, order.orderNo);

        if (!currentLocation.inCentralArea()) {
            // If we're not currently in the central area, we need to find the closest point to the border first
            // and then go here.
            LngLat borderPoint = RouteCalculator.findClosestPointInCentralArea(currentLocation);
            currentLocation = followRoute(currentLocation, currentLocation.routeTo(borderPoint), order.orderNo);
        }

        // Move to appleton tower.
        currentLocation = followRoute(currentLocation, currentLocation.routeTo(Constants.APPLETON_TOWER),
                order.orderNo);
        // Hover at appleton tower to drop off the pizza.
        currentLocation = followRoute(currentLocation, hover, order.orderNo);

    }
}
