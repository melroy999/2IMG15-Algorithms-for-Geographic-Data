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
    }

    //TODO: Split points in half and get both sweeps working together

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
        for (HalfGridCorner p : eventPoints) {
            eventsLeft.add(new PlaceEventLeft(new Point2i((int)p.point().x + p.o.w, (int) p.point().y ), p.o));
        }
        
//        for (int i = 0; i < eventsRight.size(); i++) {
//            eventsLeft.add(eventsRight.poll());
//        }

        // Get events from PQ using poll(), use .execute() on event

        while (!eventsRight.isEmpty()) {
            eventsRight.poll().execute(intervalTreeRight, points, eventsRight);
        }

        while (!eventsLeft.isEmpty()) {
            eventsLeft.poll().execute(intervalTreeLeft, points, eventsRight);
        }
    }
}