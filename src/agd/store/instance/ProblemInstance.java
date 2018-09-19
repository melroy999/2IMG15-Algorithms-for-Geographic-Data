package agd.store.instance;

public class ProblemInstance {
    // The id of the problem statement.
    public final int id;

    // The bounds of the problem statements viewport.
    public final int minx, maxx, miny, maxy;

    // The collection of points in the problem statement.
    private WeightedPointList points = new WeightedPointList();

    public ProblemInstance(int id, int minx, int maxx, int miny, int maxy) {
        this.id = id;
        this.minx = minx;
        this.maxx = maxx;
        this.miny = miny;
        this.maxy = maxy;
    }

    public void addPoint(double x, double y, int w) {
        points.add(x, y, w);
    }

    public WeightedPointList getPoints() {
        return new WeightedPointList(points);
    }
}
