package agd.data.outlines;

import agd.intersection.IntersectionSweep;
import agd.math.Point2d;

import java.util.*;

import static agd.data.outlines.Edge.*;
import static agd.data.outlines.Edge.Direction.*;

/**
 * Create a buffered outline of an existing outline.
 */
public class BufferedOutline extends AbstractOutline {

    private final int maxId;

    /**
     * A constructor that should be used when basing an outline on another outline.
     *
     * @param outline The outline to use as the original outline.
     * @param w The width of the buffering zone.
     */
    public BufferedOutline(SimpleOutline outline, double w) {
        super(outline.getRectangles());
        setEdge(createOutline(outline, w));
        maxId = getEdge().getPrevious().getId();
    }

    public BufferedOutline(ComplexOutline outline, double w) {
        super(outline.getRectangles());
        setEdge(createOutline(outline, w));
        maxId = getEdge().getPrevious().getId();
        sanitizeImproved();
        validate();
    }

    static int itot = 0;

    private void validate() {
        int i = 0;

        // Check if we have any intersections, the brute force way.
        for(Edge e1 : this) {
            for(Edge e2 : e1.getNext()) {
                if(e1 == e2) break;

                if(e1.doIntersect(e2)) {
                    i++;
                    itot++;
                }
            }
        }

        if(i != 0) {
            System.out.println("Found " + i + " intersections in our output, with a total of " + itot + " intersections in this run.");
        }
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
                // Wait a second, is the direction of the previous edge still correct if we take this as the next?
                Direction dir = Direction.getDirection(last.getOrigin(), p);
                if(dir != last.getDirection()) {
                    // We have to remake the previous edge.
                    Edge prev = new Edge(last.getOrigin(), dir);
                    prev.setPrevious(last.getPrevious());
                    last = prev;
                }

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

        // Make sure that we have no two consecutive edges in the same direction.
        Edge current = first;
        while(current.getNext() != first) {
            if(current.getDirection() == current.getNext().getDirection()) {
                // Merge.
                current.setNext(current.getNext().getNext());
            } else {
                current = current.getNext();
            }
        }

        // Return the first edge that we have set.
        return first;
    }

    private void resolveIntersection(Edge target, Edge conflict, TreeMap<Integer, Integer> castMap) {
        if(target.getDirection() == conflict.getDirection()) {

            // We should extend the current edge.
            target.setNext(conflict.getNext());

        } else {
            // We have a normal intersection with an intersection point.
            Point2d i = target.getIntersection(conflict);

            // Create a new edge for the next edge.
            Edge newEdge = new Edge(i, conflict.getDirection());

            // What was the original edge we split up? Find the edge recursively.
            castMap.put(newEdge.getId(), conflict.getId());

            target.setNext(newEdge);
            newEdge.setNext(conflict.getNext());
        }
    }

    private void sanitizeImproved() {

        // Which intersections do we have?
        TreeMap<Integer, Set<Edge>> intersectionMapping2 = IntersectionSweep.findIntersections(getEdge());
        TreeMap<Integer, Set<Edge>> intersectionMapping = IntersectionSweep.findIntersectionsBF(getEdge());

        if(intersectionMapping.size() != intersectionMapping2.size()) {
            System.out.println("Imbalance " + intersectionMapping.size() + ", " + intersectionMapping2.size());
        }

        // A mapping in which we track all the ids that have changed.
        TreeMap<Integer, Integer> castMap = new TreeMap<>();

        for(Edge target : this) {
            // Get the true id of the target.
            int targetId = castMap.getOrDefault(target.getId(), target.getId());

            // Does this edge have intersections in the intersection list?
            // If it does not, continue. Otherwise, resolve the intersection and continue.
            if(intersectionMapping.containsKey(targetId)) {
                Set<Edge> intersections = intersectionMapping.get(targetId);

                // Find the intersection that is applicable first by comparing the ids of the edges.
                for(Edge conflict : intersections) {
                    if(conflict.getId() > targetId) {
                        // We have found the edge that follows this edge immediately.
                        resolveIntersection(target, conflict, castMap);

                        // Which kind of intersection did we have? If it was an intersection, break; otherwise continue.
                        if(target.getDirection() != conflict.getDirection()) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void sanitize() {

        // Sanitize the drawn buffered outline using a bottom-up sweep.
        Edge next;
        for(Edge e : this) {
            next = e.getNext();

            while(next != e) {
                if(e.getDirection() == next.getDirection()) {
                    if(e.doIntersect(next)) {

                        // Which edge should we visit next?
                        Edge next2 = next.getNext();

                        // We should extend the current edge.
                        e.setNext(next.getNext());

                        // Set the next pointer.
                        next = next2;

                    } else if(next.doIntersect(e)) {

                        // Which edge should we visit next?
                        Edge next2 = next.getNext();

                        // We should extend the current edge.
                        e.setNext(next.getNext());

                        // Set the next pointer.
                        next = next2;
                    } else {
                        // Set a new next.
                        next = next.getNext();
                    }
                } else if(e.doIntersect(next)) {

                    // We have a normal intersection with an intersection point.
                    Point2d i = e.getIntersection(next);

                    // Create a new edge for the next edge.
                    Edge newEdge = new Edge(i, next.getDirection());

                    e.setNext(newEdge);
                    newEdge.setNext(next.getNext());

                    // Stay on this edge.
                } else {
                    // Set a new next.
                    next = next.getNext();
                }
            }
        }
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
