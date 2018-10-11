package agd.data.outlines;

import agd.data.outlines.Edge.Direction;
import agd.data.util.OutlineDimensions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An abstract variant of an outline, defining the common structures and functions.
 */
public abstract class AbstractOutline implements Iterable<Edge> {
    // Each outline has a number of rectangles that are part of it.
    private final List<OutlineRectangle> rectangles = new ArrayList<>();

    // Access point to one of the edges in the outline.
    private Edge edge;

    // The current dimensions of the outline.
    private OutlineDimensions dimensions;

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public AbstractOutline(OutlineRectangle rectangle) {
        // A left directional edge in a rectangle can only be the bottom edge.
        edge = rectangle.createOutline(Direction.LEFT);
        dimensions = new OutlineDimensions(rectangle.x, rectangle.y, rectangle.width, rectangle.height, this);
        addRectangle(rectangle);
    }

    /**
     * A constructor that should be used when basing an outline on another outline.
     */
    public AbstractOutline(List<OutlineRectangle> rectangles) {
        this.rectangles.addAll(rectangles);
    }

    /**
     * Get the list of rectangles within the outline.
     *
     * @return The rectangles the outline consists of.
     */
    public List<OutlineRectangle> getRectangles() {
        return rectangles;
    }

    /**
     * Set the access point of the outline.
     *
     * @param edge The edge to use as an access point of the outline.
     */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    /**
     * Get the edge that gives access to the cycle.
     *
     * @return The access point of the cycle.
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Add a rectangle to the list of rectangles.
     *
     * @param rectangle The new rectangle.
     */
    void addRectangle(OutlineRectangle rectangle) {
        rectangles.add(rectangle);
        rectangle.setOutline(this);

        // Set the new bounding box of the outline.
        int minx = Math.min(rectangle.x, dimensions.x);
        int maxx = Math.max(rectangle.x + rectangle.width, dimensions.x + dimensions.width);
        int miny = Math.min(rectangle.y, dimensions.y);
        int maxy = Math.max(rectangle.y + rectangle.height, dimensions.y + dimensions.height);
        this.dimensions = new OutlineDimensions(minx, miny, maxx - minx, maxy - miny, this);
    }

    /**
     * Get the dimensions of the outline.
     *
     * @return The dimensions of rectangular box around the outline.
     */
    public OutlineDimensions getDimensions() {
        return dimensions;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Edge> iterator() {
        return edge.iterator();
    }

    /**
     * Returns an iterator over elements of type {@code T}, which iterates over the edges backwards.
     *
     * @return an Iterator.
     */
    public Iterator<Edge> reverseIterator() {
        return edge.reverseIterator();
    }

    // Patterns for node and rectangle constructs in the tikz language.
    private static final String LATEX_POINT = "\\node[circle,fill,red,inner sep=1pt] (v%d) at (%f, %f) {};\n";
    private static final String LATEX_RECTANGLE = "\\draw (%d, %d) rectangle (%d, %d);\n";
    private static final String LATEX_RECTANGLE_BB = "\\draw[cyan] (%d, %d) rectangle (%d, %d);\n";

    /**
     * Convert the outline to a figure in latex.
     *
     * @return A string representing a tikz figure in latex.
     */
    public String toLatexFigure() {
        // Combine everything into one figure.
        StringBuilder result = new StringBuilder();
        result.append("\\resizebox{\\textwidth}{!}{% <------ Don't forget this %\n");
        result.append("\\begin{tikzpicture}[x=5mm, y=5mm, baseline, trim left]\n");
        result.append("\\tikz {\n");
        result.append(toTikzCode());
        result.append("}\n");
        result.append("\\end{tikzpicture}\n");
        result.append("}");

        return result.toString();
    }
    /**
     * Convert the outline to a code representation in tikz.
     *
     * @return A string representing a tikz figure in latex.
     */
    public String toTikzCode() {
        StringBuilder result = new StringBuilder();
        List<Edge> edges = edge.toList();

        // Draw the edges and nodes.
        List<String> latexNodes = new ArrayList<>();
        StringBuilder latexEdges = new StringBuilder("\\draw[red] ");
        for (Edge e : edges) {
            latexNodes.add(String.format(LATEX_POINT, e.getId(), e.getOrigin().x, e.getOrigin().y));
            latexEdges.append("(v").append(e.getId()).append(") -- ");
        }
        latexEdges.append("cycle;\n");

        // Draw the rectangles.
        List<String> latexRectangles = new ArrayList<>();
        Rectangle bb = dimensions;
        if(bb != null) {
            latexRectangles.add(String.format(LATEX_RECTANGLE_BB, bb.x, bb.y, bb.x + bb.width, bb.y + bb.height));
        }
        for(OutlineRectangle r : rectangles) {
            latexRectangles.add(String.format(LATEX_RECTANGLE, r.x, r.y, r.x + r.width, r.y + r.height));
        }

        latexRectangles.forEach(result::append);
        latexNodes.forEach(result::append);
        result.append(latexEdges);

        return result.toString();
    }
}