package agd.data.output;

import agd.data.input.WeightedPoint;
import agd.gui.util.Rectangle;
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

    public static HalfGridPoint make(Point2d c, WeightedPoint original) {
        if(original.w % 2 == 0) {
            return new HalfGridPoint(Math.round(c.x), Math.round(c.y), original);
        } else {
            return new HalfGridPoint(Math.round(c.x - 0.5d) + 0.5d, Math.round(c.y - 0.5d) + 0.5d, original);
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
        java.awt.Rectangle r1 = new java.awt.Rectangle(x_double - o.w, y_double - o.w, 2 * o.w, 2 * o.w);
        java.awt.Rectangle r2 = new java.awt.Rectangle(q.x_double - q.o.w, q.y_double - q.o.w, 2 * q.o.w, 2 * q.o.w);
        return r1.intersects(r2);
    }
}
