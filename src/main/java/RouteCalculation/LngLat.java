package RouteCalculation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.geom.Line2D;

/**
 * Class to represent a (Lng, Lat) coordinate pair.
 */
public class LngLat {
    private final double lng;
    private final double lat;

    final static double DISTANCE_TOLERANCE = 0.00015;
    final static double LENGTH_OF_MOVE = 0.00015;

    /**
     * Constructor to create a LngLat object.
     *
     * @param lng The longitude.
     * @param lat The latitude.
     */
    public LngLat(@JsonProperty("longitude") double lng, @JsonProperty("latitude") double lat) {
        // Ensure longitude is between -180 and 180.
        if (lng < -180 || lng > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }
        // Ensure latitude is between -90 and 90.
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }
        this.lng = lng;
        this.lat = lat;
    }

    // Getters
    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    /**
     * Uses the ray-casting algorithm to determine if a point is inside the zone.
     * Draws a horizontal line from the point to the right and counts the number of times it intersects with the border.
     * If the number of intersections is odd, the point is inside the zone. Otherwise, it is outside.
     *
     * @return True if the point is inside the zone, false otherwise.
     */
    private boolean inZone(LngLat[] zoneCoordinates) {
        if (zoneCoordinates == null || zoneCoordinates.length < 3) {
            System.err.println("inZone called with invalid zone coordinates.");
            return false;
        }
        boolean inside = false;
        // Loop through the border points (in anti-clockwise pairs)
        for (int i = 0; i < zoneCoordinates.length; i++) {
            LngLat p1 = zoneCoordinates[i];
            LngLat p2 = zoneCoordinates[(i + 1) % zoneCoordinates.length];
            double gradient = (p2.getLng() - p1.getLng()) / (p2.getLat() - p1.getLat());
            double latDiff = this.lat - p1.getLat();
            // Determine if the line intersects with the border.
            // If it's above the lower point and below the upper point and lies to the left of the line between
            // border points then it will intersect.
            if (this.lat > Math.min(p1.getLat(), p2.getLat()) && this.lat < Math.max(p1.getLat(), p2.getLat()) 
                && this.lng < (latDiff * gradient) + p1.getLng()) {
                inside = !inside;
            }
        }
        return inside;
    }
    

    /**
     * Checks if the current point is in the central area.
     * Gets the central area border coordinates from the singleton class and calls the inZone method.
     *
     * @return True if the point is inside the central area, false otherwise.
     */
    public boolean inCentralArea() {
        return inZone(AreaSingleton.getInstance().getCentralAreaBorder());
    }

    /**
     * Checks if the line between the current point and the previous point is in a no-fly zone.
     *
     * @param previousPoint The previous point in the path.
     * @return True if the line between the current point and previous point is in a no-fly zone, false otherwise.
     */
    public boolean inNoFlyZone(LngLat previousPoint) {
        if (previousPoint == null) {
            System.err.println("inNoFlyZone called with null previousPoint.");
            return false;
        }
        Line2D.Double l = new Line2D.Double(this.lng, this.lat, previousPoint.getLng(), previousPoint.getLat());
        for (LngLat[] noFlyZone : AreaSingleton.getInstance().getNoFlyZones()) {

            // If the current point is in the no-fly zone, return true.
            if (inZone(noFlyZone)) {
                return true;
            }

            // If the line between the current point and the previous point intersects with the no-fly zone, return true.
            for (int i = 0; i < noFlyZone.length; i++) {
                LngLat p1 = noFlyZone[i];
                LngLat p2 = noFlyZone[(i + 1) % noFlyZone.length];

                // If the line between the two points intersects with the line between the border points, then the line
                // enters the no-fly zone.
                if (l.intersectsLine(p1.getLng(), p1.getLat(), p2.getLng(), p2.getLat())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculates the Pythagorean distance between two points.
     *
     * @param source the point we are measuring to.
     * @return the pythagorean distance between the two points.
     */
    public double distanceTo(LngLat source) {
        if (source == null) {
            // If the source is null, it is infinitely far away from the current point.
            System.err.println("distanceTo called with null source.");
            return Double.POSITIVE_INFINITY;
        }
        // Calculates the Pythagorean distance between two points.
        return Math.sqrt(Math.pow(source.getLat() - this.lat, 2) + Math.pow(source.getLng() - this.lng, 2));
    }

    /**
     * Calculates the distance between two points and checks if it is within the defined definition of "close" (0.00015)
     *
     * @param source The LngLat point we are measuring to.
     * @return True if the distance is less than 0.00015, false otherwise.
     */
    public boolean closeTo(LngLat source) {
        if (source == null) {
            // If the source is null, the point is not close to it.
            System.err.println("closeTo called with null source.");
            return false;
        }
        // Finds the distance to the other source and checks if it is less than 0.00015.
        return distanceTo(source) < DISTANCE_TOLERANCE;
    }

    /**
     * Given a compass direction, calculates the drone's next position using trigonometry.
     *
     * @param direction the direction the drone is moving in.
     * @return a LngLat record which represents the new position of the drone.
     */
    public LngLat nextPosition(CompassDirection direction) {
        if (direction == null || direction == CompassDirection.HOVER) {
            // If the drone is hovering, it does not move, so we return the same position.
            return new LngLat(this.lng, this.lat);
        }
        // Calculates the next position based on the direction.
        double radian = Math.toRadians(direction.getAngle());
        double newLng = this.lng + LENGTH_OF_MOVE * Math.cos(radian);
        double newLat = this.lat + LENGTH_OF_MOVE * Math.sin(radian);
        return new LngLat(newLng, newLat);
    }

    /**
     * Uses the RouteCalculator to calculate the shortest route from this point to the destination.
     *
     * @param destination The LngLat point we are trying to reach.
     * @return An array of CompassDirections representing the shortest path from this point to the destination.
     */
    public CompassDirection[] routeTo(LngLat destination, LngLat nextTarget) {
        // Calculate the route to the destination.
        return RouteCalculator.calculateRoute(this, destination, nextTarget);
        
    }

    /**
     * Uses the RouteCalculator to calculate the shortest route from this point to the destination. Then counts the
     * number of moves that it takes.
     *
     * @param destination The LngLat point we are trying to reach.
     * @return The number of moves it takes to get to the destination.
     */
    public int numberOfMovesTo(LngLat destination) {
        // Calculate the number of moves to the destination.
        CompassDirection[] route = routeTo(destination, null);
        if (route == null) {
            // If the route is null, then this means we can't find a path to the destination, and so we can treat it
            // as if it is infinitely far away.
            return Integer.MAX_VALUE;
        }
        return route.length;
    }
}
