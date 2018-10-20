package agd.overlap;

import agd.data.outlines.Edge;
import agd.intersection.DoubleWrapper;
import agd.math.Point2d;

import java.util.*;

public class EndPointEvent extends AbstractEvent {
    // The associated starting point.
    private final StartPointEvent startPoint;

    /**
     * Create a new abstract event, with a type and a certain point as a key.
     *
     * @param p    The point to use as a key for the event.
     * @param startPoint The starting point of the edge.
     */
    public EndPointEvent(Point2d p, StartPointEvent startPoint) {
        super(p, EventType.EndPoint);
        this.startPoint = startPoint;
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
        // Get the associated key.
        DoubleWrapper key = new DoubleWrapper(p.y);
        Set<StartPointEvent> entries = status.getOrDefault(key, new TreeSet<>());

        // Remove the edge from the status.
        entries.remove(startPoint);

        // All edges that remain in the status are potential overlaps with this edge.
        entries.stream().filter(t -> startPoint.e.doIntersect(t.e) || t.e.doIntersect(startPoint.e)).forEach(t ->
                {
                    Set<Edge> set = overlaps.getOrDefault(startPoint.e.getId(), new TreeSet<>());
                    set.add(t.e);
                    overlaps.putIfAbsent(startPoint.e.getId(), set);

                    Set<Edge> set2 = overlaps.getOrDefault(t.e.getId(), new TreeSet<>());
                    set2.add(startPoint.e);
                    overlaps.putIfAbsent(t.e.getId(), set2);
                }
        );
    }
}
