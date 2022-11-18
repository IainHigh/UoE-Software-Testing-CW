package uk.ac.ed.inf;

import IO.RestAPIDataSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class RouteCalculator {

    /**
     * Internal class used to represent a node in the graph.
     */
    private static class Node {
        LngLat point;
        double g; // The cost of the path from the start node to this node.
        double h; // The heuristic cost of the path from this node to the goal node. Heuristic chosen is the straight
        // line distance from the current point to the end point. This is an admissible heuristic because it never
        // overestimates the cost of the path.
        Node parent;
        CompassDirection directionFromParent;

        public Node(LngLat point, LngLat end) {
            this.point = point;
            this.parent = null;
            this.directionFromParent = null;
            this.h = calculateHeuristic(end);
            this.g = 0;
        }

        public Node(LngLat point, LngLat end, Node parent, CompassDirection directionFromParent) {
            this.point = point;
            this.parent = parent;
            this.directionFromParent = directionFromParent;
            this.h = calculateHeuristic(end);
            this.g = parent.g + Constants.LENGTH_OF_MOVE;
        }

        private double calculateHeuristic(LngLat end) {
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
                    if (LngLat.lineIntersects(p1, p2, this.point, end)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    double min = Double.MAX_VALUE;
                    for (LngLat p : zone) {
                        double dist = this.point.distanceTo(p) + p.distanceTo(end);
                        if (dist < min) {
                            min = dist;
                        }
                    }
                    return min;
                }
            }
            return this.point.distanceTo(end);
        }
    }

    private static class NodeComparator implements java.util.Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return Double.compare(o1.g + o1.h, o2.g + o2.h);
        }
    }

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
            if ((currentNode.parent != null) && (currentNode.point.inNoFlyZone(currentNode.parent.point))
                    || (targetInCentralArea && !currentNode.point.inCentralArea() && (currentNode.parent != null) && currentNode.parent.point.inCentralArea())
            ) {
                // If the next point is in a no-fly zone, or if the next point takes us out of the central area then
                // ignore the point.
                continue;
            }
            if (currentNode.point.closeTo(end)) {
                // We have found a route to the endpoint. Now we just reconstruct by going back through the parents.
                if (nextTarget == null) {
                    return reconstructPath(currentNode);
                }
                Node newNode = test(currentNode.parent, end, nextTarget);
                return reconstructPath(newNode);
            }
            for (CompassDirection direction : CompassDirection.valuesNoHover()) {
                // Get the next node in the direction and add to list.
                LngLat newPoint = currentNode.point.nextPosition(direction);
                boolean flag = false;
                for (Node node : closedList) {
                    if (node.point.closeTo(newPoint)) {
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

    private static Node test(Node currentNode, LngLat end, LngLat nextTarget) {
        List<Node> validNodes = new ArrayList<>();
        for (CompassDirection direction : CompassDirection.valuesNoHover()) {
            LngLat newPoint = currentNode.point.nextPosition(direction);
            if (newPoint.closeTo(end)) {
                validNodes.add(new Node(newPoint, end, currentNode, direction));
            }
        }
        // Find the node that is closest to the next target.
        Node closestNode = null;
        double minDist = Double.MAX_VALUE;
        for (Node node : validNodes) {
            double dist = node.point.numberOfMovesTo(nextTarget);
            if (dist < minDist) {
                minDist = dist;
                closestNode = node;
            }
            if (dist == minDist && node.directionFromParent == node.parent.directionFromParent) {
                // If the distance is the same, then choose the node that is in the same direction as the parent.
                // This is just a cosmetic preference for looking at the geojson and doesn't affect the algorithm.
                System.out.println("HERE");
                closestNode = node;
            }
        }
        return closestNode;
    }

    private static CompassDirection[] reconstructPath(Node currentNode) {
        List<CompassDirection> route = new ArrayList<>();
        while (currentNode.parent != null) {
            route.add(currentNode.directionFromParent);
            currentNode = currentNode.parent;
        }
        Collections.reverse(route);
        return route.toArray(new CompassDirection[0]);
    }
}