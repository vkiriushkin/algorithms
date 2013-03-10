import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: fantik911
 * Date: 09.03.13
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class KdTree {

    private Set<Node> setOfNodes = new TreeSet<Node>();
    private Node root;
    private int levelIndex;

    // construct an empty kd-tree
    public KdTree() {

    }

    // is the tree empty?
    public boolean isEmpty() {
        return setOfNodes.isEmpty();
    }

    // number of points in the tree
    public int size() {
        return setOfNodes.size();
    }

    // add the point p to the tree (if it is not already in the tree)
    public void insert(Point2D p) {
        levelIndex = 0;
        root = insertPoint(root, p);
    }

    private Node insertPoint(Node node, Point2D point) {
        if (node == null) return new Node(point);
        double cmp;
        if (levelIndex % 2 == 0) {
            cmp = point.x() - (node.p.x());
            levelIndex++;
        } else {
            cmp = point.y() - (node.p.y());
            levelIndex++;
        }

        if (cmp < 0) node.lb = insertPoint(node.lb, point);
        else if (cmp >= 0) node.rt = insertPoint(node.rt, point);

        return node;
    }

    // does the tree contain the point p?
    public boolean contains(Point2D p) {
        levelIndex = 0;
        return containsPoint(root, p);
    }

    private boolean containsPoint(Node node, Point2D point) {
        if (node == null) return false;

        double cmp;
        if (levelIndex % 2 == 0) {
            cmp = point.x() - (node.p.x());
            levelIndex++;
        } else {
            cmp = point.y() - (node.p.y());
            levelIndex++;
        }

        if (cmp < 0) return containsPoint(node.lb, point);
        else if (cmp > 0) return containsPoint(node.rt, point);
        else return true;
    }

    // draw all of the points to standard draw
    public void draw() {
        Node current = root;
        StdDraw.point(current.p.x(),current.p.y());
        StdDraw.line(current.p.x(),0,current.p.x(),current.p.y());
        drawLeftChild(current);
        drawRightChild(current);
    }

    private void drawLeftChild(Node root) {
        Node leftChild = root.lb;
        StdDraw.point(leftChild.p.x(),leftChild.p.y());
        StdDraw.line(0,leftChild.p.y(),root.p.x(),leftChild.p.y());
    }

    private void drawRightChild(Node root) {

    }

    // all points in the tree that are inside the rectangle
//    public Iterable<Point2D> range(RectHV rect) {
//
//    }
//
//    // a nearest neighbor in the tree to p; null if tree is empty
//    public Point2D nearest(Point2D p) {
//
//    }

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p) {
            this.p = p;
        }
    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);

        // initialize the two data structures with point from standard input
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        System.out.println(kdtree.contains(new Point2D(0.024472,0.345492)));
        System.out.println(kdtree.contains(new Point2D(0.024472,0.654508)));
        System.out.println(kdtree.contains(new Point2D(0.500000,1.000000)));
    }
}
