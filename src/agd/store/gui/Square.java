package agd.store.gui;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Square implements IDrawable {
    // The shape that contains a renderable rectangle.
    private final Rectangle2D shape;
    private final Color color;

    /**
     * Create a square graphic shape.
     *
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param size The length of the sides of the rectangle.
     */
    public Square(double x, double y, double size) {
        this(x, y, size, Color.black);
    }

    /**
     * Create a square graphic shape.
     *
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param size The length of the sides of the rectangle.
     * @param color The color of the rectangle.
     */
    public Square(double x, double y, double size, Color color) {
        this.shape = new Rectangle2D.Double(x, y, size, size);
        this.color = color;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.draw(shape);
    }
}
