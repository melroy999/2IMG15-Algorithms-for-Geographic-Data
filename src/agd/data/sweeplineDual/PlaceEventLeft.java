package agd.data.sweeplineDual;

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
        Interval interval = new Interval(getP().y, (getP().y + getOwner().w), getP().x, getP().x + getOwner().w, getOwner().i);
        List<Interval> overlaps;
        overlaps = intervalTree.checkInterval(intervalTree.getRoot(), interval);

        // Get max depth in overlaps
        int minValue = Integer.MAX_VALUE;
        for (Interval i: overlaps ) {
            if (i.getMinDepth() < minValue) {
                minValue = i.getMinDepth();
            }
        }

        HalfGridPoint placedPoint;
        // Check if p.x is greater than the max depth in overlapping intervals
        if (minValue < interval.getMinDepth()) {
            interval.setMinDepth(minValue - getOwner().w);
            interval.setMaxDepth(minValue);
            // Create a HalfGridPoint with correct x coord
            placedPoint = new HalfGridPoint(minValue - (getOwner().w * 0.5), getP().y + (getOwner().w * 0.5), getOwner());
        } else {
            interval.setMinDepth(interval.getMinDepth() - getOwner().w);
            interval.setMaxDepth(interval.getMinDepth() + getOwner().w);
            // Create a HalfGridPoint with correct x coord
            placedPoint = new HalfGridPoint(getP().x - getOwner().w * 0.5, getP().y + getOwner().w * 0.5, getOwner());
        }

        // Add interval to interval tree and add placedPoint to solution
        intervalTree.setRoot(intervalTree.addInterval(intervalTree.getRoot(), interval));
        points.add(placedPoint);

        // Create deletion event for square
        events.add(new DeleteEvent(new Point2i(interval.getMaxDepth(), interval.getStart()), getOwner()));
    }
}
