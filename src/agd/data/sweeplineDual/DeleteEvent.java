package agd.data.sweeplineDual;

import agd.data.input.WeightedPoint;
import agd.data.output.HalfGridPoint;
import agd.math.Point2i;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class DeleteEvent extends AbstractEvent {

    public DeleteEvent(Point2i p, WeightedPoint owner) {
        super(p, owner);
    }

    // -Lower right region corner reached. Remove square corner coords from status

    /**
     *
     * @param intervalTree
     * @param points
     */
    @Override
    public void execute(IntervalTree intervalTree, ArrayList<HalfGridPoint> points, PriorityQueue<AbstractEvent> events) {
        Interval interval = new Interval(getP().y, (getP().y + getOwner().w),getP().x, getP().x + getOwner().w, getOwner().i);
        intervalTree.setRoot(intervalTree.deleteInterval(intervalTree.getRoot(), interval));
    }
}
