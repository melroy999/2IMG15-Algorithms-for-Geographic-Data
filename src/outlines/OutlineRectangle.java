package outlines;

import agd.data.input.WeightedPoint;
import agd.data.util.EntryRectangle;

import java.awt.*;

public class OutlineRectangle extends EntryRectangle implements Comparable<OutlineRectangle> {
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

    public Outline getOutline() {
        return outline;
    }

    public void setOutline(Outline outline) {
        this.outline = outline;
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

        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

    @Override
    public int compareTo(OutlineRectangle o) {
        int x = Integer.compare(this.x, o.x);

        if(x == 0) {
            return Integer.compare(this.y, o.y);
        } else {
            return x;
        }
    }
}
