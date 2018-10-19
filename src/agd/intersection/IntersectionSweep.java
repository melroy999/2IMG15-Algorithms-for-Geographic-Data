package agd.intersection;

import agd.data.outlines.Edge;

import java.util.*;

/**
 * A vertical sweep line algorithm that detects intersections between lines in a rectilinear plane.
 */
public class IntersectionSweep {

    public static TreeMap<Edge, Set<Edge>> findIntersections(Edge... edges) {
        TreeMap<Edge, Set<Edge>> intersections = new TreeMap<>();
        findIntersections(intersections, edges);
        return intersections;
    }

    public static TreeMap<Edge, Set<Edge>> findIntersectionsBF(Edge... edges) {
        TreeMap<Edge, Set<Edge>> intersections = new TreeMap<>();

        List<Edge> edgeCollection = new ArrayList<>();
        Arrays.stream(edges).forEach(e -> edgeCollection.addAll(e.toList()));

        for(int i = 0; i < intersections.size(); i++) {
            Edge target = edgeCollection.get(i);

            for(int j = 1; j < intersections.size() - 1; j++) {
                Edge conflict = edgeCollection.get((i + j) % edgeCollection.size());

                // Do the edges have an overlap?
                if(target.doIntersect(conflict) || conflict.doIntersect(target)) {
                    Set<Edge> set = intersections.getOrDefault(target, new TreeSet<>());
                    set.add(conflict);
                    intersections.putIfAbsent(target, set);

                    Set<Edge> set2 = intersections.getOrDefault(conflict, new TreeSet<>());
                    set2.add(target);
                    intersections.putIfAbsent(conflict, set2);
                }
            }
        }

        return intersections;
    }

    private static void findIntersections(Map<Edge, Set<Edge>> intersections, Edge... edges) {
        // The queue of events.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The state of the sweep, for which a tree map is used.
        // The key in the map represents the y-value of a line segment.
        TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status = new TreeMap<>();

        // Populate the queue with events.
        for(Edge edge : edges) {
            edge.forEach(e -> createEvent(events, e));
        }

        // Keep the vertical line events with the same y-value.
        PriorityQueue<VerticalLineEvent> verticalLines = new PriorityQueue<>();
        double lastY = -Double.MAX_VALUE;

        // Keep taking events from the event queue until it is empty.
        while(!events.isEmpty()) {
            AbstractEvent currentEvent = events.poll();
            currentEvent.execute(events, status, intersections);

            if(currentEvent instanceof VerticalLineEvent) {
                // Has the y-value changed?
                if(Math.abs(lastY - currentEvent.p.y) < 1e-4) {
                    // Check if the vertical lines have overlaps.
                    findVerticalOverlaps(verticalLines, intersections);
                }

                // Add the event to the priority queue.
                verticalLines.add((VerticalLineEvent) currentEvent);
            }

            // Set the new y.
            lastY = currentEvent.p.y;
        }

        // Do a final overlap check.
        findVerticalOverlaps(verticalLines, intersections);
    }

    @SuppressWarnings("Duplicates")
    private static void findVerticalOverlaps(PriorityQueue<VerticalLineEvent> verticalLines, Map<Edge, Set<Edge>> intersections) {
        List<VerticalLineEvent> lines = new ArrayList<>(verticalLines);

        // Check if the vertical lines have overlaps.
        for(int i = 0; i < lines.size(); i++) {
            VerticalLineEvent e1 = lines.get(i);

            for(int j = i + 1; j < lines.size(); j++) {
                VerticalLineEvent e2 = lines.get(j);

                // Is e1 still in range of e2?
                if(e1.upper.y < e2.p.y) {
                    break;
                } else {
                    // They overlap, if they have the same direction.
                    if(e1.e.getDirection() == e2.e.getDirection()) {
                        Set<Edge> set = intersections.getOrDefault(e1.e, new TreeSet<>());
                        set.add(e2.e);
                        intersections.putIfAbsent(e1.e, set);

                        Set<Edge> set2 = intersections.getOrDefault(e2.e, new TreeSet<>());
                        set2.add(e1.e);
                        intersections.putIfAbsent(e2.e, set2);
                    }
                }
            }
        }

        // Empty the list.
        verticalLines.clear();
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
