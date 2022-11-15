package IO;

public class FlightPathPoint {
    /**
     * This class is used to represent a single point on the flight path of the drone.
     */

    public String orderNumber;
    public double fromLongitude;
    public double fromLatitude;
    public double angle;
    public double toLongitude;
    public double toLatitude;
    public int ticksSinceStartOfCalculation;

    /**
     * Converts the flight path point to a JSON object.
     *
     * @return The JSON object.
     */
    public String toJSON() {
        return "{\"orderNumber\": \"" + orderNumber + "\""
                + ", \"fromLongitude\": " + fromLongitude
                + ", \"fromLatitude\": " + fromLatitude
                + ", \"angle\": " + angle
                + ", \"toLongitude\": " + toLongitude
                + ", \"toLatitude\": " + toLatitude
                + ", \"ticksSinceStartOfCalculation\": " + ticksSinceStartOfCalculation
                + "}";
    }
}
