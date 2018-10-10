package agd.data.sweepline;

import agd.data.input.WeightedPoint;
import agd.data.output.HalfGridPoint;
import agd.math.Point2i;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PlaceEventLeft extends AbstractEvent {
    public PlaceEventLeft(Point2i p, WeightedPoint owner) {
        super(p, owner);
    }

    @Override
    public void execute(IntervalTree intervalTree, ArrayList<HalfGridPoint> points, PriorityQueue<AbstractEvent> events) {
        // Get overlaps between interval tree and point interval on y-coords
        Interval interval = new Interval(getP().y, (getP().y + getOwner().w), getP().x, getOwner().i);
        List<Interval> overlaps;
        overlaps = intervalTree.checkInterval(intervalTree.getRoot(), interval);

        // Get max depth in overlaps
        int minValue = Integer.MAX_VALUE;
        for (Interval i: overlaps ) {
            if (i.getDepth() < minValue) {
                minValue = i.getDepth();
            }
        }

        System.out.println(minValue);
        System.out.println(overlaps);

        HalfGridPoint placedPoint;
        // Check if p.x is greater than the max depth in overlapping intervals
        if (minValue < interval.getDepth()) {
            interval.setDepth(minValue - getOwner().w);
            // Create a HalfGridPoint with correct x coord
            placedPoint = new HalfGridPoint(minValue - (getOwner().w * 0.5), getP().y + (getOwner().w * 0.5), getOwner());
        } else {
            interval.setDepth(interval.getDepth() - getOwner().w);
            // Create a HalfGridPoint with correct x coord
            placedPoint = new HalfGridPoint(getP().x - getOwner().w * 0.5, getP().y + getOwner().w * 0.5, getOwner());
        }

        // Add interval to interval tree and add placedPoint to solution
        intervalTree.setRoot(intervalTree.addInterval(intervalTree.getRoot(), interval));
        points.add(placedPoint);

        // Create deletion event for square
        events.add(new DeleteEvent(new Point2i(interval.getDepth(), interval.getStart()), getOwner()));
    }
}
