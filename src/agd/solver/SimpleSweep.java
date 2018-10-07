package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.output.HalfGridPoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Sweep line algorithm that handles points from left to right
public class SimpleSweep extends AbstractSolver {
    // Variables

    /**
     * Translate all points to their lower-left region corner and sort the points by x-coord
     *
     * @param instance The problem instance that contains all the required data.
     * @param points   The list of points.
     */
    private void translatePoints (ProblemInstance instance, ArrayList<HalfGridPoint> points){

        // Points now contains all the lower left corners of instance.getPoints as well as a reference to the original
        // point p
        // point(x, y)→round(point(x, y)−0.5wx)
        instance.getPoints().forEach(p ->
                points.add(new HalfGridPoint(Math.round(p.x - 0.5*p.w), Math.round(p.y - 0.5*p.w), p)));

        // Sort the points by x-coord
        points.sort(new SortByX());
    }

    /**
     * Solve the given problem instance using a left to right sweep line.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {

        ArrayList<HalfGridPoint> eventPoints = new ArrayList<>();
        translatePoints(instance, eventPoints);

        // TODO: Create events
        // Events: -Lower left region corner reached. Place square if possible or move to the right until possible to place
        //          and add square corner coords to status
        //         -Lower right region corner reached. Remove square corner coords from status

        // Get events from list, or other data structure, use .execute() on event

        // TODO: Use a sweep line algorithm to start placing square regions, tracking the regions that have been placed
        // TODO: Figure out the above
        // Status: Coords of corners of currently placed squares in quadtree
    }
}

class SortByX implements Comparator<HalfGridPoint>
{
    // Sort HalfGridPoints by their x-coords with y-coord
    public int compare(HalfGridPoint a, HalfGridPoint b)
    {
        return a.point().x == b.point().x ? Double.compare(a.point().y, b.point().y) : Double.compare(a.point().x, b.point().x);
    }
}