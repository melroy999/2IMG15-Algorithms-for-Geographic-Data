package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.output.HalfGridPoint;
import agd.data.output.ProblemSolution;

import java.util.ArrayList;

/**
 * A class that can be extended to create solvers for the problem.
 */
public abstract class AbstractSolver {
    public enum SortingOptions {
        MANHATTAN_CENTROID, CENTROID, CORNER, CLOSEST_POINT, FURTHEST, NONE, SIZE_ASC, SIZE_DESC, X, Y, ROTATION, MAX_BASED, MIN_BASED
    }

    /**
     * Solve the given problem instance.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points The list of placed points.
     */
    public abstract void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points);
}
