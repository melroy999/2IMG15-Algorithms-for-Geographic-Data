package agd.data.sweepline2;

import agd.data.outline.BufferedOutline;
import agd.data.outline.OutlineEdge;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Find intersections between line segments using the Bentley-Ottmann sweep line algorithm.
 */
public class BentleyOttmann {
    public static Set<Pair<OutlineEdge, OutlineEdge>> solve(BufferedOutline outline) {
        // The list of events that occur within the sweep.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();
        outline.getEdge().forEach(e -> {
            LineSegment segment = new LineSegment(e);
            events.add(new LeftEndpointEvent(segment));
            events.add(new RightEndpointEvent(segment));
        });

        // TODO Add sorting/comparator to abstract events.

        // The status of the sweep.
        SweepStatus status = new SweepStatus();

        // The resulting intersections.
        Set<Pair<OutlineEdge, OutlineEdge>> intersections = new HashSet<>();

        // Continue until the queue is empty.
        while(!events.isEmpty()) {
            AbstractEvent event = events.poll();
            event.resolve(events, status, intersections);
        }

        return intersections;
    }
}
