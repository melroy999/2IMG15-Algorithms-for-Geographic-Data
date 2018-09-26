package agd.gui.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Rectangle implements IDrawable {
    // The shape that contains a renderable rectangle.
    private final Rectangle2D shape;

    /**
     * Create a square graphic shape.
     *
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public Rectangle(double x, double y, double width, double height) {
        this.shape = new Rectangle2D.Double(x, y, width, height);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.orange);
        g.draw(shape);
    }
}
