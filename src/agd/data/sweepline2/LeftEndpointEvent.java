package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.*;

public class LeftEndpointEvent extends AbstractEvent {
    // The outline edge that this event belongs to.
    public final OutlineEdge e;

    public LeftEndpointEvent(OutlineEdge e) {
        super(e.getDirection() == OutlineEdge.Direction.RIGHT ? e.getOrigin() : e.getTarget(), EventType.LeftEndpoint);
        this.e = e;
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
        // Insert the line segment into the status.
        DoubleWrapper key = new DoubleWrapper(p.y);
        Set<LeftEndpointEvent> entries = status.getOrDefault(key, new TreeSet<>());
        entries.add(this);
        status.put(key, entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeftEndpointEvent that = (LeftEndpointEvent) o;

        return e.equals(that.e);
    }

    @Override
    public int hashCode() {
        return e.hashCode();
    }
}
