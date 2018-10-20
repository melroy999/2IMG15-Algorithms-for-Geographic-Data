package agd.overlap;


import agd.data.outlines.Edge;
import agd.intersection.DoubleWrapper;
import agd.math.Point2d;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

/**
 * A two-stage sweep line algorithm that detects overlaps between lines in a rectilinear plane having the same direction.
 */
public class OverlapSweep {
    public static TreeMap<Integer, Set<Edge>> findOverlaps(Edge... edges) {
        TreeMap<Integer, Set<Edge>> overlaps = new TreeMap<>();
        findOverlaps(overlaps, edges);
        return overlaps;
    }

    private static void findOverlaps(Map<Integer, Set<Edge>> overlaps, Edge... edges) {
        findHorizontalOverlaps(overlaps, edges);
        findVerticalOverlaps(overlaps, edges);
    }

    private static void findHorizontalOverlaps(Map<Integer, Set<Edge>> overlaps, Edge... edges) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<StartPointEvent>> status = new TreeMap<>();

        // Populate the queue with events.
        for(Edge edge : edges) {
            edge.forEach(e -> createHorizontalOverlapEvent(events, e));
        }

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            AbstractEvent currentEvent = events.poll();
            currentEvent.execute(events, status, overlaps);
        }
    }

    private static void findVerticalOverlaps(Map<Integer, Set<Edge>> overlaps, Edge... edges) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<StartPointEvent>> status = new TreeMap<>();

        // Populate the queue with events.
        for(Edge edge : edges) {
            edge.forEach(e -> createVerticalOverlapEvent(events, e));
        }

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            AbstractEvent currentEvent = events.poll();
            currentEvent.execute(events, status, overlaps);
        }
    }

    private static void createHorizontalOverlapEvent(PriorityQueue<AbstractEvent> events, Edge e) {
        // We only allow horizontal edges here.
        if(!e.getDirection().isHorizontal) {
            return;
        }

        StartPointEvent event = new StartPointEvent(e, e.getDirection() == Edge.Direction.RIGHT ? e.getOrigin() : e.getTarget());
        events.add(event);
        events.add(new EndPointEvent(e.getDirection() == Edge.Direction.LEFT ? e.getOrigin() : e.getTarget(), event));
    }

    private static void createVerticalOverlapEvent(PriorityQueue<AbstractEvent> events, Edge e) {
        // We only allow vertical edges here.
        if(e.getDirection().isHorizontal) {
            return;
        }

        // We have to swap the x and y-axis here, since we sweep from left to right by sorting.
        Point2d start = e.getDirection() == Edge.Direction.UP ? e.getOrigin() : e.getTarget();
        Point2d end = e.getDirection() == Edge.Direction.DOWN ? e.getOrigin() : e.getTarget();

        //noinspection SuspiciousNameCombination
        StartPointEvent event = new StartPointEvent(e, new Point2d(start.y, start.x));

        events.add(event);

        //noinspection SuspiciousNameCombination
        events.add(new EndPointEvent(new Point2d(end.y, end.x), event));
    }
}
