package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.*;

public class RightEndpointEvent extends AbstractEvent {
    // The associated starting point.
    private final LeftEndpointEvent leftEndpointEvent;

    public RightEndpointEvent(OutlineEdge e, LeftEndpointEvent event) {
        super(e.getDirection() == OutlineEdge.Direction.LEFT || e.getDirection() == OutlineEdge.Direction.DOWN ? e.getOrigin() : e.getTarget(), EventType.RightEndpoint);
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
    public void resolveIntersection(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, List<Pair<OutlineEdge, OutlineEdge>> intersections) {
        // We simply have to remove the edge from the status.
        DoubleWrapper key = new DoubleWrapper(p.y);
        Set<LeftEndpointEvent> entries = status.getOrDefault(key, new TreeSet<>());

        // Remove the edge from the status.
        entries.remove(leftEndpointEvent);
    }

    /**
     * Resolve the event using the sweep line data.
     *
     * @param events        The current queue of sweep line events.
     * @param status        The status of the sweep line.
     * @param intersections The set of currently found intersections.
     * @param h             The direction of the sweep (true if p.y should be used, false if p.x).
     */
    @Override
    public void resolveOverlap(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, List<Pair<OutlineEdge, OutlineEdge>> intersections, boolean h) {
        // Differently from the intersection variant, we check here whether the line segments overlap.
        DoubleWrapper key = new DoubleWrapper(h ? p.y : p.x);
        Set<LeftEndpointEvent> entries = status.getOrDefault(key, new TreeSet<>());

        // Remove the edge from the status.
        entries.remove(leftEndpointEvent);

        // We now have intersections with all edges that remain in the status at the key.
        entries.stream().filter(t -> leftEndpointEvent.e.hasOverlap(t.e)).forEach(t -> events.add(new IntersectionEvent(p, leftEndpointEvent.e, t.e)));

        if(entries.isEmpty()) {
            status.remove(key);
        } else {
            status.put(key, entries);
        }
    }
}
