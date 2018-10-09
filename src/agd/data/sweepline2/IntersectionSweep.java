package agd.data.sweepline2;

import agd.data.outline.BufferedOutline;
import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.*;

/**
 * A vertical sweep line algorithm that detects intersections between lines in a rectilinear plane.
 */
public class IntersectionSweep {


    public static List<Pair<OutlineEdge, OutlineEdge>> findIntersections(OutlineEdge edge) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status = new TreeMap<>();

        // The set of resulting intersections.
        List<Pair<OutlineEdge, OutlineEdge>> intersections = new ArrayList<>();

        // Populate the queue with events.
        edge.forEach(e -> createEvent(events, e));

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            events.poll().resolve(events, status, intersections);
        }

        return intersections;
    }

    private static void createEvent(PriorityQueue<AbstractEvent> events, OutlineEdge e) {
        if(e.getDirection().isHorizontal) {
            // Create left and right endpoint events.
            LeftEndpointEvent event = new LeftEndpointEvent(e);
            events.add(event);
            events.add(new RightEndpointEvent(e, event));
        } else {
            // Create a vertical line event.
            events.add(new VerticalLineEvent(e));
        }
    }
}
