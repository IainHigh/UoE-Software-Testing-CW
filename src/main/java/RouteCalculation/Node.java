package RouteCalculation;

import IO.RestAPIDataSingleton;
import uk.ac.ed.inf.CompassDirection;
import uk.ac.ed.inf.Constants;
import uk.ac.ed.inf.LngLat;

import java.awt.geom.Line2D;

class Node {
    private final LngLat POINT;
    private final double g; // The cost of the path from the start node to this node.
    private final double h; // The heuristic cost of the path from this node to the goal node.
    private Node parent;
    private CompassDirection directionFromParent;

    /**
     * Create a new node with no parents.
     * This is the start node.
     *
     * @param point the LngLat coordinate of the node.
     * @param end   the LngLat coordinates of the destination.
     */
    public Node(LngLat point, LngLat end) {
        this.POINT = point;
        this.parent = null;
        this.directionFromParent = null;
        this.h = calculateHeuristic(end);
        this.g = 0;
    }

    /**
     * Create a new node with a parent.
     *
     * @param POINT               the LngLat coordinate of the node.
     * @param end                 the LngLat coordinates of the destination.
     * @param parent              the parent node.
     * @param directionFromParent the compass direction from the parent node to this node. Used to reconstruct the path.
     */
    public Node(LngLat POINT, LngLat end, Node parent, CompassDirection directionFromParent) {
        this.POINT = POINT;
        this.parent = parent;
        this.directionFromParent = directionFromParent;
        this.h = calculateHeuristic(end);
        this.g = parent.g + Constants.LENGTH_OF_MOVE;
    }

    /**
     * Calculate the heuristic cost of the path from this node to the goal node.
     *
     * @param end the LngLat coordinates of the destination.
     * @return the heuristic cost of the path from this node to the goal node.
     */
    private double calculateHeuristic(LngLat end) {
        Line2D.Double line = new Line2D.Double(this.POINT.lng(), this.POINT.lat(), end.lng(), end.lat());

        // If the straight line goes through a no-fly zone, calculate the closest point on the border of the
        // no-fly zone and then calculate the distance to that point and then the distance from that point to
        // the end.
        for (LngLat[] zone : RestAPIDataSingleton.getInstance().getNoFlyZones()) {
            boolean flag = false;
            for (int i = 0; i < zone.length; i++) {
                LngLat p1 = zone[i];
                LngLat p2 = zone[(i + 1) % zone.length];

                // If the line between the two points intersects with the line between the border points, then the line
                // intersects with the no-fly zone.
                if (line.intersectsLine(p1.lng(), p1.lat(), p2.lng(), p2.lat())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                double min = Double.MAX_VALUE;
                for (LngLat p : zone) {
                    double dist = this.POINT.distanceTo(p) + p.distanceTo(end);
                    if (dist < min) {
                        min = dist;
                    }
                }
                return min;
            }
        }
        return this.POINT.distanceTo(end);
    }

    /**
     * Calculate the f value of the node.
     * The f value is the sum of the g and h values.
     * @return the f value of the node.
     */
    public double getFScore() {
        return this.g + this.h;
    }

    public Node getParent() {
        return this.parent;
    }

    public CompassDirection getDirectionFromParent() {
        return this.directionFromParent;
    }

    public LngLat getPoint() {
        return this.POINT;
    }
}