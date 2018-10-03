package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class LineSegment extends Line2D.Double {
    // The edge object that is associated with this line segment.
    public OutlineEdge edge;

    /**
     * Constructs and initializes a <code>Line2D</code> from the
     * specified <code>Point2D</code> objects.
     *
     * @param p1 the start <code>Point2D</code> of this line segment
     * @param p2 the end <code>Point2D</code> of this line segment
     * @since 1.2
     */
    public LineSegment(Point2D p1, Point2D p2) {
        super(p1, p2);
    }

    public boolean intersects(LineSegment ls) {
        // TODO
        if(edge.getDirection().isHorizontal != ls.edge.getDirection().isHorizontal) {
//            return edge.getIntersection(ls.edge);
        }

        return false;
    }



    public Point2d intersectionPoint(LineSegment ls) {
        // TODO
        if(edge.getDirection().isHorizontal != ls.edge.getDirection().isHorizontal) {
            return edge.getIntersection(ls.edge);
        }
    }

    // https://scicomp.stackexchange.com/questions/8895/vertical-and-horizontal-segments-intersection-line-sweep

}

