package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.output.HalfGridCorner;
import agd.data.output.HalfGridPoint;
import agd.data.sweepline.AbstractEvent;
import agd.data.sweepline.DeleteEvent;
import agd.data.sweepline.IntervalTree;
import agd.data.sweepline.PlaceEvent;
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

        IntervalTree intervalTree = new IntervalTree();
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // TODO: Create events
        // Events: -Lower left region corner reached. Place square if possible or move to the right until possible to place
        //          and add square corner coords to status
        //         -Lower right region corner reached. Remove square corner coords from status
        for (HalfGridCorner p : eventPoints) {
            events.add(new PlaceEvent(new Point2i((int)p.point().x, (int) p.point().y ), p.o));
        }
        System.out.println(events + "\n");

        // Get events from PQ using poll(), use .execute() on event

        while (!events.isEmpty()) {
            events.poll().execute(intervalTree, points, events);
        }

        // TODO: Use a sweep line algorithm to start placing square regions, tracking the regions that have been placed
        // TODO: Figure out the above
        // Status: Coords of corners of currently placed squares in quadtree
    }
}