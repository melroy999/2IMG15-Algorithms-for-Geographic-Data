package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

/**
 * Representation of the events in the sweep line algorithm.
 */
public abstract class AbstractEvent implements Comparable<AbstractEvent> {
    // The point which determines the ordering of the events.
    protected final Point2d p;

    // The type of the event.
    private final EventType type;

    /**
     * Create a new abstract event, with a type and a certain point as a key.
     *
     * @param p The point to use as a key for the event.
     * @param type The type of the event.
     */
    public AbstractEvent(Point2d p, EventType type) {
        this.p = p;
        this.type = type;
    }

    /**
     * Resolve the event using the sweep line data.
     *
     * @param events The current queue of sweep line events.
     * @param status The status of the sweep line.
     * @param intersections The set of currently found intersections.
     */
    public abstract void resolve(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, Set<Pair<OutlineEdge,OutlineEdge>> intersections);

    @Override
    public int compareTo(AbstractEvent o) {
        Point2d a = p;
        Point2d b = o.p;

        if(Math.abs(a.x - b.x) < 1e-4) {
            if(Math.abs(a.y - b.y) < 1e-4) {
                // Suppose that x and y are both equal.
                // In such a case, we want to base our order on the event type.
                // The order is as follows: LE < VL < RE < I.
                return type.compareTo(o.type);
            } else {
                return Double.compare(a.y, b.y);
            }
        } else {
            return Double.compare(a.x, b.x);
        }
    }

    /**
     * The different types of events that may occur.
     */
    public enum EventType {
        RightEndpoint, VerticalLine, Intersection, LeftEndpoint;
    }
}
