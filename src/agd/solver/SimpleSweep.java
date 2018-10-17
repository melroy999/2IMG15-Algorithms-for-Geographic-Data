package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.output.HalfGridCorner;
import agd.data.output.HalfGridPoint;
import agd.data.sweepline.*;
import agd.math.Point2i;

import java.util.*;

// Sweep line algorithm that handles points from left to right
public class SimpleSweep extends AbstractSolver {
    // Variables

    /**
     * Translate all points to their lower-left region corner and sort the points by x-coord
     *
     * @param instance The problem instance that contains all the required data.
     * @param points   The list of points.
     */
    private void translatePoints (ProblemInstance instance, ArrayList<HalfGridCorner> points){

        // Points now contains all the lower left corners of instance.getPoints as well as a reference to the original
        // point p
        // point(x, y)→round(point(x, y)−0.5wx)
        instance.getPoints().forEach(p ->
                points.add(new HalfGridCorner(Math.round(p.x - 0.5*p.w), Math.round(p.y - 0.5*p.w), p)));

        points.sort(new SortByX());
    }

    //TODO: Split points in half and get both sweeps working together
    //TODO: Problems, combination of max and min heap with sweep depending on order

    /**
     * Solve the given problem instance using a left to right sweep line.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {

        ArrayList<HalfGridCorner> eventPoints = new ArrayList<>();
        translatePoints(instance, eventPoints);

        IntervalTree intervalTreeRight = new IntervalTree();
        PriorityQueue<AbstractEvent> eventsRight = new PriorityQueue<>();

        IntervalTree intervalTreeLeft = new IntervalTree();
        PriorityQueue<AbstractEvent> eventsLeft = new PriorityQueue<>(Collections.reverseOrder());

        // Events: -Lower left region corner reached. Place square if possible or move to the right until possible to place
        //          and add square corner coords to status
        //         -Lower right region corner reached. Remove square corner coords from status
//        for (HalfGridCorner p : eventPoints) {
//            eventsRight.add(new PlaceEvent(new Point2i((int)p.point().x, (int) p.point().y ), p.o));
//        }

        HalfGridCorner tempPoint;
        for (int i = 0; i < eventPoints.size(); i++) {
            if (i < Math.round(eventPoints.size() /2.0)) {
                tempPoint = eventPoints.get(i);
                eventsLeft.add(new PlaceEventLeft(new Point2i((int)tempPoint.point().x + tempPoint.o.w, (int) tempPoint.point().y ),
                        tempPoint.o));
            } else {
                tempPoint = eventPoints.get(i);
                eventsRight.add(new PlaceEvent(new Point2i((int) tempPoint.point().x, (int) tempPoint.point().y), tempPoint.o));
            }
            //eventsLeft.add(eventsRight.poll());
        }

        // Get events from PQ using poll(), use .execute() on event

        while (!eventsLeft.isEmpty()) {
            eventsLeft.poll().execute(intervalTreeRight, points, eventsRight);
        }

        while (!eventsRight.isEmpty()) {
            eventsRight.poll().execute(intervalTreeRight, points, eventsRight);
        }
    }
}

class SortByX implements Comparator<HalfGridCorner> {
    // Sort HalfGridPoints by their x-coords with y-coord
    public int compare(HalfGridCorner a, HalfGridCorner b) {
        return a.point().x == b.point().x ? Double.compare(a.point().y, b.point().y) : Double.compare(a.point().x, b.point().x);
    }
}