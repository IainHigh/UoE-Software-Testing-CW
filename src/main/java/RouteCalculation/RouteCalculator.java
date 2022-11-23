package RouteCalculation;

import uk.ac.ed.inf.CompassDirection;
import uk.ac.ed.inf.LngLat;

import java.util.*;

/**
 * This class is used for calculating the shortest route between two points.
 * It uses the A* search algorithm.
 */
public class RouteCalculator {
    /**
     * Use the A* algorithm to find the shortest route from the start to the end.
     * The heuristic will be the straight line distance (distanceTo) between the current node and the end node.
     *
     * @param start the start node.
     * @param end   the end node.
     * @return the shortest route from the start to the end.
     */
    public static CompassDirection[] calculateRoute(LngLat start, LngLat end, LngLat nextTarget) {
        if (start == null || end == null) {
            System.err.println("calculateRoute was called with a null start or end point.");
            return null;
        }

        // Create the priority queue and add the start node
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getFScore));
        openList.add(new Node(start, end));

        // Create a list of the nodes that have already been visited.
        List<Node> closedList = new ArrayList<>();

        // While the open list is not empty
        while (!openList.isEmpty()) {

            // Get the node with the lowest f value
            Node currentNode = openList.poll();

            // If the next point is in a no-fly zone, or takes us out of the central area then ignore point.
            if (currentNode.getParent() != null
                    && (currentNode.getPoint().inNoFlyZone(currentNode.getParent().getPoint()) // Path from parent to current node goes through a no-fly zone.
                    || (end.inCentralArea() && currentNode.getParent().getPoint().inCentralArea() && !currentNode.getPoint().inCentralArea()) // Path from parent to current node takes us out of the central area.
            )) {
                continue;
            }

            // If we've found a point that is close to the endpoint.
            if (currentNode.getPoint().closeTo(end)) {
                // If we don't care about the next target then just reconstuct the path.
                if (nextTarget == null) {
                    return reconstructPath(currentNode);
                }

                // If we do care about the next target, then check all the nodes which are close to the current
                // target and get the one that is closest to the next target as well.
                Node newNode = findBestMoveInCloseRadius(currentNode.getParent(), end, nextTarget);
                return reconstructPath(newNode);
            }

            for (CompassDirection direction : CompassDirection.valuesNoHover()) {
                // Get the next node in the direction and add to list.
                LngLat newPoint = currentNode.getPoint().nextPosition(direction);

                // If the new point is already in the closed list, ignore it.
                if (closedList.stream().anyMatch(node -> node.getPoint().closeTo(newPoint))) continue;

                // Add the new node to the open list.
                Node newNode = new Node(newPoint, end, currentNode, direction);
                openList.add(newNode);
            }

            // Add the current node to the closed list.
            closedList.add(currentNode);
        }
        return null; // In the case that there is no route.
    }

    /**
     * Checks if there is multiple possible moves which result in being close to the destination
     * If this is the case, then it will find the number of moves from each one to the next target
     * and return the one with the least number of moves.
     */
    private static Node findBestMoveInCloseRadius(Node currentNode, LngLat end, LngLat nextTarget) {

        // Find all the points that are close to the end and the drone can get to from the current point in one
        // move.
        List<Node> validNodes = new ArrayList<>();
        for (CompassDirection direction : CompassDirection.valuesNoHover()) {
            LngLat newPoint = currentNode.getPoint().nextPosition(direction);
            if (newPoint.closeTo(end)) {
                validNodes.add(new Node(newPoint, end, currentNode, direction));
            }
        }

        // Find the node that is closest to the next target.
        Node closestNode = null;
        double minDist = Double.MAX_VALUE;
        for (Node node : validNodes) {
            double dist = node.getPoint().numberOfMovesTo(nextTarget);
            if (dist < minDist) {
                minDist = dist;
                closestNode = node;
            }

            // If the distance is the same, then choose the node that is in the same direction as the parent.
            // This is just a cosmetic preference for looking at the geojson and doesn't affect the algorithm.
            if (dist == minDist && node.getDirectionFromParent() == node.getParent().getDirectionFromParent()) {
                closestNode = node;
            }
        }

        // Return the node that is close to the current end and is closest to the next target.
        return closestNode;
    }

    /**
     * Reconstruct the path and record the directions that were taken.
     * To do this we simply go from child to parent until we reach the start node.
     * The directions are recorded in reverse order, so we reverse the list before returning.
     *
     * @param currentNode the node that is the end of the path.
     * @return the directions that were taken to get to this end node.
     */
    private static CompassDirection[] reconstructPath(Node currentNode) {
        List<CompassDirection> route = new ArrayList<>();

        // Go from child to parent until we reach the start node.
        while (currentNode.getParent() != null) {
            // Record the direction that was taken.
            route.add(currentNode.getDirectionFromParent());
            currentNode = currentNode.getParent();
        }

        // Since we recorded the directions in reverse order, we need to reverse the list.
        Collections.reverse(route);

        // All routes must end in a hover.
        route.add(CompassDirection.HOVER);
        return route.toArray(new CompassDirection[0]);
    }
}