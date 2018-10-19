package agd.data.outlines;

import agd.data.input.WeightedPoint;
import agd.data.util.EntryRectangle;
import agd.math.Point2d;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static agd.data.outlines.Edge.*;

/**
 * A class representing a rectangle within an outline, supported by the quad tree data structure.
 */
public class OutlineRectangle extends EntryRectangle {
    // A reference to the outline this rectangle is part of.
    private AbstractOutline outline;

    // Whether to include the borders in our intersection check.
    private final boolean includeBorders;

    /**
     * Create an outline rectangle.
     *
     * @param x The x-coordinate of the left bottom corner point.
     * @param y The y-coordinate of the left bottom corner point.
     * @param size The size of the square.
     * @param owner The weighted point for which this rectangle is a placement.
     */
    public OutlineRectangle(int x, int y, int size, WeightedPoint owner, boolean includeBorders) {
        super(x, y, size, size, owner);
        this.includeBorders = includeBorders;
    }

    /**
     * Create an outline rectangle.
     *
     * @param x The x-coordinate of the left bottom corner point.
     * @param y The y-coordinate of the left bottom corner point.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param owner The weighted point for which this rectangle is a placement.
     */
    public OutlineRectangle(int x, int y, int width, int height, WeightedPoint owner, boolean includeBorders) {
        super(x, y, width, height, owner);
        this.includeBorders = includeBorders;
    }

    /**
     * Create an outline rectangle.
     *
     * @param x The x-coordinate of the left bottom corner point.
     * @param y The y-coordinate of the left bottom corner point.
     * @param size The size of the square.
     * @param owner The weighted point for which this rectangle is a placement.
     */
    public OutlineRectangle(int x, int y, int size, WeightedPoint owner) {
        this(x, y, size, owner, true);
    }

    public OutlineRectangle(OutlineRectangle r, boolean includeBorders) {
        super(r.x, r.y, r.width, r.height, r.owner);
        this.includeBorders = includeBorders;
    }

    /**
     * Get the outline associated with the rectangle.
     *
     * @return The outline if it exists, null otherwise.
     */
    public AbstractOutline getOutline() {
        return outline;
    }

    /**
     * Set the outline of this rectangle.
     *
     * @param outline The outline the rectangle is part of.
     */
    public void setOutline(AbstractOutline outline) {
        this.outline = outline;
    }

    /**
     * Create a linked list of outline edges in clockwise order.
     *
     * @param direction The side of the rectangle we want to use as our handle.
     * @return A collection of outline edges connected through links.
     */
    public Edge createOutline(Direction direction) {
        // Create all the required edges.
        Edge up = new Edge(new Point2d(x, y), Direction.UP);
        Edge right = new Edge(new Point2d(x, y + this.height), Direction.RIGHT);
        Edge down = new Edge(new Point2d(x + this.width, y + this.height), Direction.DOWN);
        Edge left = new Edge(new Point2d(x + this.width, y), Direction.LEFT);

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

    /**
     * Create a linked list of outline edges in clockwise order, and return them as a map.
     *
     * @return A collection of outline edges connected through links.
     */
    public Map<Direction, Edge> createOutlineMap() {
        // Create all the required edges.
        Edge up = new Edge(new Point2d(x, y), Direction.UP);
        Edge right = new Edge(new Point2d(x, y + this.height), Direction.RIGHT);
        Edge down = new Edge(new Point2d(x + this.height, y + this.height), Direction.DOWN);
        Edge left = new Edge(new Point2d(x + this.height, y), Direction.LEFT);

        // Set the links.
        up.setNext(right);
        right.setNext(down);
        down.setNext(left);
        left.setNext(up);

        //noinspection Duplicates
        Map<Direction, Edge> result = new HashMap<>();
        result.put(Direction.LEFT, left);
        result.put(Direction.UP, up);
        result.put(Direction.RIGHT, right);
        result.put(Direction.DOWN, down);
        return result;
    }
    /**
     * Determines whether or not this <code>Rectangle</code> and the specified
     * <code>Rectangle</code> intersect. Two rectangles intersect if
     * their intersection is nonempty.
     *
     * @param r the specified <code>Rectangle</code>
     * @return <code>true</code> if the specified <code>Rectangle</code>
     * and this <code>Rectangle</code> intersect;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean intersects(Rectangle r) {
        int tw = this.width;
        int th = this.height;
        int rw = r.width;
        int rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.x;
        int ty = this.y;
        int rx = r.x;
        int ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;

        if(includeBorders || (r instanceof OutlineRectangle && ((OutlineRectangle) r).includeBorders)) {
            //      overflow || intersect
            return ((rw <= rx || rw >= tx) &&
                    (rh <= ry || rh >= ty) &&
                    (tw <= tx || tw >= rx) &&
                    (th <= ty || th >= ry));
        } else {
            //      overflow || intersect
            return ((rw < rx || rw > tx) &&
                    (rh < ry || rh > ty) &&
                    (tw < tx || tw > rx) &&
                    (th < ty || th > ry));
        }
    }
}
