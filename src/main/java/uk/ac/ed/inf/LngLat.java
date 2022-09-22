package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;

public class LngLat {
    public double longitude;
    public double latitude;
    private double[][] centralAreaBorder;
    public LngLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
        try {
            URL url = new URL("https://ilp-rest.azurewebsites.net/centralArea");
            JSONRetriever retriever = new JSONRetriever();
            CentralAreaPoint[] centralBorder = retriever.getCentralArea(url);
            centralAreaBorder = new double[centralBorder.length][2];
            for (int i = 0; i < centralBorder.length; i++){
                centralAreaBorder[i][0] = centralBorder[i].longitude;
                centralAreaBorder[i][1] = centralBorder[i].latitude;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Uses the ray-casting algorithm to determine if a point is inside the central area.
     * https://en.wikipedia.org/wiki/Point_in_polygon#Ray_casting_algorithm
     * Gets the coordinates of the end-points of the central area border from the JSON reader.
     * Draws a horizontal line from the point to the right and counts the number of times it intersects with the border.
     * If the number of intersections is odd, the point is inside the central area. Otherwise, it is outside.
     * @return true if the point is inside the central area, false otherwise.
     */
    public boolean inCentralArea(){
        int intersections = 0;
        for (int i = 0; i < this.centralAreaBorder.length; i++){
            double[] p1 = this.centralAreaBorder[i];
            double[] p2 = this.centralAreaBorder[(i+1) % this.centralAreaBorder.length];
            if (p1[1] == p2[1] && p1[1] == this.latitude && this.longitude > Math.min(p1[0], p2[0]) && this.longitude < Math.max(p1[0], p2[0])){
                return true;
            }
            if (this.latitude > Math.min(p1[1], p2[1]) && this.latitude <= Math.max(p1[1], p2[1]) && this.longitude <= Math.max(p1[0], p2[0]) && p1[1] != p2[1]){
                double xinters = (this.latitude - p1[1]) * (p2[0] - p1[0]) / (p2[1] - p1[1]) + p1[0];
                if (p1[0] == p2[0] || this.longitude <= xinters){
                    intersections++;
                }
            }
        }
        if (intersections % 2 != 0){
            return true;
        }
        return false;
    }

    /**
     * Calculates the pythagorean distance between two points.
     * @param source the point we are measuring to.
     * @return the distance between the two points.
     */
    public double distanceTo(LngLat source){
        // Calculates the pythagorean distance between two points.
        return Math.sqrt(Math.pow(source.latitude - this.latitude, 2) + Math.pow(source.longitude - this.longitude, 2));
    }

    /**
     * Calculates the distance between two points and checks if it is within the defined definition of "close" (0.00015)
     * @param source the point we are measuring to.
     * @return true if the distance is less than 0.00015, false otherwise.
     */
    public boolean closeTo(LngLat source){
        // Finds the distance to the other source and checks if it is less than 0.00015.
        return distanceTo(source) < 0.00015;
    }

    /**
     * Given a compass direction, calculates the drones next position using trigonometry.
     * @param direction the direction the drone is moving in.
     * @return the new position of the drone.
     */
    public LngLat nextPosition(CompassDirection direction){
        if (direction == null || direction == CompassDirection.HOVER){
            // If the drone is hovering, it does not move, so we return the same position.
            return this;
        }
        // Calculates the next position based on the direction.
        double radian = Math.toRadians(direction.getAngle());
        double newLng = this.longitude + 0.00015 * Math.cos(radian);
        double newLat = this.latitude + 0.00015 * Math.sin(radian);
        return new LngLat(newLng, newLat);
    }

}