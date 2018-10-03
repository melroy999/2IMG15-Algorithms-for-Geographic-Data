package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.ArrayList;

public class IntersectionEvent extends AbstractEvent {
    public IntersectionEvent(Point2d point) {
        super(point);
    }

    @Override
    public void resolve(SweepStatus status, ArrayList<Pair<OutlineEdge, OutlineEdge>> intersections) {

    }
}
