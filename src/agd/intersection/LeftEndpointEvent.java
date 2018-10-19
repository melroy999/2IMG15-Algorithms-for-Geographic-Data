package agd.intersection;

import agd.data.outlines.Edge;

import java.util.*;

public class LeftEndpointEvent extends AbstractEvent {
    // The outline edge that this event belongs to.
    public final Edge e;

    public LeftEndpointEvent(Edge e) {
        super(e.getDirection() == Edge.Direction.RIGHT || e.getDirection() == Edge.Direction.UP ? e.getOrigin() : e.getTarget(), EventType.LeftEndpoint);
        this.e = e;
    }

    /**
     * Execute the event using the sweep line data.
     *
     * @param events        The current queue of sweep line events.
     * @param status        The status of the sweep line.
     * @param intersections The set of currently found intersections.
     */
    @Override
    public void execute(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, Map<Edge, Set<Edge>> intersections) {
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
