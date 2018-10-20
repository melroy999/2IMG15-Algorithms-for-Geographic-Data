package agd.intersection;

import agd.data.outlines.Edge;

import java.util.*;

public class RightEndpointEvent extends AbstractEvent {
    // The associated starting point.
    private final LeftEndpointEvent leftEndpointEvent;

    public RightEndpointEvent(Edge e, LeftEndpointEvent event) {
        super(e.getDirection() == Edge.Direction.LEFT ? e.getOrigin() : e.getTarget(), EventType.RightEndpoint);
        this.leftEndpointEvent = event;
    }

    /**
     * Execute the event using the sweep line data.
     *
     * @param events        The current queue of sweep line events.
     * @param status        The status of the sweep line.
     * @param intersections The set of currently found intersections.
     */
    @Override
    public void execute(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, Map<Integer, Set<Edge>> intersections) {
        // We simply have to remove the edge from the status.
        DoubleWrapper key = new DoubleWrapper(p.y);
        Set<LeftEndpointEvent> entries = status.getOrDefault(key, new TreeSet<>());

        // Remove the edge from the status.
        entries.remove(leftEndpointEvent);
    }
}
