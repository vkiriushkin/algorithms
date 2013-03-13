import java.awt.*;
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

    private final int VERTICAL = 1;
    private final  int HORIZONTAL = 0;

    private Set<Point2D> setOfNodes = new TreeSet<Point2D>();
    private Node root;
    private Node tempParent;
    private int levelIndex;
    private Set<Point2D> pointsInRange;
    private Point2D currentChamp;
    private double shortestDistance;

    // construct an empty kd-tree
    public KdTree() {

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
        System.out.println(kdtree.size());
//        kdtree.draw();
//        StdDraw.setPenRadius(.02);
//        StdDraw.setPenColor(StdDraw.GREEN);
//        StdDraw.point(0.8544921875,0.6783203125);
//        System.out.println(kdtree.nearest(new Point2D(0.8544921875,0.6783203125)));
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
        double cmp;
        if (node == null) {
            Node rootNode = new Node(point);
            if (tempParent == null) {
                rootNode.rect = new RectHV(0,0,1,1);
                rootNode.orientation = VERTICAL;
            } else {
                if (levelIndex % 2 == 1) {
                    rootNode.orientation = HORIZONTAL;
                    cmp = point.x() - (tempParent.p.x());
                    if (cmp < 0 ) {
                        rootNode.rect = new RectHV(tempParent.rect.xmin(),tempParent.rect.ymin(),tempParent.p.x(),tempParent.rect.ymax());
                    } else {
                        rootNode.rect = new RectHV(tempParent.p.x(),tempParent.rect.ymin(),tempParent.rect.xmax(),tempParent.rect.ymax());
                    }
                } else {
                    rootNode.orientation = VERTICAL;
                    cmp = point.y() - (tempParent.p.y());
                    if (cmp < 0 ) {
                        rootNode.rect = new RectHV(tempParent.rect.xmin(),tempParent.rect.ymin(),tempParent.rect.xmax(),tempParent.p.y());
                    } else {
                        rootNode.rect = new RectHV(tempParent.rect.xmin(),tempParent.p.y(),tempParent.rect.xmax(),tempParent.rect.ymax());
                    }
                }

            }
            setOfNodes.add(rootNode.p);
            return rootNode;
        }
        if (levelIndex % 2 == 0) {
            cmp = point.x() - (node.p.x());
            levelIndex++;
        } else {
            cmp = point.y() - (node.p.y());
            levelIndex++;
        }

        if (cmp < 0) {
            tempParent = node;
            tempParent.rect = node.rect;
            node.lb = insertPoint(node.lb, point);
        } else if (cmp >= 0) {
            tempParent = node;
            tempParent.rect = node.rect;
            node.rt = insertPoint(node.rt, point);
        }

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
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.02);
        StdDraw.point(current.p.x(),current.p.y());
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(.01);
        StdDraw.line(current.p.x(),0,current.p.x(),1);

        if (current.lb != null) {
            drawLeftChild(current);
        }
        if (current.rt != null) {
            drawRightChild(current);
        }
    }

    private void drawLeftChild(Node root) {
        Node leftChild = root.lb;
        StdDraw.setPenRadius(.02);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.point(leftChild.p.x(),leftChild.p.y());
        if (leftChild.orientation == HORIZONTAL) {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius(.01);
            StdDraw.line(root.rect.xmin(),leftChild.p.y(),root.p.x(),leftChild.p.y());
        } else {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius(.01);
            StdDraw.line(leftChild.p.x(),root.rect.ymin(),leftChild.p.x(),root.p.y());
        }
        if (leftChild.lb != null) {
            drawLeftChild(leftChild);
        }
        if (leftChild.rt != null) {
            drawRightChild(leftChild);
        }
    }

    // all points in the tree that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        pointsInRange = new TreeSet<Point2D>();
        if (rect.contains(root.p))
            pointsInRange.add(root.p);
        childInRange(root,rect);
        return pointsInRange;
    }

    private void childInRange(Node root, RectHV rect) {
        if (root.rt != null && root.rt.rect.intersects(rect)) {
            if (rect.contains(root.rt.p)) {
                pointsInRange.add(root.rt.p);
            }
            childInRange(root.rt, rect);
        }
        if (root.lb != null && root.lb.rect.intersects(rect)) {
            if (rect.contains(root.lb.p)) {
                pointsInRange.add(root.lb.p);
            }
            childInRange(root.lb, rect);
        }
    }


    // a nearest neighbor in the tree to p; null if tree is empty
    public Point2D nearest(Point2D p) {
        if (setOfNodes.isEmpty()) {
            return null;
        }

        currentChamp = root.p;
        shortestDistance = p.distanceTo(currentChamp);
        if (root.lb.rect.contains(p)) {
            inspectChildren(p,root.lb);
        } else {
            inspectChildren(p,root.rt);
        }

        return currentChamp;
    }

    private void inspectChildren(Point2D queryPoint, Node nodeToInspect) {
        if (queryPoint.distanceTo(nodeToInspect.p) < shortestDistance) {
            currentChamp = nodeToInspect.p;
            shortestDistance = queryPoint.distanceTo(nodeToInspect.p);
        }
        if (nodeToInspect.lb != null || nodeToInspect.rt != null) {
            if (nodeToInspect.lb != null) {
                if (nodeToInspect.lb.rect.contains(queryPoint)) {
                    inspectChildren(queryPoint,nodeToInspect.lb);
                } else {
                    if (nodeToInspect.rt != null) {
                        inspectChildren(queryPoint,nodeToInspect.rt);
                    }
                }
            } else {
                inspectChildren(queryPoint,nodeToInspect.rt);
            }
        }
    }

    private void drawRightChild(Node root) {
        Node rightChild = root.rt;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.02);
        StdDraw.point(rightChild.p.x(),rightChild.p.y());
        if (rightChild.orientation == HORIZONTAL) {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius(.01);
            StdDraw.line(rightChild.rect.xmin(),rightChild.p.y(),rightChild.rect.xmax(),rightChild.p.y());
        } else {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius(.01);
            StdDraw.line(rightChild.p.x(),rightChild.rect.ymin(),rightChild.p.x(),rightChild.rect.ymax());
        }
        if (rightChild.lb != null) {
            drawLeftChild(rightChild);
        }
        if (rightChild.rt != null) {
            drawRightChild(rightChild);
        }
    }

    private static class Node{
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private int orientation;

        public Node(Point2D p) {
            this.p = p;
        }
    }
}
