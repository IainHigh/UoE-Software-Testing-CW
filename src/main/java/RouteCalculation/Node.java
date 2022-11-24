package RouteCalculation;

import java.awt.geom.Line2D;
import java.util.Arrays;

/**
 * A class to represent a node in the A* search algorithm.
 */
class Node {
    private final LngLat POINT; // The LngLat point represented by the node.
    private final double g; // The cost of the path from the start node to this node.
    private final double h; // The heuristic cost of the path from this node to the goal node.
    private final Node PARENT; // The parent node of this node.
    private final CompassDirection DIRECTION_FROM_PARENT; // The direction from the parent node to this node.

    /**
     * Create a new node with no parents.
     * This constructor is intended for the start node.
     *
     * @param point the LngLat coordinate of the node.
     * @param end   the LngLat coordinates of the destination.
     */
    public Node(LngLat point, LngLat end) {
        this.POINT = point;
        this.PARENT = null;
        this.DIRECTION_FROM_PARENT = null;
        this.h = calculateHeuristic(end);
        this.g = 0;
    }

    /**
     * Create a new node with a parent. This constructor is intended for all other nodes.
     *
     * @param POINT               the LngLat coordinate of the node.
     * @param end                 the LngLat coordinates of the destination.
     * @param parent              the parent node.
     * @param directionFromParent the compass direction from the parent node to this node. Used to reconstruct the path.
     */
    public Node(LngLat POINT, LngLat end, Node parent, CompassDirection directionFromParent) {
        this.POINT = POINT;
        this.PARENT = parent;
        this.DIRECTION_FROM_PARENT = directionFromParent;
        this.h = calculateHeuristic(end);
        this.g = parent.g + LngLat.LENGTH_OF_MOVE;
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
        for (LngLat[] zone : AreaSingleton.getInstance().getNoFlyZones()) {
            for (int i = 0; i < zone.length; i++) {
                LngLat p1 = zone[i];
                LngLat p2 = zone[(i + 1) % zone.length];

                // If the line between the two points intersects with the line between the border points, then the line
                // intersects with the no-fly zone.
                if (line.intersectsLine(p1.lng(), p1.lat(), p2.lng(), p2.lat())) {
                    // Calculate the border point p which minimises the distance from start to p and then from p to end.
                    return Arrays.stream(zone).mapToDouble(p -> this.POINT.distanceTo(p) + p.distanceTo(end)).min().getAsDouble();
                }
            }
        }

        // If the straight line does not go through a no-fly zone, then use the straight line distance.
        return this.POINT.distanceTo(end);
    }

    /**
     * Calculate the f value of the node.
     * The f value is the sum of the g and h values.
     *
     * @return the f value of the node.
     */
    public double getFScore() {
        return this.g + this.h;
    }

    /**
     * Accessor method for the parent node.
     *
     * @return the parent node.
     */
    public Node getParent() {
        return this.PARENT;
    }

    /**
     * Accessor method for the direction from parent node.
     *
     * @return the direction the drone took to get from the parent node to the current node.
     */
    public CompassDirection getDirectionFromParent() {
        return this.DIRECTION_FROM_PARENT;
    }

    /**
     * Accessor method for the current point.
     *
     * @return the LngLat coordinate of the current node.
     */
    public LngLat getPoint() {
        return this.POINT;
    }
}