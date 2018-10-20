package agd.overlap;

import agd.data.outlines.Edge;
import agd.intersection.DoubleWrapper;
import agd.math.Point2d;

import java.util.*;

public class StartPointEvent extends AbstractEvent {
    // The outline edge that this event belongs to.
    public final Edge e;

    /**
     * Create a new abstract event, with a type and a certain point as a key.
     *
     * @param e    The edge associated with the event.
     * @param p    The point to use as a key for the event.
     */
    public StartPointEvent(Edge e, Point2d p) {
        super(p, EventType.StartPoint);
        this.e = e;
    }

    /**
     * Execute the event using the sweep line data.
     *
     * @param events   The current queue of sweep line events.
     * @param status   The status of the sweep line.
     * @param overlaps The set of currently found overlaps.
     */
    @Override
    public void execute(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<StartPointEvent>> status, Map<Integer, Set<Edge>> overlaps) {
        // Insert the line segment into the status.
        DoubleWrapper key = new DoubleWrapper(p.y);
        Set<StartPointEvent> entries = status.getOrDefault(key, new TreeSet<>());
        entries.add(this);
        status.putIfAbsent(key, entries);
    }
}
