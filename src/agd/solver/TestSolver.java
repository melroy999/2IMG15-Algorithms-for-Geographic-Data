package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.output.HalfGridPoint;

import java.util.ArrayList;

/**
 * A... "framework" for a solution. WIP.
 */
public class TestSolver extends AbstractSolver {

    /**
     * Solve the given problem instance.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points   The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {
        instance.getPoints().forEach(p -> points.add(new HalfGridPoint(p.c.x, p.c.y, p)));
    }
}
