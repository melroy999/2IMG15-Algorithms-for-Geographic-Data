package agd.data.sweeplineDual;

import agd.data.input.WeightedPoint;
import agd.data.output.HalfGridPoint;
import agd.math.Point2i;

import java.util.ArrayList;
import java.util.PriorityQueue;

public abstract class AbstractEvent implements Comparable<AbstractEvent>{
    // Events: -Lower left region corner reached. Place square if possible or move to the right until possible to place
    //          and add square corner coords to status
    //         -Lower right region corner reached. Remove square corner coords from status

    private final Point2i p;
    private final WeightedPoint owner;

    public Point2i getP() {
        return p;
    }

    public WeightedPoint getOwner() {
        return owner;
    }

    public AbstractEvent(Point2i p, WeightedPoint owner) {
        this.p = p;
        this.owner = owner;
    }

    @Override
    public int compareTo(AbstractEvent o) {
        return p.x == o.p.x ? Integer.compare(p.y, o.p.y) : Integer.compare(p.x, o.p.x);
    }

    public String toString() {
        return "[" + this.p.x + " " + this.p.y + " " + this.owner.i +  "]";
    }

    public abstract void execute(IntervalTree intervalTree, ArrayList<HalfGridPoint> points, PriorityQueue<AbstractEvent> events);
}