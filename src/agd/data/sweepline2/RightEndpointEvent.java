package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;

public class RightEndpointEvent extends AbstractEvent {
    // The line segment associated with this point.
    private final LineSegment segment;

    public RightEndpointEvent(LineSegment segment) {
        this.segment = segment;
    }

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

    @Override
    public Point2d getPoint() {
        return segment.right;
    }
}
