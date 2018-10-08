package agd.data.sweepline;

import agd.data.input.WeightedPoint;
import agd.data.output.HalfGridPoint;
import agd.math.Point2i;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class PlaceEvent extends AbstractEvent {

    public PlaceEvent(Point2i p, WeightedPoint owner) {
        super(p, owner);
    }

    // -Lower left region corner reached. Place square if possible or move to the right until possible to place
    //  and add square corner coords to status

    /**
     *
     * @param intervalTree
     * @param points
     */
    @Override
    public void execute(IntervalTree intervalTree, ArrayList<HalfGridPoint> points, PriorityQueue<AbstractEvent> events) {

        intervalTree.printTree(intervalTree.getRoot());
        System.out.println("\n");

        // Get overlaps between interval tree and point interval on y-coords
        Interval interval = new Interval(getP().y, (getP().y + getOwner().w), getP().x, getOwner().i);
        List<Interval> overlaps;
        overlaps = intervalTree.checkInterval(intervalTree.getRoot(), interval);

        // Get max depth in overlaps
        int maxDepth = Integer.MIN_VALUE;
        for (Interval i: overlaps ) {
            if (i.getDepth() > maxDepth) {
                maxDepth = i.getDepth();
            }
        }

        System.out.println(interval + ": ");
        for (Interval i : overlaps) {
            System.out.println(i);
        }

        HalfGridPoint placedPoint;
        // Check if p.x is greater than the max depth in overlapping intervals
        if (maxDepth > interval.getDepth()) {
            interval.setDepth(maxDepth + getOwner().w);
            // Create a HalfGridPoint with correct x coord
            placedPoint = new HalfGridPoint(maxDepth + (getOwner().w * 0.5), getP().y + (getOwner().w * 0.5), getOwner());
        } else {
            interval.setDepth(interval.getDepth() + getOwner().w);
            // Create a HalfGridPoint with correct x coord
            placedPoint = new HalfGridPoint(getP().x + getOwner().w * 0.5, getP().y + getOwner().w * 0.5, getOwner());
        }

        System.out.println(placedPoint);
        System.out.println("\n");
        // Add interval to interval tree and add placedPoint to solution
        intervalTree.setRoot(intervalTree.addInterval(intervalTree.getRoot(), interval));
        points.add(placedPoint);

        events.add(new DeleteEvent(new Point2i(interval.getDepth(), interval.getStart()), getOwner()));
    }
}
