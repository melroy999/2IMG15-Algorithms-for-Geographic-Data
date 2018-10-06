package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

public class IntersectionEvent extends AbstractEvent {
    // The two outline edges that have an intersection.
    private final OutlineEdge e1, e2;

    /**
     * Create a new abstract event, with a type and a certain point as a key.
     *
     * @param p    The point to use as a key for the event.
     */
    public IntersectionEvent(Point2d p, OutlineEdge e1, OutlineEdge e2) {
        super(p, EventType.Intersection);
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Resolve the event using the sweep line data.
     *
     * @param events        The current queue of sweep line events.
     * @param status        The status of the sweep line.
     * @param intersections The set of currently found intersections.
     */
    @Override
    public void resolve(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, Set<Pair<OutlineEdge, OutlineEdge>> intersections) {
        intersections.add(new Pair<>(e1, e2));
    }
}
