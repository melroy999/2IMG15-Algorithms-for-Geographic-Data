package agd.store.gui;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Square implements IDrawable {
    // The shape that contains a renderable rectangle.
    private final Rectangle2D shape;

    /**
     * Create a square graphic shape.
     *
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param size The length of the sides of the rectangle.
     */
    public Square(double x, double y, double size) {
        this.shape = new Rectangle2D.Double(x, y, size, size);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.black);
        g.draw(shape);
    }
}
