package agd.data.outline;

import agd.data.input.WeightedPoint;
import agd.data.util.EntryRectangle;
import agd.math.Point2d;

import java.util.HashMap;

import static agd.data.outline.OutlineEdge.*;

/**
 * A class representing a rectangle within an outline, supported by the quad tree data structure.
 */
public class OutlineRectangle extends EntryRectangle {
    // A reference to the outline this rectangle is part of.
    private Outline outline;

    /**
     * Create an outline rectangle.
     *
     * @param x The x-coordinate of the left bottom corner point.
     * @param y The y-coordinate of the left bottom corner point.
     * @param size The size of the square.
     * @param owner The weighted point for which this rectangle is a placement.
     */
    public OutlineRectangle(int x, int y, int size, WeightedPoint owner) {
        super(x, y, size, size, owner);
    }

    /**
     * Get the outline associated with the rectangle.
     *
     * @return The outline if it exists, null otherwise.
     */
    public Outline getOutline() {
        return outline;
    }

    /**
     * Set the outline of this rectangle.
     *
     * @param outline The outline the rectangle is part of.
     */
    public void setOutline(Outline outline) {
        this.outline = outline;
    }

    /**
     * Create a linked list of outline edges in clockwise order.
     *
     * @param direction The side of the rectangle we want to use as our handle.
     * @return A collection of outline edges connected through links.
     */
    public OutlineEdge createOutline(Direction direction) {
        // Create all the required edges.
        OutlineEdge up = new OutlineEdge(new Point2d(x, y), Direction.UP);
        OutlineEdge right = new OutlineEdge(new Point2d(x, y + this.height), Direction.RIGHT);
        OutlineEdge down = new OutlineEdge(new Point2d(x + this.height, y + this.height), Direction.DOWN);
        OutlineEdge left = new OutlineEdge(new Point2d(x + this.height, y), Direction.LEFT);

        // Set the links.
        up.setNext(right);
        right.setNext(down);
        down.setNext(left);
        left.setNext(up);

        //noinspection Duplicates
        switch (direction) {
            case UP: return up;
            case DOWN: return down;
            case LEFT: return left;
            default: return right;
        }
    }
}
