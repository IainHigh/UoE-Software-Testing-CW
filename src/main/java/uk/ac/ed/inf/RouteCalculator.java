package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class RouteCalculator {

    private static class Node{
        LngLat point;
        double g;
        double h;
        Node parent = null;
        CompassDirection directionFromParent = null;

        public Node(LngLat point, LngLat end) {
            this.point = point;
            this.h = point.distanceTo(end); // Heuristic is the straight line function from the current point to the
            // end point.
            this.g = 0;
        }

        public Node(LngLat point, LngLat end, Node parent, CompassDirection directionFromParent) {
            this.point = point;
            this.parent = parent;
            this.directionFromParent = directionFromParent;
            this.h = point.distanceTo(end); // Heuristic is the straight line function from the current point to the
            // end point.
            this.g = parent.g + 0.00015; // The cost of the path from the start to the current point. The 0.00015
            // shouldn't be hard coded - maybe have a class for constants? TODO.
        }
    }

    private static class NodeComparator implements java.util.Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return Double.compare(o1.g + 2*o1.h, o2.g + 2*o2.h);
        }
    }

    /**
     * Use the A* algorithm to find the shortest route from the start to the end.
     * The heuristic will be the straight line distance (distanceTo) between the current node and the end node.
     * @param start the start node.
     * @param end the end node.
     * @return the shortest route from the start to the end.
     */
    public static CompassDirection[] calculateRoute(LngLat start, LngLat end) {

        // This will be used to go through every possible direction.
        CompassDirection[] directions = CompassDirection.values();

        // Create the priority queue and add the start node.
        PriorityQueue<Node> openList = new PriorityQueue<>(new NodeComparator());
        openList.add(new Node(start, end));

        // While the open list is not empty
        while (!openList.isEmpty()) {

            // Get the node with the lowest f value
            Node currentNode = openList.poll();
            for (CompassDirection direction : directions) {
                // Get the next node in the direction
                LngLat newPoint = currentNode.point.nextPosition(direction);
                if (newPoint.closeTo(end)) {
                    // We have found a route to the endpoint. Now we just reconstruct by going back through the parents.
                    List<CompassDirection> route = new ArrayList<>();
                    while (currentNode.parent != null) {
                        route.add(currentNode.directionFromParent);
                        currentNode = currentNode.parent;
                    }
                    return route.toArray(new CompassDirection[0]);
                }
                // Add the node to the open list.
                Node newNode = new Node(newPoint, end, currentNode, direction);
                openList.add(newNode);
            }
            openList.remove(currentNode);
        }
        return null;
    }
}
