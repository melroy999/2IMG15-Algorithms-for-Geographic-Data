package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;

public class LineSegment {
    // The edge object that is associated with this line segment.
    public final OutlineEdge edge;

    // The left and right endpoints.
    public final Point2d left, right;

    /**
     * Create a new line segment that represents the outline edge.
     *
     * @param edge The outline edge that this line segment represents.
     */
    public LineSegment(OutlineEdge edge) {
        this.edge = edge;

        if(edge.getDirection() == OutlineEdge.Direction.UP || edge.getDirection() == OutlineEdge.Direction.RIGHT) {
            left = edge.getOrigin();
            right = edge.getTarget();
        } else {
            left = edge.getTarget();
            right = edge.getOrigin();
        }
    }

    /**
     * Check whether two line segments have an intersection or not.
     *
     * @param ls The line segment to check intersections with.
     * @return Whether there exists an intersection between the two line segments.
     */
    @SuppressWarnings("Duplicates")
    public boolean intersects(LineSegment ls) {
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
     * Get the point of intersection of the two line segments.
     *
     * @param ls The line segment to find the intersection with.
     * @return The intersection point between the two line segments.
     */
    public Point2d intersectionPoint(LineSegment ls) {
        return edge.getIntersection(ls.edge);
    }
}

