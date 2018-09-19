package agd.store.math;

import java.util.Locale;

/**
 * A class representing a 2d point.
 */
public class Point2i extends Tuple2i<Point2i> {
    /**
     * Define a point by giving an x and y-coordinates.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public Point2i(int x, int y) {
        super(x, y);
    }

    /**
     * Define a point with coordinates 0, 0.
     */
    public Point2i() {
        super();
    }

    /**
     * Create a new instance of a tuple2d subclass.
     *
     * @param x The x-coordinate of the tuple.
     * @param y The y-coordinate of the point.
     * @return An instance of the desired type, that is an extension of Tuple2d.
     */
    @Override
    protected Point2i get(int x, int y) {
        return new Point2i(x, y);
    }

    /**
     * Get the string representation of the vertex.
     *
     * @return v concatenated with the id, together with its coordinates.
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
