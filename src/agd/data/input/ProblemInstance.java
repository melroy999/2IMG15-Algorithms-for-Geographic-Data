package agd.data.input;

import java.util.*;

public class ProblemInstance {
    // The id of the problem statement.
    public final int id;

    // The bounds of the problem statements viewport.
    public final int min_x, max_x, min_y, max_y;

    // The collection of points in the problem statement.
    private List<WeightedPoint> points = new ArrayList<>();

    /**
     * Create a problem instance with the given parameters.
     * @param id The id of the problem instance.
     * @param min_x The minimum x value of the viewport.
     * @param max_x The maximum x value of the viewport.
     * @param min_y The minimum y value of the viewport.
     * @param max_y The maximum y value of the viewport.
     */
    private ProblemInstance(int id, int min_x, int max_x, int min_y, int max_y) {
        this.id = id;
        this.min_x = min_x;
        this.max_x = max_x;
        this.min_y = min_y;
        this.max_y = max_y;
    }

    /**
     * Construct a problem instance through the input of a scanner.
     *
     * @param s The scanner that is reading the file with problem intance data.
     * @return A problem instance with the appropriate data.
     */
    public static ProblemInstance readInstance(Scanner s) {
        // Create a wrapper that will hold all the data about the problem to solve.
        ProblemInstance instance = new ProblemInstance(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt());

        // Add all of the new points.
        int n = s.nextInt();
        while(n > 0) {
            // Create a new point with the expected scanner input.
            instance.points.add(new WeightedPoint(s.nextDouble(), s.nextDouble(), s.nextInt(), instance.points.size()));
            n--;
        }

        return instance;
    }

    /**
     * Receive a copy of the list of points in the problem instance.
     *
     * @return A new arraylist containing the point set in the form of a list.
     */
    public List<WeightedPoint> getPoints() {
        return new ArrayList<>(points);
    }
}
