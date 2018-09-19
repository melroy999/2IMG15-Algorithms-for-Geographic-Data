package agd.store.instance;

import agd.store.math.Point2d;

import java.util.HashSet;
import java.util.Set;

public class ProblemInstance {
    // The id of the problem statement.
    public final int id;

    // The bounds of the problem statements viewport.
    public final int minx, maxx, miny, maxy;

    // The collection of points in the problem statement.
    private WeightedPointList points = new WeightedPointList();

    public ProblemInstance(int id, int minx, int maxx, int miny, int maxy) {
        this.id = id;
        this.minx = minx;
        this.maxx = maxx;
        this.miny = miny;
        this.maxy = maxy;
    }

    public void addPoint(double x, double y, int w) {
        points.add(x, y, w);
    }

    public WeightedPointList getPoints() {
        return new WeightedPointList(points);
    }

    public double getMinimumError() {
        return points.stream().map(p -> p.distance2(p.c)).reduce(0d, Double::sum);
    }

    public double getTotalError() {
        return points.stream().map(p -> p.distance2(p.assigned)).reduce(0d, Double::sum);
    }

    public Set<Integer> getInvalidPoints() {
        // A set of integers holding all points that have faulty placements.
        HashSet<Integer> errors = new HashSet<>();

        for(WeightedPoint p : points) {
            // First check if the coordinates are valid.
            Point2d bl = p.assigned.add(new Point2d(p.weight, p.weight).scale(-0.5d));
            Point2d blRounded = new Point2d(Math.round(bl.x), Math.round(bl.y));

            if(!bl.epsilonEquals(blRounded, 0.01)) {
                // The assigned point is invalid, since the corner points are not on integer positions.
                errors.add(p.id);
            }

            // Next, check if the node overlaps with other regions.
            for(WeightedPoint q : points) {
                if(p.id != q.id && p.hasOverlap(q)) {
                    errors.add(p.id);
                }
            }
        }

        return errors;
    }
}
