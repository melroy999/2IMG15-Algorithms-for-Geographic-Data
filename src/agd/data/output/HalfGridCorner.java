package agd.data.output;

import agd.data.input.WeightedPoint;
import agd.math.Point2d;

public class HalfGridCorner {
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
    public HalfGridCorner(double x, double y, WeightedPoint original) {
        // Store the x and y coordinates as a integer, such that we can always assure that we get exact halves.
        this.x_double = (int) (2 * x);
        this.y_double = (int) (2 * y);
        this.o = original;
    }

    /**
     * Get the coordinates of the point as a mathematical 2d point.
     *
     * @return A point with the internal coordinates divided by two.
     */
    public Point2d point() {
        return new Point2d(x_double / 2.0, y_double / 2.0);
    }
}
