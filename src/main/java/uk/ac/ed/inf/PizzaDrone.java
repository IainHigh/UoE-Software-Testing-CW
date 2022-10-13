package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PizzaDrone {
    static List<FlightPathPoint> allDirectionsFollowed = new ArrayList<>();
    static List<double[]> pathWayCoordinates = new ArrayList<>();
    private static int ticksSinceStartOfCalculation = 0;
    private static int maxMoves = Constants.MAX_MOVES;

    /**
     * This is the main method of the program which will be called when the program is run.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        // Validate and parse the command line arguments.
        validateInput(args);
        String date = args[0];
        String restAPIUrl = args[1];

        // Set up the FlyZoneSingleton with the URLs of the central area border and the no-fly zones.
        try {
            FlyZoneSingleton.getInstance().setURLs(new URL(restAPIUrl + "/centralarea"), new URL(restAPIUrl + "/noflyzones"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Get the orders from the JSON file and store them in an array.
        var retriever = new JSONRetriever();
        Order[] orders;
        try {
            orders = retriever.getOrders(new URL(restAPIUrl + "/orders/" + date));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Validate the orders and store the valid orders in a new list - validOrders.
        var validator = new OrderValidator();
        ArrayList<Order> validOrders = new ArrayList<>();
        for (Order order : orders) {
            OrderOutcome outcome = validator.validateOrder(order);
            order.outcome = outcome;
            if (outcome == OrderOutcome.VALID_BUT_NOT_DELIVERED){
                System.out.println(order.restaurantOrderedFrom.toString());
                validOrders.add(order);
            }
        }

        System.out.println(validOrders.size() + " valid orders found.");
        // Sort the orders by the number of moves to appleton tower.
        validOrders.sort((o1, o2) -> {
            LngLat o1LngLat = new LngLat(o1.restaurantOrderedFrom.longitude, o1.restaurantOrderedFrom.latitude);
            LngLat o2LngLat = new LngLat(o2.restaurantOrderedFrom.longitude, o2.restaurantOrderedFrom.latitude);
            return Double.compare(o1LngLat.numberOfMovesTo(Constants.APPLETON_TOWER),
                    o2LngLat.numberOfMovesTo(Constants.APPLETON_TOWER));
        });

        // Starting at appleton tower, deliver the orders starting with those with the fewest moves from appleton tower.
        LngLat currentLocation = Constants.APPLETON_TOWER;
        CompassDirection[] hover = {null};
        for (Order order : validOrders) {
            LngLat restaurantLocation = new LngLat(order.restaurantOrderedFrom.longitude,
                    order.restaurantOrderedFrom.latitude);

            // If the drone has already made the maximum number of moves, then we can't deliver this order.
            // Leeway of 5 moves to account for the fact that the above is just an estimate.
            int maxMovesEstimate = maxMoves - currentLocation.numberOfMovesTo(restaurantLocation) * 2;
            if (maxMovesEstimate < 5) {
                break;
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

            // Update the outcome of the order to "delivered".
            order.outcome = OrderOutcome.DELIVERED;
        }

        // Write the results to the JSON and GEOJSON files.
        FileWriterSingleton.setDate(date);
        FileWriterSingleton.getInstance().writeToDroneGEOJSON(pathWayCoordinates);
        FileWriterSingleton.getInstance().writeToFlightpathJSON(allDirectionsFollowed);
        FileWriterSingleton.getInstance().writeToDeliveriesJSON(orders);
    }

    /**
     * This method validates the input from the command line.
     * @param args The command line arguments.
     */
    private static void validateInput(String[] args) {

        // Check that there are exactly two arguments - the date and the URL.
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected 2 arguments, got " + args.length);
        }

        // Check that the first argument follows the pattern "YYYY-MM-DD".
        if (!args[0].matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Expected first argument to be a date in the format YYYY-MM-DD, got " + args[0]);
        }

        // Check if the input date is in the range of between 2023-01-01 and 2023-05-31
        String[] dateParts = args[0].split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);
        if (year != 2023 || month < 1 || month > 5 || day < 1 || day > 31
        || (month == 2 && day > 28) || (month == 4 && day > 30)) {
            throw new IllegalArgumentException("Expected first argument to be a date in the range 2023-01-01 to 2023-05-31, got " + args[0]);
        }

        // Check that the second argument is a valid URL. And the test JSON can be accessed.
        try {
            new URL(args[1] + "/test");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
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
        pathWayCoordinates.add(coordinate);
        for (CompassDirection direction : route) {
            FlightPathPoint flightPathPoint = new FlightPathPoint();
            flightPathPoint.orderNumber = orderNo;
            flightPathPoint.fromLongitude = currentLocation.lng();
            flightPathPoint.fromLatitude = currentLocation.lat();
            flightPathPoint.angle = direction != null ? direction.getAngle() : -1;
            flightPathPoint.ticksSinceStartOfCalculation = ticksSinceStartOfCalculation++;

            coordinate = new double[2];
            currentLocation = currentLocation.nextPosition(direction);
            maxMoves -= 1;
            coordinate[0] = currentLocation.lng();
            coordinate[1] = currentLocation.lat();
            pathWayCoordinates.add(coordinate);

            flightPathPoint.toLongitude = currentLocation.lng();
            flightPathPoint.toLatitude = currentLocation.lat();
            allDirectionsFollowed.add(flightPathPoint);
        }
        return currentLocation;
    }
}
