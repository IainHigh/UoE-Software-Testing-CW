package IO;

/**
 * This class is used to represent a single point on the flight path of the drone.
 */
public class FlightPathPoint {

    private final String ORDER_NUMBER;
    private final double FROM_LONGITUDE;
    private final double FROM_LATITUDE;
    private final Double ANGLE;
    private final double TO_LONGITUDE;
    private final double TO_LATITUDE;
    private final int TICKS;

    /**
     * Create a new FlightPathPoint.
     *
     * @param orderNumber   The eight-character order number for the pizza order which the drone is currently
     *                      collecting or delivering.
     * @param fromLongitude The longitude of the drone at the start of this move.
     * @param fromLatitude  The latitude of the drone at the start of this move.
     * @param angle         The angle of travel of the drone in this move.
     * @param toLongitude   The longitude of the drone at the end of this move.
     * @param toLatitude    The latitude of the drone at the end of this move.
     * @param ticks         The elapsed ticks since the computation started for the day.
     */
    public FlightPathPoint(String orderNumber,
                           double fromLongitude,
                           double fromLatitude,
                           Double angle,
                           double toLongitude,
                           double toLatitude,
                           int ticks
    ) {
        this.ORDER_NUMBER = orderNumber;
        this.FROM_LONGITUDE = fromLongitude;
        this.FROM_LATITUDE = fromLatitude;
        this.ANGLE = angle;
        this.TO_LONGITUDE = toLongitude;
        this.TO_LATITUDE = toLatitude;
        this.TICKS = ticks;
    }

    /**
     * Accessor for the destination longitude and latitude.
     *
     * @return a 2-element array containing the destination longitude and latitude.
     */
    public double[] getDestinationCoordinates() {
        return new double[]{TO_LONGITUDE, TO_LATITUDE};
    }

    /**
     * Converts the flight path point to a string in JSON format.
     *
     * @return A String storing the information in JSON format.
     */
    public String toJSON() {
        return "{\"orderNumber\": \"" + ORDER_NUMBER + "\""
                + ", \"fromLongitude\": " + FROM_LONGITUDE
                + ", \"fromLatitude\": " + FROM_LATITUDE
                + ", \"angle\": " + ANGLE
                + ", \"toLongitude\": " + TO_LONGITUDE
                + ", \"toLatitude\": " + TO_LATITUDE
                + ", \"ticksSinceStartOfCalculation\": " + TICKS
                + "}";
    }
}
