package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class VerticalLineEvent extends AbstractEvent {
    // The other endpoint of the line.
    private final Point2d upper;

    // The outline edge.
    public final OutlineEdge e;

    public VerticalLineEvent(OutlineEdge e) {
        super(e.getOrigin().y <= e.getTarget().y ? e.getOrigin() : e.getTarget(), EventType.VerticalLine);
        upper = e.getOrigin().y <= e.getTarget().y ? e.getTarget() : e.getOrigin();
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
    public void resolveIntersection(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, List<Pair<OutlineEdge, OutlineEdge>> intersections) {
        // Do a range search on the tree map.
        Map<DoubleWrapper, Set<LeftEndpointEvent>> range = status.subMap(new DoubleWrapper(p.y), new DoubleWrapper(upper.y));

        // We have potential intersections with all elements in the tree.
        List<LeftEndpointEvent> targets = range.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
        targets.stream().filter(t -> e.hasIntersection(t.e)).forEach(t -> events.add(new IntersectionEvent(p, e, t.e)));
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
        throw new IllegalArgumentException("This version should never occur within an overlap sweep.");
    }
}
