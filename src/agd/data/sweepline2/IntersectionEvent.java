package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;

/**
 * Representation of the intersection events in the sweep line algorithm.
 */
public class IntersectionEvent extends AbstractEvent {
    // The two line segments that intersect.
    private final LineSegment above, below;

    // The intersection point.
    private final Point2d i;

    /**
     * Create an intersection event for the two line segments that intersect.
     *
     * @param above The line segment that is considered the 'above' segment by the intersection algorithm.
     * @param below The line segment that is considered the 'below' segment by the intersection algorithm.
     */
    public IntersectionEvent(LineSegment above, LineSegment below) {
        super(EventType.I);
        this.above = above;
        this.below = below;
        i = above.intersectionPoint(below);
    }

    /**
     * Resolve the event using the sweep line data.
     *
     * @param events The current queue of sweep line events.
     * @param status The status of the sweep line.
     * @param intersections The set of currently found intersections.
     */
    @Override
    public void resolve(PriorityQueue<AbstractEvent> events, SweepStatus status, Set<Pair<OutlineEdge, OutlineEdge>> intersections) {
        // Add the intersection to the list of intersections.
        intersections.add(new Pair<>(above.edge, below.edge));

        // Note that we should now swap the two lines, as one should now be above the other.
        status.remove(above);
        status.remove(below);

        // Swap the two positions.
        above.swap(below);

        // Add them again to the status.
        status.insert(above);
        status.insert(below);

        // Note that the original above is now below. Above is segE1, below is SegE2.
        LineSegment s1 = status.above(below);
        LineSegment s2 = status.below(above);

        // Check whether the line segment intersects with one of its neighbors.
        if(s1 != null && s1.intersects(below)) events.add(new IntersectionEvent(s1, below));
        if(s2 != null && s2.intersects(above)) events.add(new IntersectionEvent(above, s2));
    }

    /**
     * Get the point the event ordering should be based on.
     *
     * @return The intersection point.
     */
    @Override
    public Point2d getPoint() {
        return i;
    }


}
