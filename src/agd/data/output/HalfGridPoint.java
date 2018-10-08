package agd.data.output;

import agd.data.input.WeightedPoint;
import agd.math.Point2d;

/**
 * A data structure for a point of which the coordinates are always a multiple of a half.
 */
public class HalfGridPoint {
    // The x and y coordinates doubled.
    private final int x_double, y_double;

    // The original weighted point.
    public final WeightedPoint o;

    /**
     * Construct a point that will always satisfy the half-multiple requirement.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param original The coordinates of the original point.
     */
    public HalfGridPoint(double x, double y, WeightedPoint original) {
        // Store the x and y coordinates as a integer, such that we can always assure that we get exact halves.
        this.x_double = (int) (2 * x);
        this.y_double = (int) (2 * y);
        this.o = original;

        // Validate the point, i.e. make sure that the corner points are on integer positions.
        if(original.w % 2 == 1) {
            if(x_double % 2 == 0 || y_double == 0) {
                throw new IllegalArgumentException(
                        "The coordinates of an odd weighted point should " +
                                "always be at the center of the cells in the grid.");
            }
        } else {
            if(x_double % 2 == 1 || y_double == 1) {
                throw new IllegalArgumentException(
                        "The coordinates of an even weighted point should " +
                                "always be at the intersection points of the grid.");
            }
        }
    }

    /**
     * Get the coordinates of the point as a mathematical 2d point.
     *
     * @return A point with the internal coordinates divided by two.
     */
    public Point2d point() {
        return new Point2d(x_double / 2.0, y_double / 2.0);
    }

    /**
     * Check whether the chosen assignment for the centroid leads to overlap with another point.
     *
     * @param q The point to check overlap with.
     * @return Whether the two regions associated with the points overlap.
     */
    public boolean hasOverlap(HalfGridPoint q) {
        Point2d a = point().add(new Point2d(o.w, o.w).scale(-0.5d));
        Point2d b = q.point().add(new Point2d(q.o.w, q.o.w).scale(-0.5d));
        int w = a.x <= b.x ? o.w : q.o.w;
        return Math.abs(a.x - b.x) < w && Math.abs(a.y - b.y) < w;
    }

    public String toString() {
        return "[" + this.point().x + ", " + this.point().y + ", " + this.o.i + ", " + this.o.w +  "]";
    }
}
