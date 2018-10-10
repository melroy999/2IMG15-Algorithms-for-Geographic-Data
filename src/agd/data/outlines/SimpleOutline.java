package agd.data.outlines;

import agd.math.Point2d;

import java.util.*;

import static agd.data.outlines.Edge.*;

/**
 * Create a simple outline, which is not allowed to have notches. I.e., gaps have to be filled up by an edge.
 */
public class SimpleOutline extends AbstractOutline implements Insertable {
    // The bounding edges of the outline, for each direction.
    private final EnumMap<Direction, Edge> bounds = new EnumMap<>(Direction.class);

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public SimpleOutline(OutlineRectangle rectangle) {
        super(rectangle);
        // Create an outline for the new rectangle.
        Map<Direction, Edge> rectangleEdges = rectangle.createOutlineMap();
        rectangleEdges.forEach(bounds::put);
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
        List<Edge> touching = new ArrayList<>();
        for(Edge edge : this) {
            // Find the edge that is opposing the given edge.
            Direction direction = edge.getDirection();
            Edge target = rectangleEdges.get(direction.opposite());

            // Check if the edge and the target edge touch.
            if(edge.doTouch(target)) {
                touching.add(target);
            }
        }

        if(touching.size() == 1) {
            Edge a0 = touching.get(0);

            // We only touch one edge. Is this the direction-most edge?
            if(a0 != bounds.get(a0.getDirection())) {
                // We are about to place the new rectangle on the direction-most edge.
            } else {
                //
            }


        } else if(touching.size() == 2) {
            // We know that the edges follow one another. Thus, we only have to look at two points.
            Edge a0 = touching.get(0);
            Edge b0 = rectangleEdges.get(a0.getDirection().opposite());
            Relative r0 = a0.getRelativePosition(b0.getTarget());

            Edge a1 = touching.get(1);
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

                    // Resolve potential simplicity issues.
                    resolveBackwards(n0);
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

                    // Resolve potential simplicity issues.
                    resolveForwards(b1);
                    break;
            }
        } else if(touching.isEmpty()) {
            throw new IllegalArgumentException("The rectangle does not touch the outline.");
        } else {
            throw new IllegalArgumentException("The rectangle touches too many edges.");
        }

        // TODO make sure that the outline is "simple" through a cleaning process.
        // TODO set the edge as the new maximum, obviously...
        // This should be quite straightforward: we just propagate to each side until we reach one of the borders.


        addRectangle(rectangle);
    }

    /**
     * Resolve non-simplistic patterns, by moving backwards in counter-clockwise order.
     *
     * @param edge The edge to start the resolution from.
     */
    private void resolveBackwards(Edge edge) {
        // What is the direction we start at?
        Direction targetDirection = edge.getDirection().opposite();
        Direction terminateDirection = edge.getDirection();

        // The current closest candidate for a pair.
        Edge candidate = null;

        // Move backwards on the chain until we find an edge that:
        // - is moving in the opposite direction;
        // - which has an origin above the target of e.
        // We should terminate when no such edge is found before a turn opposite to the direction of the previous.
        Iterator<Edge> reverse = reverseIterator();
        while(reverse.hasNext()) {
            Edge previous = reverse.next();

            // If we encounter a termination direction, break and find the last visited edge.
            if(previous.getDirection() == terminateDirection) {
                break;
            }

            // If we encounter an edge in the desired direction.
            if(previous.getDirection() == targetDirection) {
                // Find the point on the line corresponding to our target.
                Point2d point = previous.getIntersection(edge.getNext());

                // Where is our target point relative to the origin of previous?
                Relative r = previous.getRelativePosition(point);

                // If relative reports after, we have found the edge to contract.
                if(r == Relative.AFTER) {
                    // Project the target onto previous and create a new edge.
                    Edge n = new Edge(point, edge.getDirection());

                    n.setPrevious(previous);
                    n.setNext(edge.getNext().getNext());
                    return;
                }

                if(r == Relative.ON) {
                    // We don't need to create a new edge.
                    previous.getPrevious().setNext(edge.getNext().getNext());
                    return;
                }

                // Set a pointer to the last encountered correct directional edge.
                candidate = previous;
            }
        }

        // If we reach this point, we know that the next of the edge is a direction-maximum.
        // We know that we will have to flip the direction of the candidate.
        assert candidate != null;
        Point2d point = candidate.getPrevious().getIntersection(edge);
        Edge n = new Edge(point, edge.getDirection());

        n.setPrevious(candidate.getPrevious());
        n.setNext(edge.getNext());

        // TODO set the edge as the new maximum, obviously...
    }

    /**
     * Resolve non-simplistic patterns, by moving forward in clockwise order.
     *
     * @param edge The edge to start the resolution from.
     */
    private void resolveForwards(Edge edge) {
        // What is the direction we start at?
        Direction targetDirection = edge.getDirection().opposite();
        Direction terminateDirection = edge.getDirection();

        // The current closest candidate for a pair.
        Edge candidate = null;

        // Move forwards on the chain until we find an edge that:
        // - is moving in the opposite direction;
        // - which has an origin above the target of e.
        // We should terminate when no such edge is found (which is when we are back at the direction of our edge).
        for(Edge next : this) {
            // If we encounter a termination direction, break and find the last visited edge.
            if(next.getDirection() == terminateDirection) {
                break;
            }

            // If we encounter an edge in the desired direction.
            if(next.getDirection() == targetDirection) {
                // Find the point on the line corresponding to our target.
                Point2d point = next.getIntersection(edge.getPrevious());

                // The actual point we should take relatively (i.e. it lies on edge).
                Point2d point2 = next.getNext().getIntersection(edge);

                // Is the target point of next past our line? (in this case, BEFORE).
                Relative r = edge.getRelativePosition(point2);

                // If relative reports before, we have found the edge to contract.
                if(r == Relative.BEFORE) {
                    // Create the new required edge in the direction of next.
                    Edge n = new Edge(point, next.getDirection());

                    n.setPrevious(edge.getPrevious());
                    n.setNext(next.getNext());
                    return;
                }

                if(r == Relative.ON) {
                    // We don't need to create a new edge.
                    edge.getPrevious().setNext(next.getNext().getNext());
                    return;
                }

                // Set a pointer to the last encountered correct directional edge.
                candidate = next;
            }
        }

        // If we reach this point, we know that the next of the edge is a direction-maximum.
        // We know that we will have to flip the direction of the candidate.
        assert candidate != null;
        Point2d point = candidate.getNext().getIntersection(edge);
        Edge n = new Edge(point, edge.getDirection().opposite());

        n.setPrevious(edge.getPrevious());
        n.setNext(candidate.getNext());
        // TODO set the edge as the new maximum, obviously...
    }
}
