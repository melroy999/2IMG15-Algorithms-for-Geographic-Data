package agd.data.output;

import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.math.Point2d;
import agd.solver.AbstractSolver;

import java.util.*;

/**
 * A data structure that contains the solution for the given problem instance.
 */
public class ProblemSolution {
    // The problem instance we are attempting to solve.
    private final ProblemInstance instance;

    // The points that are in the solution.
    private final ArrayList<HalfGridPoint> points = new ArrayList<>();

    /**
     * Create a solution for the given problem.
     *
     * @param instance The problem instance we are constructing a solution for.
     */
    public ProblemSolution(ProblemInstance instance, AbstractSolver solver) {
        this.instance = instance;

        // Populate the points list.
        solver.solve(instance, points);
    }

    /**
     * Calculate the unavoidable minimum error the problem instance will have.
     *
     * @return The sum of the differences between the original points and the closest reference points.
     */
    public double getMinimumError() {
        return points.stream().map(h -> h.o.distance2(h.o.c)).reduce(0d, Double::sum);
    }

    /**
     * Calculate the total error the problem instance solution.
     *
     * @return The sum of the differences between the original points and the solution positions.
     */
    public double getTotalError() {
        return points.stream().map(h -> h.o.distance2(h.point())).reduce(0d, Double::sum);
    }

    /**
     * Get the ids of the points that have been placed invalidly.
     *
     * @return A set of integers representing the ids of the points that are invalid.
     */
    public Set<Integer> getInvalidPoints() {
        // A set of integers holding all points that have faulty placements.
        HashSet<Integer> errors = new HashSet<>();

        for(HalfGridPoint p : points) {
            // First check if the coordinates are valid.
            Point2d bl = p.point().add(new Point2d(p.o.w, p.o.w).scale(-0.5d));
            Point2d blRounded = new Point2d(Math.round(bl.x), Math.round(bl.y));

            if(!bl.epsilonEquals(blRounded, 0.01)) {
                // The assigned point is invalid, since the corner points are not on integer positions.
                errors.add(p.o.i);
                System.out.println("Point " + p.o.i + " does not have its corner position on integer positions.");
            }

            // Next, check if the node overlaps with other regions.
            for(HalfGridPoint q : points) {
                if(p.o.i != q.o.i && p.hasOverlap(q)) {
                    errors.add(p.o.i);
                    System.out.println("Point " + p.o.i + " overlaps with " + q.o.i + ".");
                }
            }
        }

        // Check whether each point is represented in the solution.
        List<WeightedPoint> originalPoints = instance.getPoints();

        if(points.size() != instance.getPoints().size()) {
            throw new RuntimeException("There are points missing in the solution (or there are too many points)!");
        } else {
            List<HalfGridPoint> sortedPoints = getPoints();
            sortedPoints.sort(Comparator.comparingInt(a -> a.o.i));

            for(int i = 0; i < points.size(); i++) {
                if(originalPoints.get(i).i != sortedPoints.get(i).o.i) {
                    throw new RuntimeException("The " + i + "th point is not present in the solution!");
                }
            }
        }

        return errors;
    }

    /**
     * Receive a copy of the list of points in the problem instance.
     *
     * @return A new arraylist containing the point set in the form of a list.
     */
    public List<HalfGridPoint> getPoints() {
        return new ArrayList<>(points);
    }

    /**
     * Output the configuration to text.
     *
     * @return A string containing the entire contents of the file.
     */
    public String output() {
        StringBuilder builder = new StringBuilder();
        builder.append(7).append("\n");
        builder.append(instance.id);

        // Sort the points on the id.
        points.sort(Comparator.comparingInt(a -> a.o.i));

        for(HalfGridPoint p : points) {
            Point2d t = p.point();
            builder.append("\n").append(t.x).append(" ").append(t.y);
        }

        return builder.toString();
    }
}
