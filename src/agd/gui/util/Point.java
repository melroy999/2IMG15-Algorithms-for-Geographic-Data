package agd.gui.util;

import agd.math.Point2d;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Draw a point in the gui.
 */
public class Point implements IDrawable {
    // The point is represented as an elliptical shape.
    private final Ellipse2D shape;
    private final Color color;

    private static int r = 10;

    /**
     * Create a point graphic shape.
     *
     * @param p the point to take the coordinates from.
     */
    public Point(Point2d p) {
        this(p, Color.black, r);
    }

    /**
     * Create a point graphic shape.
     *
     * @param p the point to take the coordinates from.
     */
    public Point(Point2d p, Color color) {
        this(p, color, r);
    }

    /**
     * Create a point graphic shape.
     *
     * @param p the point to take the coordinates from.
     * @param r the radius of the point to draw.
     */
    public Point(Point2d p, double r) {
        this(p, Color.black, r);
    }

    /**
     * Create a point graphic shape.
     *
     * @param p the point to take the coordinates from.
     * @param r the radius of the point to draw.
     */
    public Point(Point2d p, Color color, double r) {
        // Since the center point is (x,y), we have to do some calculations with the radius.
        this.shape = new Ellipse2D.Double(p.x - r, p.y - r, 2 * r, 2 * r);
        this.color = color;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        // Draw the shape, with the desired color.
        g.setColor(color);
        g.fill(shape);
    }
}
