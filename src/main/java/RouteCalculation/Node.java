package RouteCalculation;

import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * A class to represent a node in the A* search algorithm.
 */
class Node {
    private final LngLat point; // The LngLat point represented by the node.
    private final double g; // The cost of the path from the start node to this node.
    private final double h; // The heuristic cost of the path from this node to the goal node.
    private final Node parent; // The parent node of this node.
    private final CompassDirection directionFromParent; // The direction from the parent node to this node.
    private final double GREEDY_FACTOR = 1;
    /**
     * Create a new node with no parents.
     * This constructor is intended for the start node.
     *
     * @param point The LngLat coordinate of the node.
     * @param end   The LngLat coordinates of the destination.
     */
    public Node(LngLat point, LngLat end) {
        this.point = point;
        this.parent = null;
        this.directionFromParent = null;
        this.h = calculateHeuristic(end);
        this.g = 0;
    }

    /**
     * Create a new node with a parent. This constructor is intended for all other nodes.
     *
     * @param point               The LngLat coordinate of the node.
     * @param end                 The LngLat coordinates of the destination.
     * @param parent              The parent node.
     * @param directionFromParent The compass direction from the parent node to this node. Used to reconstruct the path.
     */
    public Node(LngLat point, LngLat end, Node parent, CompassDirection directionFromParent) {
        this.point = point;
        this.parent = parent;
        this.directionFromParent = directionFromParent;
        this.h = calculateHeuristic(end);
        this.g = parent.g + LngLat.LENGTH_OF_MOVE;
    }

    /**
     * Calculate the heuristic cost of the path from this node to the goal node.
     *
     * @param end The LngLat coordinates of the destination.
     * @return The heuristic cost of the path from this node to the goal node.
     */
    private double calculateHeuristic(LngLat end) {
        Line2D.Double line = new Line2D.Double(this.point.getLng(), this.point.getLat(), end.getLng(), end.getLat());

        // If the straight line goes through a no-fly zone, calculate the closest point on the border of the
        // no-fly zone and then calculate the distance to that point and then the distance from that point to
        // the end.
        for (LngLat[] zone : AreaSingleton.getInstance().getNoFlyZones()) {
            for (int i = 0; i < zone.length; i++) {
                LngLat p1 = zone[i];
                LngLat p2 = zone[(i + 1) % zone.length];

                // If the line between the two points intersects with the line between the border points, then the line
                // intersects with the no-fly zone.
                if (line.intersectsLine(p1.getLng(), p1.getLat(), p2.getLng(), p2.getLat())) {
                    // Calculate the border point p which minimizes the distance from start to p and then from p to end.
                    OptionalDouble minDistance = Arrays.stream(zone)
                        .mapToDouble(p -> this.point.distanceTo(p) + p.distanceTo(end))
                        .min();
                    if (minDistance.isPresent()) {
                        return minDistance.getAsDouble();
                    }
                }
            }
        }

        // If the straight line does not go through a no-fly zone, then use the straight-line distance.
        return this.point.distanceTo(end);
    }

    /**
     * Calculate the f value of the node.
     * The f value is influenced by the greedyFactor, balancing between g and h.
     *
     * @return The f value of the node.
     */
    public double getFScore() {
        return (1 - GREEDY_FACTOR) * this.g + GREEDY_FACTOR * this.h;
    }

    // Accessor methods
    public Node getParent() {
        return this.parent;
    }

    public CompassDirection getDirectionFromParent() {
        return this.directionFromParent;
    }

    public LngLat getPoint() {
        return this.point;
    }
}
