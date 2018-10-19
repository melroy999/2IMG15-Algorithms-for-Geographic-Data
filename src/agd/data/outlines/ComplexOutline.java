package agd.data.outlines;

import agd.math.Point2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static agd.data.outlines.Edge.Direction;
import static agd.data.outlines.Edge.Relative;

/**
 * Create a simple outline, which is not allowed to have notches. I.e., gaps have to be filled up by an edge.
 */
public class ComplexOutline extends AbstractOutline implements Insertable {
    // The bounding edges of the outline, for each direction.
//    private final EnumMap<Direction, Edge> bounds = new EnumMap<>(Direction.class);

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public ComplexOutline(OutlineRectangle rectangle) {
        super(rectangle);
    }

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public ComplexOutline(OutlineRectangle rectangle, List<OutlineRectangle> rectangles) {
        super(rectangle);

        // Remove the rectangle that we just added.
        this.getRectangles().clear();

        // Add all the rectangles.
        this.getRectangles().addAll(rectangles);
        rectangles.forEach(r -> r.setOutline(this));
    }

    /**
     * Insert the given rectangle into the outline.
     *
     * @param rectangle The rectangle to insert into the outline.
     */
    @Override
    public void insert(OutlineRectangle rectangle) {
        // Create an outline for the new rectangle.
        Map<Direction, Edge> rectangleEdges = rectangle.createOutlineMap();

        // We know that the rectangle is placed against at most two existing edges. Find those edges.
        List<Edge> touching = getTouchingEdges(rectangleEdges);

        if(touching.size() == 1) {
            // Get the single edge that we touch.
            Edge a0 = touching.get(0);

            // Get the edge on the opposite side of our rectangle.
            Edge b0 = rectangleEdges.get(a0.getDirection().opposite());

            // Check relative positions for both sides.
            Relative r0 = a0.getRelativePosition(b0.getTarget());
            Relative r1 = b0.getRelativePosition(a0.getTarget());

            // The nexts of both lines.
            Edge a0next = a0.getNext();

            switch(r0) {
                case ON:
                    // We have to merge a0.previous and b0.next.
                    a0.getPrevious().setNext(b0.getNext().getNext());
                    break;
                case BEFORE:
                    // We have to create a new edge in the same direction as b0.
                    Edge n0 = new Edge(a0.getOrigin(), b0.getDirection());

                    n0.setNext(b0.getNext());
                    n0.setPrevious(a0.getPrevious());
                    break;
                case AFTER:
                    // We have to cut short a0.
                    a0.setNext(b0.getNext());
                    break;
            }

            switch (r1) {
                case ON:
                    // We have to merge b0.previous and a0.next.
                    b0.getPrevious().setNext(a0next.getNext());
                    break;
                case BEFORE:
                    // We have to create a new edge in the same direction as a1.
                    Edge n1 = new Edge(b0.getOrigin(), a0.getDirection());
                    n1.setNext(a0next);
                    n1.setPrevious(b0.getPrevious());
                    break;
                case AFTER:
                    // We have to cut short b0.
                    b0.setNext(a0next);
                    break;
            }


        } else if(touching.size() > 1) {
            // We know that the edges follow one another. Thus, we only have to look at two points.
            Edge a0 = touching.get(0);
            Edge b0 = rectangleEdges.get(a0.getDirection().opposite());
            Relative r0 = a0.getRelativePosition(b0.getTarget());

            Edge a1 = touching.get(touching.size() - 1);
            Edge b1 = rectangleEdges.get(a1.getDirection().opposite());
            Relative r1 = b1.getRelativePosition(a1.getTarget());

            //noinspection Duplicates
            switch(r0) {
                case ON:
                    // We have to merge a0.previous and b0.next.
                    a0.getPrevious().setNext(b0.getNext().getNext());
                    break;
                case BEFORE:
                    // We have to create a new edge in the same direction as b0.
                    Edge n0 = new Edge(a0.getOrigin(), b0.getDirection());
                    n0.setNext(b0.getNext());
                    n0.setPrevious(a0.getPrevious());
                    break;
                case AFTER:
                    // We have to cut short a0.
                    a0.setNext(b0.getNext());
                    break;
            }

            //noinspection Duplicates
            switch(r1) {
                case ON:
                    // We have to merge b1.previous and a1.next.
                    b1.getPrevious().setNext(a1.getNext().getNext());
                    break;
                case BEFORE:
                    // We have to create a new edge in the same direction as a1.
                    Edge n1 = new Edge(b1.getOrigin(), a1.getDirection());
                    n1.setNext(a1.getNext());
                    n1.setPrevious(b1.getPrevious());
                    break;
                case AFTER:
                    // We have to cut short b1.
                    b1.setNext(a1.getNext());
                    break;
            }
        } else {
            // TODO if it does not touch the outline, we should consider it an internal rectangle.
            //throw new IllegalArgumentException("The rectangle does not touch the outline.");
        }

        // Add the rectangle to the list of rectangles.
        addRectangle(rectangle);

        double oy = getEdge().getOrigin().y;
        double ny = rectangleEdges.get(Direction.LEFT).getOrigin().y;

        // Do we need to change our access point?
        updateAccessPoint(rectangleEdges, oy, ny);
    }

    private List<Edge> getTouchingEdges(Map<Direction, Edge> rectangleEdges) {
        List<Edge> touching = new ArrayList<>();
        for(Edge edge : this) {
            // Find the edge that is opposing the given edge.
            Direction direction = edge.getDirection();
            Edge target = rectangleEdges.get(direction.opposite());

            // Check if the edge and the target edge touch.
            if(edge.doTouch(target)) {
                touching.add(edge);
            }
        }
        return touching;
    }
}
