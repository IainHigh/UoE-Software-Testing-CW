package uk.ac.ed.inf;

import IO.RestAPIDataSingleton;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LngLat(@JsonProperty("longitude") double lng, @JsonProperty("latitude") double lat) {
    /**
     * Uses the ray-casting algorithm to determine if a point is inside the zone.
     * Draws a horizontal line from the point to the right and counts the number of times it intersects with the border.
     * If the number of intersections is odd, the point is inside the zone. Otherwise, it is outside.
     *
     * @return true if the point is inside the zone, false otherwise.
     */
    public boolean inZone(LngLat[] zoneCoordinates) {
        int intersections = 0;

        // Loop through the border points (in anti-clockwise pairs)
        for (int i = 0; i < zoneCoordinates.length; i++) {
            LngLat p1 = zoneCoordinates[i];
            LngLat p2 = zoneCoordinates[(i + 1) % zoneCoordinates.length];

            double gradient = (p2.lng() - p1.lng()) / (p2.lat() - p1.lat());
            double latDiff = this.lat - p1.lat();

            // If the point lies on the line of the border, it is inside the central area.
            boolean onVerticalLine = (p1.lng() == this.lng && p2.lng() == this.lng && this.lat >= Math.min(p1.lat(),
                    p2.lat()) && this.lat <= Math.max(p1.lat(), p2.lat()));
            boolean onHorizontalLine = (p1.lat() == this.lat && p2.lat() == this.lat && this.lng >= Math.min(p1.lng()
                    , p2.lng()) && this.lng <= Math.max(p1.lng(), p2.lng()));
            boolean onDiagonalLine = ((this.lng == (latDiff * gradient) + p1.lng()) && this.lat >= Math.min(p1.lat(),
                    p2.lat()) && this.lat <= Math.max(p1.lat(), p2.lat()));
            if (onVerticalLine || onHorizontalLine || onDiagonalLine) {
                return true;
            }

            // Determine if the line intersects with the border.
            // If it's above the lower point and below the upper point and lies to the left of the line between
            // border points then it will intersect.
            if (this.lat > Math.min(p1.lat(), p2.lat())
                    && this.lat < Math.max(p1.lat(), p2.lat())
                    && this.lng < (latDiff * gradient) + p1.lng()
            ) {
                intersections++;
            }
        }
        return (intersections % 2 != 0);
    }

    /**
     * Checks if the current point is in the central area.
     * Gets the central area border coordinates from the singleton class and calls the inZone method.
     *
     * @return true if the point is inside the central area, false otherwise.
     */
    public boolean inCentralArea() {
        return inZone(RestAPIDataSingleton.getInstance().getCentralAreaBorder());
    }

    /**
     * Checks if the current point is in a no-fly zone.
     * Gets the no-fly zone coordinates from the singleton class and calls the inZone method.
     *
     * @return true if the point is inside a no-fly zone, false otherwise.
     */
    public boolean inNoFlyZone() {
        for (LngLat[] noFlyZone : RestAPIDataSingleton.getInstance().getNoFlyZones()) {
            if (inZone(noFlyZone)) return true;
        }
        return false;
    }

    /**
     * Checks if the line between the current point and the previous point is in a no-fly zone.
     *
     * @param previousPoint the previous point in the path.
     * @return true if the line is in a no-fly zone, false otherwise.
     */
    public boolean inNoFlyZone(LngLat previousPoint) {
        for (LngLat[] zone : RestAPIDataSingleton.getInstance().getNoFlyZones()) {
            for (int i = 0; i < zone.length; i++) {
                LngLat p1 = zone[i];
                LngLat p2 = zone[(i + 1) % zone.length];

                // If the line between the two points intersects with the line between the border points, then the line
                // intersects with the no-fly zone.
                if (lineIntersects(p1, p2, previousPoint, this)) return true;
            }
        }
        return (inNoFlyZone() || previousPoint.inNoFlyZone());
    }

    /**
     * Given two lines (p1->p2 and p3->p4), determines if they intersect or cross each other.
     *
     * @param p1 The first point of the first line.
     * @param p2 The second point of the first line.
     * @param p3 The first point of the second line.
     * @param p4 The second point of the second line.
     * @return true if the lines intersect, false otherwise.
     */
    public static boolean lineIntersects(LngLat p1, LngLat p2, LngLat p3, LngLat p4) {
        double denominator = (p4.lat() - p3.lat()) * (p2.lng() - p1.lng()) - (p4.lng() - p3.lng()) * (p2.lat() - p1.lat());
        if (denominator == 0) return false;
        double uA = ((p4.lng() - p3.lng()) * (p1.lat() - p3.lat()) - (p4.lat() - p3.lat()) * (p1.lng() - p3.lng()))
                / denominator;
        double uB = ((p2.lng() - p1.lng()) * (p1.lat() - p3.lat()) - (p2.lat() - p1.lat()) * (p1.lng() - p3.lng()))
                / denominator;
        return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
    }

    /**
     * Calculates the pythagorean distance between two points.
     *
     * @param source the point we are measuring to.
     * @return the pythagorean distance between the two points.
     */
    public double distanceTo(LngLat source) {
        if (source == null) {
            // If the source is null, it is infinitely far away from the current point.
            return Double.POSITIVE_INFINITY;
        }
        // Calculates the pythagorean distance between two points.
        return Math.sqrt(Math.pow(source.lat - this.lat, 2) + Math.pow(source.lng - this.lng, 2));
    }

    /**
     * Calculates the distance between two points and checks if it is within the defined definition of "close" (0.00015)
     *
     * @param source the point we are measuring to.
     * @return true if the distance is less than 0.00015, false otherwise.
     */
    public boolean closeTo(LngLat source) {
        if (source == null) {
            // If the source is null, the point is not close to it.
            return false;
        }
        // Finds the distance to the other source and checks if it is less than 0.00015.
        return distanceTo(source) < Constants.DISTANCE_TOLERANCE;
    }

    /**
     * Given a compass direction, calculates the drones next position using trigonometry.
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
        double newLng = this.lng + Constants.LENGTH_OF_MOVE * Math.cos(radian);
        double newLat = this.lat + Constants.LENGTH_OF_MOVE * Math.sin(radian);
        return new LngLat(newLng, newLat);
    }


    /**
     * Uses the RouteCalculator to calculate the shortest route from this point to the destination.
     *
     * @param destination the point we are trying to reach.
     * @return the shortest route from this point to the destination.
     */
    public CompassDirection[] routeTo(LngLat destination) {
        // Calculate the route to the destination.
        return RouteCalculator.calculateRoute(this, destination);
    }

    /**
     * Uses the RouteCalculator to calculate the shortest route from this point to the destination. Then counts the
     * number of moves that it takes.
     *
     * @param destination the point we are trying to reach.
     * @return the number of moves it takes to get to the destination.
     */
    public int numberOfMovesTo(LngLat destination) {
        // Calculate the number of moves to the destination.
        if (destination == null) {
            // If the destination is null, it is infinitely far away, so we return the maximum integer value.
            return Integer.MAX_VALUE;
        }
        CompassDirection[] route = routeTo(destination);
        if (route == null) {
            // If the route is null, then this means we can't find a path to the destination, and so we can treat it
            // as if it is infinitely far away, so we return the maximum integer value.
            return Integer.MAX_VALUE;
        }
        return route.length;
    }

}