package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import javafx.util.Pair;

import java.util.*;

/**
 * A vertical sweep line algorithm that detects intersections between lines in a rectilinear plane.
 */
public class IntersectionSweep {


    public static List<Pair<OutlineEdge, OutlineEdge>> findIntersections(OutlineEdge edge) {

        // The set of resulting intersections.
        List<Pair<OutlineEdge, OutlineEdge>> intersections = new ArrayList<>();
        findIntersections(edge, intersections);

        return intersections;
    }

    public static List<Pair<OutlineEdge, OutlineEdge>> findHorizontalOverlaps(OutlineEdge edge) {

        // The set of resulting intersections.
        List<Pair<OutlineEdge, OutlineEdge>> intersections = new ArrayList<>();
        findHorizontalOverlaps(edge, intersections);

        return intersections;
    }

    public static List<Pair<OutlineEdge, OutlineEdge>> findVerticalOverlaps(OutlineEdge edge) {

        // The set of resulting intersections.
        List<Pair<OutlineEdge, OutlineEdge>> intersections = new ArrayList<>();
        findVerticalOverlaps(edge, intersections);

        return intersections;
    }

    private static void findIntersections(OutlineEdge edge, List<Pair<OutlineEdge, OutlineEdge>> intersections) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status = new TreeMap<>();

        // Populate the queue with events.
        edge.forEach(e -> createEvent(events, e));

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            AbstractEvent currentEvent = events.poll();
            currentEvent.resolveIntersection(events, status, intersections);
        }
    }

    private static void findHorizontalOverlaps(OutlineEdge edge, List<Pair<OutlineEdge, OutlineEdge>> intersections) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status = new TreeMap<>();

        // Populate the queue with events.
        edge.toList().stream().filter(e -> e.getDirection().isHorizontal).forEach(e -> createOverlapEvent(events, e));

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            AbstractEvent currentEvent = events.poll();
            currentEvent.resolveOverlap(events, status, intersections, true);
        }
    }

    private static void findVerticalOverlaps(OutlineEdge edge, List<Pair<OutlineEdge, OutlineEdge>> intersections) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status = new TreeMap<>();

        // Populate the queue with events.
        edge.toList().stream().filter(e -> !e.getDirection().isHorizontal).forEach(e -> createOverlapEvent(events, e));

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            AbstractEvent currentEvent = events.poll();
            currentEvent.resolveOverlap(events, status, intersections, false);
        }
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

    private static void createOverlapEvent(PriorityQueue<AbstractEvent> events, OutlineEdge e) {
        LeftEndpointEvent event = new LeftEndpointEvent(e);
        events.add(event);
        events.add(new RightEndpointEvent(e, event));
    }
}
