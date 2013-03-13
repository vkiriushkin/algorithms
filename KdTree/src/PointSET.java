import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: fantik911
 * Date: 09.03.13
 * Time: 10:10
 * To change this template use File | Settings | File Templates.
 */
public class PointSET {

    private Set<Point2D> setOfPoints;

    // construct an empty set of points
    public PointSET() {
        setOfPoints = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return setOfPoints.isEmpty();
    }

    // number of points in the set
    public int size() {
        return setOfPoints.size();
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        setOfPoints.add(p);
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return setOfPoints.contains(p);
    }

    // draw all of the points to standard draw
    public void draw() {
        for (Point2D point:setOfPoints)
            StdDraw.point(point.x(), point.y());
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Set<Point2D> pointsInRect = new TreeSet<Point2D>();
        for (Point2D point: setOfPoints) {
            if (rect.contains(point))
                pointsInRect.add(point);
        }

        return pointsInRect;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if (setOfPoints.isEmpty())
            return null;

        Point2D nearest = null;
        for(Point2D point: setOfPoints) {
            if (nearest == null) {
                nearest = point;
            } else {
                if (p.distanceTo(point) < p.distanceTo(nearest)) {
                    nearest = point;
                }
            }
        }

        return nearest;
    }

//    public static void main(String[] args) {
//        String filename = args[0];
//        In in = new In(filename);
//
//
//        // initialize the two data structures with point from standard input
//        PointSET brute = new PointSET();
//        for (int i=0; i<100000; i++) {
//            brute.insert(new Point2D(StdRandom.random(),StdRandom.random()));
//        }
//        while (!in.isEmpty()) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            Point2D p = new Point2D(x, y);
//            brute.insert(p);
//        }
//        System.out.println(brute.size());
//    }




}

