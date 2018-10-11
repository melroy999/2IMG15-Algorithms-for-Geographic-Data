package agd.data.outlines;

import agd.math.Point2d;

import java.util.EnumSet;
import java.util.List;

import static agd.data.outlines.Edge.*;
import static agd.data.outlines.Edge.Direction.*;

/**
 * Create a buffered outline of an existing outline.
 */
public class BufferedOutline extends AbstractOutline {
    /**
     * A constructor that should be used when basing an outline on another outline.
     *
     * @param outline The outline to use as the original outline.
     * @param w The width of the buffering zone.
     */
    public BufferedOutline(SimpleOutline outline, double w) {
        super(outline.getRectangles());
        setEdge(createOutline(outline, w));
    }

    /**
     * Generate the buffered outline associated with the given outline.
     *
     * @param outline The outline to buffer.
     * @param w The width of the buffering zone.
     * @return The edge to use as the access point.
     */
    private Edge createOutline(AbstractOutline outline, double w) {
        // Keep the last encountered edge such that we can set next and previous references.
        Edge last = null;
        Edge first = null;

        for(Edge e : outline) {
            // Translate each edge to the correct position.
            Point2d p = e.getOrigin().add(e.getDirection().bufferVector().scale(w));

            // Check whether we have made a left turn.
            if(e.getPrevious().getDirection().isLeftTurn(e.getDirection())) {
                p = p.add(e.getDirection().leftTurnVector().scale(2 * w));
            }

            // Create a new edge and set the appropriate pointers.
            Edge bufferedEdge = new Edge(p, e.getDirection());

            if(last != null) {
                last.setNext(bufferedEdge);
            }
            last = bufferedEdge;

            if(first == null) {
                first = bufferedEdge;
            }
        }

        // Finalize the cycle.
        assert last != null;
        last.setNext(first);

        // Return the first edge that we have set.
        return first;
    }

    /**
     * Project the point p onto the outline and find the position that has the smallest euclidean distance.
     *
     * @param p The point to find the closest position on the outline to.
     * @return A point on the line segments of the outline such that the distance is minimal.
     */
    public Point2d projectAndSelect(Point2d p) {
        // For each of the edges, find the best position and the associated score.
        double min = Double.MAX_VALUE;
        Point2d position = null;

        for(Edge e : this) {
            Point2d projection = e.project(p);
            double distance = projection.distance2(p);
            if(distance < min) {
                position = projection;
                min = distance;
            }
        }

        return position;
    }

    /**
     * Project the point p onto the outline and find the position that has the smallest euclidean distance.
     * Points that are aimed towards the center of mass are marked as invalid.
     *
     * @param p The point to find the closest position on the outline to.
     * @param c The center of mass of all the points.
     * @return A point on the line segments of the outline such that the distance is minimal.
     */
    public Point2d projectAndSelect(Point2d p, Point2d c) {
        // For each of the edges, find the best position and the associated score.
        double min = Double.MAX_VALUE;
        Point2d position = null;

        // Get the relative position.
        Quadrant q = Quadrant.getQuadrant(p, c);

        for(Edge e : this) {
            if(q.isValid(e.getDirection())) {
                Point2d projection = e.project(p);
                double distance = projection.distance2(p);
                if(distance < min) {
                    position = projection;
                    min = distance;
                }
            }
        }

        return position;
    }

    /**
     * The different quadrants of the plane.
     */
    public enum Quadrant {
        NW(EnumSet.of(UP, RIGHT)),
        SW(EnumSet.of(UP, LEFT)),
        SE(EnumSet.of(DOWN, LEFT)),
        NE(EnumSet.of(DOWN, RIGHT)),
        ON(EnumSet.of(UP, RIGHT, LEFT, DOWN));

        private final EnumSet<Direction> valid;

        Quadrant(EnumSet<Direction> valid) {
            this.valid = valid;
        }

        public static Quadrant getQuadrant(Point2d p, Point2d c) {
            if(p.distance2(c) < 1e-4) {
                return ON;
            } else {
                if(p.x <= c.x) {
                    // West.
                    if(p.y <= c.y) {
                        // South.
                        return SW;
                    } else {
                        return NW;
                    }
                } else {
                    // East.
                    if(p.y <= c.y) {
                        // South.
                        return SE;
                    } else {
                        return NE;
                    }
                }
            }
        }

        public boolean isValid(Direction d) {
            return valid.contains(d);
        }
    }
}