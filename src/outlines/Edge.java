package outlines;

import agd.math.Point2d;

public class Edge {
    public final Point2d p1, p2;

    public Edge(Point2d p1, Point2d p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public String toString() {
        return "\\draw[red] (" + p1.x + ", " + p1.y + ") -- (" + p2.x + ", " + p2.y + ");";
    }

    public static Edge rescale(Edge edge) {
        return new Edge(edge.p1.scale(0.5), edge.p2.scale(0.5));
    }

    /**
     * Project the given point onto the line segment.
     *
     * @param p The point to project on the line segment.
     * @return A point on the line segment that has the shortest euclidean distance to p.
     */
    public Point2d project(Point2d p) {
        // Find the bounds of the projection.
        double xmin = Math.min(p1.x, p2.x);
        double xmax = Math.max(p1.x, p2.x);
        double ymin = Math.min(p1.y, p2.y);
        double ymax = Math.max(p1.y, p2.y);
        return new Point2d(Math.max(xmin, Math.min(xmax, p.x)), Math.max(ymin, Math.min(ymax, p.y)));
    }
}
