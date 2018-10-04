package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;

/**
 * Representation of the right endpoint events in the sweep line algorithm.
 */
public class RightEndpointEvent extends AbstractEvent {
    // The line segment associated with this point.
    private final LineSegment segment;

    /**
     * Create a right endpoint even for the given line segment.
     *
     * @param segment The line segment the event should be based upon.
     */
    public RightEndpointEvent(LineSegment segment) {
        super(EventType.RE);
        this.segment = segment;
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
        // Find the closest two edges.
        LineSegment above = status.above(segment);
        LineSegment below = status.below(segment);

        // Delete the line segment.
        status.remove(segment);

        // Check whether the line segment intersects with one of its neighbors.
        if(above != null && above.intersects(below)) events.add(new IntersectionEvent(above, below));
    }

    /**
     * Get the point the event ordering should be based on.
     *
     * @return The right endpoint of the line segment.
     */
    @Override
    public Point2d getPoint() {
        return segment.right;
    }
}
