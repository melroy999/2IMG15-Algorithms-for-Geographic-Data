package agd.data.outline;

import java.util.*;
import java.util.stream.Collectors;

import static agd.data.outline.OutlineEdge.*;

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
        edge = rectangle.createOutline(Direction.LEFT);
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
        // First, find all of the edges that touch the sides of the rectangle.
        // Note that an edge may only touch edges of the opposite direction.
        OutlineEdge rectangleEdge = rectangle.createOutline(Direction.LEFT);
        Map<Direction, OutlineEdge> mapping = rectangleEdge.toList().stream().collect(Collectors.toMap(OutlineEdge::getDirection, e -> e));
        Map<Direction, List<OutlineEdge>> touchMap = findTouchingEdges(mapping);

        // Notes:
        // - One single edge should never touch two consecutive edges, since these consecutive edges should have been merged beforehand.


        for(Direction d : Direction.values()) {
            // The edge we are trying to add to the shape.
            OutlineEdge e1 = mapping.get(d);

            // Resolve all conflicted edges.
            for(OutlineEdge e2 : touchMap.get(d)) {
                // Keep pointers to the next of each line.
                OutlineEdge next1 = e1.getNext();
                OutlineEdge next2 = e2.getNext();

                // What is the relative position of e2.t compared to e1.o?
                Relative r1 = e1.getRelativePositionToOrigin(e2.getTarget());
                correctByRelativePlacement(e1, e2, next2, r1);

                // What is the relative position of e1.t compared to e2.o?
                Relative r2 = e2.getRelativePositionToOrigin(e1.getTarget());
                correctByRelativePlacement(e2, e1, next1, r2);
            }
        }

        // Make sure that we have the correct access point to our cycle.
        if(edge.getOrigin().y > rectangleEdge.getOrigin().y) {
            edge = rectangleEdge;
        } else {
            // We might have merged the two if the access edge is equal.
            // In general, we would always like the access point to be the rightmost left pointed edge.
            if(Math.abs(edge.getOrigin().y - rectangleEdge.getOrigin().y) < 1e-4) {
                // What is the position of our new edge relative to the previous access point?
                if(edge.getOrigin().x <= rectangleEdge.getOrigin().x) {
                    edge = rectangleEdge;
                }
            }
        }

        // Merge edges that can be merged.
        mergeEdges();

        // Set references to the correct outline.
        rectangle.setOutline(this);
        rectangles.add(rectangle);
    }

    /**
     * Correct the pointers by the relative placement of the target point on the other edge.
     *
     * @param e1 The edge of which we take the origin to get the relative position of a point to.
     * @param e2 The edge of which we take the target position, checking for its relative position.
     * @param next2 A change-safe pointer to the next pointer of the edge e2.
     * @param r1 The relative position of e2.t to e1.o.
     */
    private void correctByRelativePlacement(OutlineEdge e1, OutlineEdge e2, OutlineEdge next2, Relative r1) {
        switch (r1) {
            case LEFT:
                OutlineEdge eNew = new OutlineEdge(e1.getOrigin(), e2.getDirection());
                eNew.setNext(next2);
                e1.getPrevious().setNext(eNew);
                break;
            case ON:
                e1.getPrevious().setNext(next2);
                break;
            case RIGHT:
                e1.setNext(next2);
                break;
        }
    }

    /**
     * Merge edges that have the same direction and follow one another.
     */
    private void mergeEdges() {
        // Make sure that we do not have two consecutive edges in the same direction; they should be merged otherwise.
        List<OutlineEdge> edges = edge.toList();
        OutlineEdge prev = edges.get(0);

        for(int i = 0; i < edges.size(); i++) {
            OutlineEdge e2 = edges.get((i + 1) % edges.size());

            if(prev.getDirection() == e2.getDirection()) {
                // Merge the two directions.
                prev.setNext(e2.getNext());

                // Be careful if edges.size() - 1 == i, since we might have removed our handle!
                if(i == edges.size() - 1) {
                    // Set e1 as our handle instead.
                    edge = prev;
                }

                // Skip ahead one iteration.
                i++;
            } else {
                // Set the new previous.
                prev = e2;
            }
        }
    }

    /**
     * Find all the edges that touch a side of the given rectangle sides.
     *
     * @param sides The collection of outline edges surrounding the rectangle.
     * @return A mapping that maps a side of the rectangle to the edges that touch it.
     */
    private Map<Direction, List<OutlineEdge>> findTouchingEdges(Map<Direction, OutlineEdge> sides) {
        // Note that the key corresponds to the side of the rectangle, not to the side of the conflicting edge!
        Map<Direction, List<OutlineEdge>> result = new HashMap<>();
        Arrays.stream(Direction.values()).forEach(d -> result.put(d, new ArrayList<>()));

        for(OutlineEdge e : edge) {
            Direction rectangleDirection = e.getDirection().opposite();

            OutlineEdge rectangleEdge = sides.get(rectangleDirection);
            if(rectangleEdge.doTouch(e)) {
                // if they do touch, add the edge to the mapping.
                result.get(rectangleDirection).add(e);
            }
        }

        return result;
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
