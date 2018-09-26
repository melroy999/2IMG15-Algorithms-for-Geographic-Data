package agd.state.util;

import agd.math.Point2d;
import agd.math.Point2i;

public class WeightedPoint extends Point2d {
    // The index of the point.
    public final int id;

    // The weight of the point.
    public final int weight;

    // The coordinates defining the rectangle with the best potential placement.
    public final Point2i bl, br, tl, tr;
    public final Point2d c;

    // The centroid of the final placement.
    public Point2d assigned;

    /**
     * Create a point in 2d space with a weight and id.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param weight The weight of the point.
     * @param id The id of the point.
     */
    WeightedPoint(double x, double y, int weight, int id) {
        super(x, y);
        this.weight = weight;
        this.id = id;

        // Find the rectangle placement closest to the point.
        bl = new Point2i((int) Math.round(x - 0.5d * weight), (int) Math.round(y - 0.5d * weight));
        br = bl.add(new Point2i(weight, 0));
        tl = bl.add(new Point2i(0, weight));
        tr = bl.add(new Point2i(weight, weight));
        c = new Point2d(bl.x + 0.5d * weight, bl.y + 0.5d * weight);

        // For now, we make the assigned point the closest grid point.
        assigned = new Point2d(c.x, c.y);
    }

    /**
     * Check whether the chosen assignment for the centroid leads to overlap with another point.
     *
     * @param t The point to check overlap with.
     * @return Whether the two regions associated with the points overlap.
     */
    public boolean hasOverlap(WeightedPoint t) {
        Point2d a = assigned.add(new Point2d(weight, weight).scale(-0.5d));
        Point2d b = t.assigned.add(new Point2d(t.weight, t.weight).scale(-0.5d));
        int w = a.x <= b.x ? weight : t.weight;

        return Math.abs(a.x - b.x) < w && Math.abs(a.y - b.y) < w;
    }
}
