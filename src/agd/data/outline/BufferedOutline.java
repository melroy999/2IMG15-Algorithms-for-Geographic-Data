package agd.data.outline;

import agd.data.outline.OutlineEdge.Direction;
import agd.data.sweepline2.IntersectionSweep;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import static agd.data.outline.OutlineEdge.Direction.*;

/**
 * A buffered version of an original outline.
 */
public class BufferedOutline {
    // Access point to one of the edges in the outline.
    private OutlineEdge edge;

    /**
     * Create a new buffered outline.
     *
     * @param outline The outline to buffer.
     * @param w The width of the buffering zone.
     */
    public BufferedOutline(Outline outline, double w) {
        createOutline(outline, w);
    }

    /**
     * A constructor used for testing purposes.
     *
     * @param edge The root of the outline.
     */
    public BufferedOutline(OutlineEdge edge) {
        this.edge = edge;
    }

    /**
     * Generate the buffered outline associated with the given outline.
     *
     * @param outline The outline to buffer.
     * @param w The width of the buffering zone.
     */
    private void createOutline(Outline outline, double w) {
        // Keep the last encountered edge such that we can set next and previous references.
        OutlineEdge last = null;

        for(OutlineEdge e : outline.getEdge()) {
            // Translate each edge to the correct position.
            Point2d p = e.getOrigin().add(e.getDirection().bufferVector().scale(w));

            // Check whether we have made a left turn.
            if(e.getPrevious().getDirection().isLeftTurn(e.getDirection())) {
                p = p.add(e.getDirection().leftTurnVector().scale(2 * w));
            }

            // Create a new edge and set the appropriate pointers.
            OutlineEdge bufferedEdge = new OutlineEdge(p, e.getDirection());

            if(last != null) {
                last.setNext(bufferedEdge);
            }
            last = bufferedEdge;

            if(edge == null) {
                edge = bufferedEdge;
            }
        }

        // Finalize the cycle.
        assert last != null;
        last.setNext(edge);
    }

    /**
     * Get an access point to the edge cycle in the outline.
     *
     * @return The lowest edge in the outline.
     */
    public OutlineEdge getEdge() {
        return edge;
    }

    /**
     * Remove the intersections within the outline.
     */
    public void sanitizeOutline() {
        // Start by resolving intersections.
        for(Pair<OutlineEdge, OutlineEdge> pair : IntersectionSweep.findIntersections(edge)) {
            pair.getKey().resolveIntersection(pair.getValue());
        }

        // Proceed with resolving the remaining overlaps.
        for(Pair<OutlineEdge, OutlineEdge> pair : IntersectionSweep.findHorizontalOverlaps(edge)) {
            pair.getKey().resolveOverlap(pair.getValue());
        }
        for(Pair<OutlineEdge, OutlineEdge> pair : IntersectionSweep.findVerticalOverlaps(edge)) {
            pair.getKey().resolveOverlap(pair.getValue());
        }
    }

    /**
     * Project the point p onto the outline and find the position that has the smallest euclidean distance.
     *
     * @param p The point to find the closest position on the outline to.
     * @return A point on the line segments of the outline such that the distance is minimal.
     */
    public Point2d projectAndSelect(Point2d p) {
        // For each of the edges, find the best position and the associated score.
        double min = Double.MAX_VALUE;
        Point2d position = null;

        for(OutlineEdge e : edge) {
            Point2d projection = e.project(p);
            double distance = projection.distance2(p);
            if(distance < min) {
                position = projection;
                min = distance;
            }
        }

        return position;
    }

    /**
     * Project the point p onto the outline and find the position that has the smallest euclidean distance.
     * Points that are aimed towards the center of mass are marked as invalid.
     *
     * @param p The point to find the closest position on the outline to.
     * @param c The center of mass of all the points.
     * @return A point on the line segments of the outline such that the distance is minimal.
     */
    public Point2d projectAndSelect(Point2d p, Point2d c) {
        // For each of the edges, find the best position and the associated score.
        double min = Double.MAX_VALUE;
        Point2d position = null;

        // Get the relative position.
        Quadrant q = Quadrant.getQuadrant(p, c);

        for(OutlineEdge e : edge) {
            if(q.isValid(e.getDirection())) {
                Point2d projection = e.project(p);
                double distance = projection.distance2(p);
                if(distance < min) {
                    position = projection;
                    min = distance;
                }
            }
        }

        return position;
    }

    /**
     * Remove the intersections within the outline.
     */
    public void sanitizeOutlineBruteForce() {
        // Iterate over all edges, and find the line segments that intersect (O(n^2)).
        for(OutlineEdge source : edge) {
//            System.out.println("source: " + source);
            for(OutlineEdge target : source.getNext()) {
                // Stop when we are back at our source again.
                if(source.equals(target)) break;
//                System.out.println("target: " + target);

                // Check whether an intersection exists.
                if(source.hasIntersection(target)) {
                    source.resolveIntersection(target);
                    break;
                }
            }
        }
    }

    private static final String LATEX_POINT = "\\node[circle,fill,red,inner sep=1pt] (u%d) at (%f, %f) {};\n";
    private static final String LATEX_RECTANGLE = "\\draw (%d, %d) rectangle (%d, %d);\n";

    /**
     * Convert the outline to a figure in latex.
     *
     * @return A string representing a tikz figure in latex.
     */
    public String toLatexFigure() {
        List<OutlineEdge> edges = new ArrayList<>();
        for(OutlineEdge e : edge) {
            edges.add(e);
            if(e.getNext() == null) {
                break;
            }
        }

        // Draw the edges and nodes.
        List<String> latexNodes = new ArrayList<>();
        StringBuilder latexEdges = new StringBuilder("\\draw[red] ");
        for(int i = 0; i < edges.size(); i++) {
            OutlineEdge e = edges.get(i);
            latexNodes.add(String.format(LATEX_POINT, i, e.getOrigin().x, e.getOrigin().y));
            latexEdges.append("(u").append(i).append(") -- ");
        }
        latexEdges.append("(u").append(0).append(");\n");

        // Combine everything into one figure.
        StringBuilder result = new StringBuilder();
        result.append("\\resizebox{\\textwidth}{!}{% <------ Don't forget this %\n");
        result.append("\\begin{tikzpicture}[x=5mm, y=5mm, baseline, trim left]\n");
        result.append("\\tikz {\n");
        latexNodes.forEach(result::append);
        result.append(latexEdges);
        result.append("}\n");
        result.append("\\end{tikzpicture}\n");
        result.append("}");

        return result.toString();
    }

    /**
     * The different quadrants of the plane.
     */
    public enum Quadrant {
        NW(EnumSet.of(UP, RIGHT)),
        SW(EnumSet.of(UP, LEFT)),
        SE(EnumSet.of(DOWN, LEFT)),
        NE(EnumSet.of(DOWN, RIGHT)),
        ON(EnumSet.of(UP, RIGHT, LEFT, DOWN));

        private final EnumSet<Direction> valid;

        Quadrant(EnumSet<Direction> valid) {
            this.valid = valid;
        }

        public static Quadrant getQuadrant(Point2d p, Point2d c) {
            if(p.distance2(c) < 1e-4) {
                return ON;
            } else {
                if(p.x <= c.x) {
                    // West.
                    if(p.y <= c.y) {
                        // South.
                        return SW;
                    } else {
                        return NW;
                    }
                } else {
                    // East.
                    if(p.y <= c.y) {
                        // South.
                        return SE;
                    } else {
                        return NE;
                    }
                }
            }
        }

        public boolean isValid(Direction d) {
            return valid.contains(d);
        }
    }
}
