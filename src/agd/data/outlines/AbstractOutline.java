package agd.data.outlines;

import agd.data.outlines.Edge.Direction;

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

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public AbstractOutline(OutlineRectangle rectangle) {
        // A left directional edge in a rectangle can only be the bottom edge.
        edge = rectangle.createOutline(Direction.LEFT);
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

    /**
     * Convert the outline to a figure in latex.
     *
     * @return A string representing a tikz figure in latex.
     */
    public String toLatexFigure() {
        List<Edge> edges = edge.toList();

        // Draw the edges and nodes.
        List<String> latexNodes = new ArrayList<>();
        StringBuilder latexEdges = new StringBuilder("\\draw[red] ");
        for(int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            latexNodes.add(String.format(LATEX_POINT, i, e.getOrigin().x, e.getOrigin().y));
            latexEdges.append("(v").append(i).append(") -- ");
        }
        latexEdges.append("(v").append(0).append(");\n");

        // Draw the rectangles.
        List<String> latexRectangles = new ArrayList<>();
        for(OutlineRectangle r : rectangles) {
            latexRectangles.add(String.format(LATEX_RECTANGLE, r.x, r.y, r.x + r.width, r.y + r.height));
        }

        // Combine everything into one figure.
        StringBuilder result = new StringBuilder();
        result.append("\\resizebox{\\textwidth}{!}{% <------ Don't forget this %\n");
        result.append("\\begin{tikzpicture}[x=5mm, y=5mm, baseline, trim left]\n");
        result.append("\\tikz {\n");
        latexRectangles.forEach(result::append);
        latexNodes.forEach(result::append);
        result.append(latexEdges);
        result.append("}\n");
        result.append("\\end{tikzpicture}\n");
        result.append("}");

        return result.toString();
    }
}
