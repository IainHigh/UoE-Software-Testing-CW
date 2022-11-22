package RouteCalculation;

/**
 * A comparator for comparing two nodes.
 * The node with the lowest f value is the smallest.
 */
class NodeComparator implements java.util.Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        return Double.compare(o1.getFScore(), o2.getFScore());
    }
}
