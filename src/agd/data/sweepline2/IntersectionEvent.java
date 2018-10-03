package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;

public class IntersectionEvent extends AbstractEvent {
    // The two line segments that intersect.
    private final LineSegment above, below;

    // The intersection point.
    private final Point2d i;

    public IntersectionEvent(LineSegment above, LineSegment below) {
        this.above = above;
        this.below = below;
        i = above.intersectionPoint(below);
    }

    @Override
    public void resolve(PriorityQueue<AbstractEvent> events, SweepStatus status, Set<Pair<OutlineEdge, OutlineEdge>> intersections) {
        // Add the intersection to the list of intersections.
        intersections.add(new Pair<>(above.edge, below.edge));

        // Note that we should now swap the two lines, as one should now be above the other.
        status.remove(above);
        status.remove(below);

        // Swap the two positions.
        above.swap(below);

        // Add them again to the status.
        status.insert(above);
        status.insert(below);

        // Note that the original above is now below. Above is segE1, below is SegE2.
        LineSegment s1 = status.above(below);
        LineSegment s2 = status.below(above);

        // Check whether the line segment intersects with one of its neighbors.
        if(s1 != null && s1.intersects(below)) events.add(new IntersectionEvent(s1, below));
        if(s2 != null && s2.intersects(above)) events.add(new IntersectionEvent(above, s2));
    }

    @Override
    public Point2d getPoint() {
        return i;
    }


}
