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
    private static class Node{
        LngLat point;
        double g; // The cost of the path from the start node to this node.
        double h; // The heuristic cost of the path from this node to the goal node. Heuristic chosen is the straight
        // line distance from the current point to the end point. This is an admissible heuristic because it never
        // overestimates the cost of the path.
        Node parent = null;
        CompassDirection directionFromParent = null;

        public Node(LngLat point, LngLat end) {
            this.point = point;
            this.h = point.distanceTo(end);
            this.g = 0;
        }

        public Node(LngLat point, LngLat end, Node parent, CompassDirection directionFromParent) {
            this.point = point;
            this.parent = parent;
            this.directionFromParent = directionFromParent;
            this.h = point.distanceTo(end);
            this.g = parent.g + Constants.LENGTH_OF_MOVE;
        }
    }

    private static class NodeComparator implements java.util.Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return Double.compare(o1.g + 1.05*o1.h, o2.g + 1.05*o2.h);
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

        boolean targetInCentralArea = end.inCentralArea();

        // While the open list is not empty
        while (!openList.isEmpty()) {

            // Get the node with the lowest f value
            Node currentNode = openList.poll();
            if ( (currentNode.parent != null) && (currentNode.point.inNoFlyZone(currentNode.parent.point) )
                || (currentNode.point.inNoFlyZone())
                || (targetInCentralArea && !currentNode.point.inCentralArea() && (currentNode.parent != null) && currentNode.parent.point.inCentralArea())
            ) {
                // If the next point is in a no-fly zone, or if the next point takes us out of the central area then
                // ignore the point.
                continue;
            }
            if (currentNode.point.closeTo(end)) {
                // We have found a route to the endpoint. Now we just reconstruct by going back through the parents.
                List<CompassDirection> route = new ArrayList<>();
                while (currentNode.parent != null) {
                    route.add(currentNode.directionFromParent);
                    currentNode = currentNode.parent;
                }
                Collections.reverse(route);
                return route.toArray(new CompassDirection[0]);
            }
            for (CompassDirection direction : directions) {
                // Get the next node in the direction and add to list.
                LngLat newPoint = currentNode.point.nextPosition(direction);
                Node newNode = new Node(newPoint, end, currentNode, direction);
                openList.add(newNode);
            }
        }
        return null; // In the case that there is no route.
    }

    /**
     * Goes through every pair of lines that make up the central area border and finds the shortest route from the
     * current point to get inside central area.
     * @param point the current point.
     * @return The closest LngLat point which lies in the central area.
     */
    public static LngLat findClosestPointInCentralArea(LngLat point) {
        LngLat[] centralAreaBorder = RestAPIDataSingleton.getInstance().getCentralAreaBorder();
        double minDistance = Double.MAX_VALUE;
        LngLat closestPoint = null;
        for (int i = 0; i < centralAreaBorder.length; i++) {
            LngLat lineStart = new LngLat(centralAreaBorder[i].lng(), centralAreaBorder[i].lat());
            LngLat lineEnd = new LngLat(centralAreaBorder[(i+1) % centralAreaBorder.length].lng(),
                    centralAreaBorder[(i+1) % centralAreaBorder.length].lat());
            LngLat closestPointOnLine = closestPointOnLine(point, lineStart, lineEnd);
            double distance = point.distanceTo(closestPointOnLine);
            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = closestPointOnLine;
            }
        }
        return closestPoint;
    }

    /**
     * Given three points, calculate the point on the line between the first two points which is closest to the third point.
     * @param point the point to find the closest point on the line to.
     * @param lineStart the start of the line.
     * @param lineEnd the end of the line.
     * @return the closest point on the line to the given point.
     */
    private static LngLat closestPointOnLine(LngLat point, LngLat lineStart, LngLat lineEnd) {
        double x1 = lineStart.lng();
        double y1 = lineStart.lat();
        double x2 = lineEnd.lng();
        double y2 = lineEnd.lat();
        double x3 = point.lng();
        double y3 = point.lat();
        if ( (x3 < Math.max(x1, x2) && x3 > Math.min(x1, x2) )
                || ( y3 < Math.max(y1, y2) && y3 > Math.min(y1, y2) )
        ) {
            // If the point lies between the two points start and end.
            double x4 = x3 + (y2 - y1);
            double y4 = y3 + (x1 - x2);
            double v = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
            double x = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / v;
            double y = ((x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4)) / v;
            return new LngLat(x, y);
        }
        // If the point is not between the two points then we just return the closest point.
        return point.distanceTo(lineStart) < point.distanceTo(lineEnd) ? lineStart : lineEnd;
    }
}