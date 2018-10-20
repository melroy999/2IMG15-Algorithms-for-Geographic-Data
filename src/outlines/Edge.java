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
}
