package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;

public class LeftEndpointEvent extends AbstractEvent {
    // The line segment associated with this point.
    private final LineSegment segment;

    public LeftEndpointEvent(LineSegment segment) {
        super(EventType.LE);
        this.segment = segment;
    }

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

    @Override
    public Point2d getPoint() {
        return segment.left;
    }
}
