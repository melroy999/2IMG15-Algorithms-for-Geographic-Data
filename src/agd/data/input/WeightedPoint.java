package agd.data.input;

import agd.data.outline.OutlineRectangle;
import agd.math.Point2d;
import agd.math.Point2i;

/**
 * An immutable data structure that represents a weighted point in the two-dimensional plane.
 */
public class WeightedPoint extends Point2d {
    // The assigned weight of the point.
    public final int w;

    // The id of the point, which is the location it occurs at in the list.
    public final int i;

    // The position of the closest point on the half interval grid.
    public final Point2d c;

    /**
     * Create a point in the two-dimensional plane with an accompanying weight.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param w The weight of the point.
     * @param i The id of the point.
     */
    public WeightedPoint(double x, double y, int w, int i) {
        super(x, y);
        this.w = w;
        this.i = i;

        // Calculate the coordinates of the reference point.
        Point2i bottomLeft = new Point2i((int) Math.round(x - 0.5d * w), (int) Math.round(y - 0.5d * w));
        c = new Point2d(bottomLeft.x + 0.5d * w, bottomLeft.y + 0.5d * w);
    }

    /**
     * Convert the preferred placement of a weighted point to a outline rectangle.
     *
     * @return An outline rectangle with the point c at the center.
     */
    public OutlineRectangle getOutlineRectangle() {
        return new OutlineRectangle(
                (int) Math.round(c.x - 0.5 * w),
                (int) Math.round(c.y - 0.5 * w),
                w,
                this
        );
    }

    /**
     * Convert the preferred placement of a weighted point to a outline rectangle.
     *
     * @param p The chosen placement for the center point.
     * @return An outline rectangle with the point c at the center.
     */
    public OutlineRectangle getOutlineRectangle(Point2d p) {
        return new OutlineRectangle(
                (int) Math.round(p.x - 0.5 * w),
                (int) Math.round(p.y - 0.5 * w),
                w,
                this
        );
    }

    // TODO possibly add a better equality and hashcode.
}
