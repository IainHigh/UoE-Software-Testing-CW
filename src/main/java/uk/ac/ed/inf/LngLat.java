package uk.ac.ed.inf;
public record LngLat(double lng, double lat) {
    /**
     * Uses the ray-casting algorithm to determine if a point is inside the central area.
     * https://en.wikipedia.org/wiki/Point_in_polygon#Ray_casting_algorithm
     * Gets the coordinates of the end-points of the central area border from the singleton class.
     * Draws a horizontal line from the point to the right and counts the number of times it intersects with the border.
     * If the number of intersections is odd, the point is inside the central area. Otherwise, it is outside.
     * @param zoneCoordinates The array of coordinates that make up the border of the zone.
     * @return true if the point is inside the central area, false otherwise.
     */
    public boolean inZone(double[][] zoneCoordinates){
        int intersections = 0;

        // Loop through the border points (in anti-clockwise pairs)
        for (int i = 0; i < zoneCoordinates.length; i++){
            double[] p1 = zoneCoordinates[i];
            double[] p2 = zoneCoordinates[(i+1) % zoneCoordinates.length];

            // If the point is on a corner or border, it is inside the central area.
            if (p1[1] == this.lat && p2[1] == this.lat
                    || p1[0] == this.lng && p2[0] == this.lng){
                return true;
            }

            // Determine if the line intersects with the border.
            if (this.lat > Math.min(p1[1], p2[1])
                    && this.lat < Math.max(p1[1], p2[1])
                    && this.lng < ((this.lat - p1[1]) * (p2[0] - p1[0]) / (p2[1] - p1[1]) + p1[0])
            ){
                intersections++;
            }
        }
        return (intersections % 2 != 0);
    }

    /**
     * Calculates the pythagorean distance between two points.
     * @param source the point we are measuring to.
     * @return the pythagorean distance between the two points.
     */
    public double distanceTo(LngLat source){
        if (source == null){
            // If the source is null, it is infinitely far away from the current point.
            return Double.POSITIVE_INFINITY;
        }
        // Calculates the pythagorean distance between two points.
        return Math.sqrt(Math.pow(source.lat - this.lat, 2) + Math.pow(source.lng - this.lng, 2));
    }

    /**
     * Calculates the distance between two points and checks if it is within the defined definition of "close" (0.00015)
     * @param source the point we are measuring to.
     * @return true if the distance is less than 0.00015, false otherwise.
     */
    public boolean closeTo(LngLat source){
        if (source == null){
            // If the source is null, the point is not close to it.
            return false;
        }
        // Finds the distance to the other source and checks if it is less than 0.00015.
        return distanceTo(source) < Constants.DISTANCE_TOLERANCE;
    }

    /**
     * Given a compass direction, calculates the drones next position using trigonometry.
     * @param direction the direction the drone is moving in.
     * @return a LngLat record which represents the new position of the drone.
     */
    public LngLat nextPosition(CompassDirection direction){
        if (direction == null){
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
     * @param destination the point we are trying to reach.
     * @return the shortest route from this point to the destination.
     */
    public CompassDirection[] routeTo(LngLat destination){
        // Calculate the route to the destination.
        return RouteCalculator.calculateRoute(this, destination);
    }

    /**
     * Uses the RouteCalculator to calculate the shortest route from this point to the destination. Then counts the
     * number of moves that it takes.
     * @param destination the point we are trying to reach.
     * @return the number of moves it takes to get to the destination.
     */
    public int numberOfMovesTo(LngLat destination){
        // Calculate the number of moves to the destination.
        return routeTo(destination).length;
    }

}