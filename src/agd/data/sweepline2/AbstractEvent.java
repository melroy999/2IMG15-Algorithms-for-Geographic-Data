package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.PriorityQueue;

public abstract class AbstractEvent {
    // The points associated with the event.
    private final Point2d point;

    public AbstractEvent(Point2d point) {
        this.point = point;
    }

    public abstract void resolve(PriorityQueue<AbstractEvent> events, SweepStatus status, ArrayList<Pair<OutlineEdge, OutlineEdge>> intersections);
}
