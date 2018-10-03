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

        // TODO what the hell is swapping?
        // Shorten the one that is below, such that it is now above.
        // This is difficult.

        // http://geomalgorithms.com/a09-_intersect-3.html


    }

    @Override
    public Point2d getPoint() {
        return i;
    }


}
