package agd.store.instance;

public class ProblemInstance {
    // The id of the problem statement.
    public int id;

    // The bounds of the problem statements viewport.
    public int minx, maxx, miny, maxy;

    // The collection of points in the problem statement.
    public WeightedPointList points = new WeightedPointList();
}
