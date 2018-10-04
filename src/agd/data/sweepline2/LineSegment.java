package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;

public class LineSegment implements Comparable<LineSegment> {
    // The edge object that is associated with this line segment.
    public final OutlineEdge edge;

    // The left and right endpoints.
    public final Point2d left, right;

    // The height ranking of the segment, which might be changed for reorderings!
    private double y;

    /**
     * Create a new line segment that represents the outline edge.
     *
     * @param edge The outline edge that this line segment represents.
     */
    public LineSegment(OutlineEdge edge) {
        this.edge = edge;

        // Make sure that the left and right endpoints are oriented correctly.
        if(edge.getDirection() == OutlineEdge.Direction.UP || edge.getDirection() == OutlineEdge.Direction.RIGHT) {
            left = edge.getOrigin();
            right = edge.getTarget();
        } else {
            left = edge.getTarget();
            right = edge.getOrigin();
        }
        y = left.y;
    }

    /**
     * Check whether two line segments have an intersection or not.
     *
     * @param ls The line segment to check intersections with.
     * @return Whether there exists an intersection between the two line segments.
     */
    @SuppressWarnings("Duplicates")
    public boolean intersects(LineSegment ls) {
        if(ls == null) return false;

        if(edge.getDirection().isHorizontal != ls.edge.getDirection().isHorizontal) {

            Point2d o1 = edge.getOrigin();
            Point2d t1 = edge.getNext().getOrigin();

            Point2d o2 = ls.edge.getOrigin();
            Point2d t2 = ls.edge.getNext().getOrigin();

            if(edge.getDirection().isHorizontal) {
                // The x value is a range, y is constant.
                double min_x = Math.min(o1.x, t1.x);
                double max_x = Math.max(o1.x, t1.x);
                double x = o2.x;

                if(x <= min_x || x >= max_x) {
                    return false;
                }

                double min_y = Math.min(o2.y, t2.y);
                double max_y = Math.max(o2.y, t2.y);
                double y = o1.y;

                return !(y <= min_y) && !(y >= max_y);
            } else {
                // The y value is a range, x is constant.
                double min_x = Math.min(o2.x, t2.x);
                double max_x = Math.max(o2.x, t2.x);
                double x = o1.x;

                if(x <= min_x || x >= max_x) {
                    return false;
                }

                double min_y = Math.min(o1.y, t1.y);
                double max_y = Math.max(o1.y, t1.y);
                double y = o2.y;

                return !(y <= min_y) && !(y >= max_y);
            }
        }

        // if the edges are on the same axis, they will never intersect.
        return false;
    }

    /**
     * Get the current y-height of the line segment.
     *
     * @return The current y-height, which initially corresponds to the y-coordinate of the left end point.
     */
    public double getY() {
        return y;
    }

    /**
     * Swap this line segment and another in y-height.
     *
     * @param ls The line segment to swap with.
     */
    public void swap(LineSegment ls) {
        double current = y;
        y = ls.y;
        ls.y = current;
    }

    /**
     * Get the point of intersection of the two line segments.
     *
     * @param ls The line segment to find the intersection with.
     * @return The intersection point between the two line segments.
     */
    public Point2d intersectionPoint(LineSegment ls) {
        return edge.getIntersection(ls.edge);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(LineSegment o) {
        return Double.compare(y, o.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineSegment segment = (LineSegment) o;

        return left.equals(segment.left) && right.equals(segment.right);
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}

