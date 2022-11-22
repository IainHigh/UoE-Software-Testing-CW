package RouteCalculation;

import uk.ac.ed.inf.CompassDirection;
import uk.ac.ed.inf.LngLat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

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

        if (start.closeTo(end)) {
            return new CompassDirection[]{};
        }

        // Create the priority queue and add the start node.
        PriorityQueue<Node> openList = new PriorityQueue<>(new NodeComparator());
        openList.add(new Node(start, end));

        // Create a list of the nodes that have already been visited.
        List<Node> closedList = new ArrayList<>();

        boolean targetInCentralArea = end.inCentralArea();

        // While the open list is not empty
        while (!openList.isEmpty()) {

            // Get the node with the lowest f value
            Node currentNode = openList.poll();
            if ((currentNode.getParent() != null) && (currentNode.getPoint().inNoFlyZone(currentNode.getParent().getPoint()))
                    || (targetInCentralArea && !currentNode.getPoint().inCentralArea() && (currentNode.getParent() != null) && currentNode.getParent().getPoint().inCentralArea())
            ) {
                // If the next point is in a no-fly zone, or if the next point takes us out of the central area then
                // ignore the point.
                continue;
            }
            if (currentNode.getPoint().closeTo(end)) {
                // We have found a route to the endpoint. Now we just reconstruct by going back through the parents.
                if (nextTarget == null) {
                    return reconstructPath(currentNode);
                }
                Node newNode = findBestMoveInCloseRadius(currentNode.getParent(), end, nextTarget);
                return reconstructPath(newNode);
            }
            for (CompassDirection direction : CompassDirection.valuesNoHover()) {
                // Get the next node in the direction and add to list.
                LngLat newPoint = currentNode.getPoint().nextPosition(direction);
                boolean flag = false;
                for (Node node : closedList) {
                    if (node.getPoint().closeTo(newPoint)) {
                        // If the new point is close to a point in the closed list, then ignore it.
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
                Node newNode = new Node(newPoint, end, currentNode, direction);
                openList.add(newNode);
            }
            closedList.add(currentNode);
        }
        return null; // In the case that there is no route.
    }

    private static Node findBestMoveInCloseRadius(Node currentNode, LngLat end, LngLat nextTarget) {
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
            if (dist == minDist && node.getDirectionFromParent() == node.getParent().getDirectionFromParent()) {
                // If the distance is the same, then choose the node that is in the same direction as the parent.
                // This is just a cosmetic preference for looking at the geojson and doesn't affect the algorithm.
                closestNode = node;
            }
        }
        return closestNode;
    }

    private static CompassDirection[] reconstructPath(Node currentNode) {
        List<CompassDirection> route = new ArrayList<>();
        while (currentNode.getParent() != null) {
            route.add(currentNode.getDirectionFromParent());
            currentNode = currentNode.getParent();
        }
        Collections.reverse(route);

        // All routes must end in a hover.
        route.add(CompassDirection.HOVER);
        return route.toArray(new CompassDirection[0]);
    }
}