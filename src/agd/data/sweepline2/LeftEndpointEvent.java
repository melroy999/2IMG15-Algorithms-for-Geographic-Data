package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class LeftEndpointEvent extends AbstractEvent {
    // The line segment associated with this point.
    private final LineSegment segment;

    public LeftEndpointEvent(Point2d point, LineSegment segment) {
        super(point);
        this.segment = segment;
    }

    @Override
    public void resolve(PriorityQueue<AbstractEvent> events, SweepStatus status, ArrayList<Pair<OutlineEdge, OutlineEdge>> intersections) {
        status.insert(segment);

        LineSegment above = status.above(segment);
        LineSegment below = status.below(segment);

        // Check whether the line segment intersects with one of its neighbors.
        if(segment.intersectsLine(above)) {

        }
    }
}
