package agd.sweepline;

import agd.data.input.ProblemInstance;
import agd.data.output.HalfGridPoint;
import agd.solver.AbstractSolver;

import java.util.ArrayList;
import java.util.Comparator;

// Sweep line algorithm that handles points from left to right
public class LeftRight extends AbstractSolver {
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
        // Moet ik nog kijken of ze terug kunnen naar de orignele volgorde aangezien ze een referentie hebben naar het orignele punt?
        points.sort(new SortByX());
    }

    /**
     * Sort the translated points by x-coord, making sure that the order can be reverted to the original
     */
    private void sortPoints () {

    }

    /**
     * Solve the given problem instance using a left to right sweep line.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {

        translatePoints(instance, points);

        // TODO: Create events

        // TODO: Use a sweep line algorithm to start placing square regions, tracking the regions that have been placed
        // TODO: Figure out the above

        // Status: Coords of corners of currently placed squares

        // Events: -Lower left region corner reached. Place square if possible or move to the right until possible to place
        //          and add square corner coords to status
        //         -Lower right region corner reached. Remove square corner coords from status
    }
}

class SortByX implements Comparator<HalfGridPoint>
{
    // Sort HalfGridPoints by their x-coords
    public int compare(HalfGridPoint a, HalfGridPoint b)
    {
        return (int)a.point().x - (int)b.point().x;
    }
}