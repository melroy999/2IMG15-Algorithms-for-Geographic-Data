package agd.intersection;

import agd.data.outlines.Edge;

import java.util.*;

/**
 * A vertical sweep line algorithm that detects intersections between lines in a rectilinear plane.
 */
public class IntersectionSweep {

    public static TreeMap<Integer, Set<Edge>> findIntersections(Edge... edges) {
        TreeMap<Integer, Set<Edge>> intersections = new TreeMap<>();
        findIntersections(intersections, edges);
        return intersections;
    }

    private static void findIntersections(Map<Integer, Set<Edge>> intersections, Edge... edges) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status = new TreeMap<>();

        // Populate the queue with events.
        for(Edge edge : edges) {
            edge.forEach(e -> createEvent(events, e));
        }

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            AbstractEvent currentEvent = events.poll();
            currentEvent.execute(events, status, intersections);
        }
    }

    private static void createEvent(PriorityQueue<AbstractEvent> events, Edge e) {
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
