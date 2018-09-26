package agd.gui.util;

import agd.math.Point2d;
import agd.math.Tuple2d;

import java.awt.*;

/**
 * Draw a polygon face in the gui.
 */
public class Polygon implements IDrawable {
    // The shape that contains a renderable polygon.
    private final java.awt.Polygon shape;

    // The x and y coordinates of the center point of the polygon as integers.
    private final int x, y;

    // Additional information for rendering the point, like the color and labels.
    private final String label;

    /**
     * Define a polygon, given a set of points.
     *
     * @param points The points that the polygon is made out of.
     */
    public Polygon(String label, Tuple2d... points) {
        // Set the label and color.
        this.label = label;

        // Now, create a shape, and determine the position for the label.
        Point2d center = new Point2d();
        shape = new java.awt.Polygon();
        for (Tuple2d p : points) {
            shape.addPoint((int) p.x, (int) p.y);
            center.add(p);
        }

        // Set the center point.
        center = center.scale(1d / points.length);
        x = (int) center.x;
        y = (int) center.y;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        // Draw the shape, with the desired color.
        g.fill(shape);
    }
}
