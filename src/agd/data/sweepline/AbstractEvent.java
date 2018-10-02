package agd.data.sweepline;

import agd.data.input.WeightedPoint;
import agd.math.Point2i;

public abstract class AbstractEvent implements Comparable<AbstractEvent>{
    // Events: -Lower left region corner reached. Place square if possible or move to the right until possible to place
    //          and add square corner coords to status
    //         -Lower right region corner reached. Remove square corner coords from status

    private final Point2i p;
    private final WeightedPoint owner;

    public AbstractEvent(Point2i p, WeightedPoint owner) {
        this.p = p;
        this.owner = owner;
    }

    @Override
    public int compareTo(AbstractEvent o) {
        return p.x == o.p.x ? Integer.compare(p.y, o.p.y) : Integer.compare(p.x, o.p.x);
    }

    public abstract void execute();
}