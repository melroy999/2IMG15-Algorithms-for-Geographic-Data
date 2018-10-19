package agd.intersection;

import agd.data.outlines.Edge;
import agd.math.Point2d;

import java.util.*;

public class VerticalLineEvent extends AbstractEvent {
    // The other endpoint of the line.
    public final Point2d upper;

    // The outline edge.
    public final Edge e;

    public VerticalLineEvent(Edge e) {
        super(e.getDirection() == Edge.Direction.UP ? e.getOrigin() : e.getTarget(), EventType.VerticalLine);
        upper = e.getDirection() == Edge.Direction.DOWN ? e.getOrigin() : e.getTarget();

        if(p.y > upper.y) {
            System.out.println("HOW!");
        }

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
    public void execute(PriorityQueue<AbstractEvent> events, TreeMap<DoubleWrapper, Set<LeftEndpointEvent>> status, Map<Integer, Set<Edge>> intersections) {
        if(p.y > upper.y) {
            System.out.println("HOW!");
        }

        // Do a range search on the tree map.
        Map<DoubleWrapper, Set<LeftEndpointEvent>> range = status.subMap(new DoubleWrapper(p.y), new DoubleWrapper(upper.y));

        // We have potential intersections with all elements in the tree.
        List<LeftEndpointEvent> targets = range.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
        targets.stream().filter(t -> e.doIntersect(t.e)).forEach(t -> {
            Set<Edge> set = intersections.getOrDefault(e.getId(), new TreeSet<>());
            set.add(t.e);
            intersections.putIfAbsent(e.getId(), set);

            Set<Edge> set2 = intersections.getOrDefault(t.e.getId(), new TreeSet<>());
            set2.add(e);
            intersections.putIfAbsent(t.e.getId(), set2);
        });
    }
}