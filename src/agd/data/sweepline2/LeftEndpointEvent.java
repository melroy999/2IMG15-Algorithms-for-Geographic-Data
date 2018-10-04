package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;

/**
 * Representation of the left endpoint events in the sweep line algorithm.
 */
public class LeftEndpointEvent extends AbstractEvent {
    // The line segment associated with this point.
    private final LineSegment segment;

    /**
     * Create a left endpoint even for the given line segment.
     *
     * @param segment The line segment the event should be based upon.
     */
    public LeftEndpointEvent(LineSegment segment) {
        super(EventType.LE);
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
        // Update the status.
        status.insert(segment);

        // Find the closest two edges.
        LineSegment above = status.above(segment);
        LineSegment below = status.below(segment);

        // Check whether the line segment intersects with one of its neighbors.
        if(segment.intersects(above)) events.add(new IntersectionEvent(above, segment));
        if(segment.intersects(below)) events.add(new IntersectionEvent(segment, below));
    }

    /**
     * Get the point the event ordering should be based on.
     *
     * @return The left side of the line segment.
     */
    @Override
    public Point2d getPoint() {
        return segment.left;
    }
}
