package agd.data.outline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class that represents an outline of a number of adjacent rectangles.
 */
public class Outline {
    // Access point to one of the edges in the outline.
    private OutlineEdge edge;

    // The rectangles within the outline.
    public ArrayList<OutlineRectangle> rectangles = new ArrayList<>();

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public Outline(OutlineRectangle rectangle) {
        // A left directional edge in a rectangle can only be the bottom edge.
        edge = rectangle.createOutline(OutlineEdge.Direction.LEFT);
        rectangle.setOutline(this);
        rectangles.add(rectangle);
    }

    /**
     * A constructor used for testing purposes.
     *
     * @param edge The root of the outline.
     */
    public Outline(OutlineEdge edge) {
        this.edge = edge;
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
     * Insert the given rectangle into the outline.
     *
     * @param rectangle The rectangle to insert into the outline.
     */
    public void insert(OutlineRectangle rectangle) {
        // TODO insertion

        // First, find all of the edges that touch the sides of the rectangle.
        // Note that an edge may only touch edges of the opposite direction.
        OutlineEdge rectangleEdge = rectangle.createOutline(OutlineEdge.Direction.LEFT);
        Map<OutlineEdge.Direction, OutlineEdge> mapping = rectangleEdge.toList().stream().collect(Collectors.toMap(OutlineEdge::getDirection, e -> e));



        rectangle.setOutline(this);
        rectangles.add(rectangle);
    }

    private static final String LATEX_POINT = "\\node[circle,fill,red,inner sep=1pt] (v%d) at (%f, %f) {};\n";
    private static final String LATEX_RECTANGLE = "\\draw (%d, %d) rectangle (%d, %d);\n";

    /**
     * Convert the outline to a figure in latex.
     *
     * @return A string representing a tikz figure in latex.
     */
    public String toLatexFigure() {
        List<OutlineEdge> edges = edge.toList();

        // Draw the edges and nodes.
        List<String> latexNodes = new ArrayList<>();
        StringBuilder latexEdges = new StringBuilder("\\draw[red] ");
        for(int i = 0; i < edges.size(); i++) {
            OutlineEdge e = edges.get(i);
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
        result.append("\\begin{tikzpicture}[x=5mm, y=5mm, baseline]\n");
        result.append("\\tikz {\n");
        latexRectangles.forEach(result::append);
        latexNodes.forEach(result::append);
        result.append(latexEdges);
        result.append("}\n");
        result.append("\\end{tikzpicture}");

        return result.toString();
    }
}
