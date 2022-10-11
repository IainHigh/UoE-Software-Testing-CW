package uk.ac.ed.inf;

public class FlightPathPoint {
    /**
     * This class is used to represent a single point on the flight path of the drone.
     */

    String orderNumber;
    double fromLongitude;
    double fromLatitude;
    double angle;
    double toLongitude;
    double toLatitude;
    int ticksSinceStartOfCalculation;

    /**
     * Converts the flight path point to a JSON object.
     * @return The JSON object.
     */
    public String toJSON() {
        return "{\"orderNumber\": \"" + orderNumber + "\", \"fromLongitude\": " + fromLongitude + ", \"fromLatitude\": " + fromLatitude + ", \"angle\": " + angle + ", \"toLongitude\": " + toLongitude + ", \"toLatitude\": " + toLatitude + ", \"ticksSinceStartOfCalculation\": " + ticksSinceStartOfCalculation + "}";
    }
}
