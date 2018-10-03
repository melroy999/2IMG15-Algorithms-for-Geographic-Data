package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class RightEndpointEvent extends AbstractEvent {
    // The line segment associated with this point.
    private final LineSegment segment;

    public RightEndpointEvent(Point2d point, LineSegment segment) {
        super(point);
        this.segment = segment;
    }

    @Override
    public void resolve(PriorityQueue<AbstractEvent> events, SweepStatus status, ArrayList<Pair<OutlineEdge, OutlineEdge>> intersections) {

    }
}
