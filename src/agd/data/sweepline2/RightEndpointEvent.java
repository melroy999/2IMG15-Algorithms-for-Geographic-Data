package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class RightEndpointEvent extends AbstractEvent {
    // The associated starting point.
    private final LeftEndpointEvent leftEndpointEvent;

    public RightEndpointEvent(OutlineEdge e, LeftEndpointEvent event) {
        super(e.getDirection() == OutlineEdge.Direction.LEFT ? e.getOrigin() : e.getTarget(), EventType.RightEndpoint);
        this.leftEndpointEvent = event;
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

        // Remove the edge from the status.
        entries.remove(leftEndpointEvent);

        // We now have intersections with all edges that remain in the status at key p.y.
        entries.stream().filter(t -> leftEndpointEvent.e.hasIntersection(t.e)).forEach(t -> events.add(new IntersectionEvent(p, leftEndpointEvent.e, t.e)));

        if(entries.isEmpty()) {
            status.remove(key);
        } else {
            status.put(key, entries);
        }
    }
}
