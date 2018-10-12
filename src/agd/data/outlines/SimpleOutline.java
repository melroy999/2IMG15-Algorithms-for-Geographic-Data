package agd.data.outlines;

import agd.math.Point2d;

import java.util.*;

import static agd.data.outlines.Edge.*;

/**
 * Create a simple outline, which is not allowed to have notches. I.e., gaps have to be filled up by an edge.
 */
public class SimpleOutline extends AbstractOutline implements Insertable {
    // The bounding edges of the outline, for each direction.
//    private final EnumMap<Direction, Edge> bounds = new EnumMap<>(Direction.class);

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public SimpleOutline(OutlineRectangle rectangle) {
        super(rectangle);
        // Create an outline for the new rectangle.
//        Map<Direction, Edge> rectangleEdges = rectangle.createOutlineMap();
//        rectangleEdges.forEach(bounds::put);
    }

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public SimpleOutline(OutlineRectangle rectangle, List<OutlineRectangle> rectangles) {
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

                    // Resolve potential simplicity issues.
                    resolveBackwards(n0);
                    break;
                case AFTER:
                    // We have to cut short a0.
                    a0.setNext(b0.getNext());

                    // Resolve potential simplicity issues.
                    resolveBackwards(a0.getNext());
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

                    // Resolve potential simplicity issues.
                    resolveForwards(n1.getPrevious());
                    break;
                case AFTER:
                    // We have to cut short b0.
                    b0.setNext(a0next);

                    // Resolve potential simplicity issues.
                    resolveForwards(b0);
                    break;
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

        // Add the rectangle to the list of rectangles.
        addRectangle(rectangle);

        double oy = getEdge().getOrigin().y;
        double ny = rectangleEdges.get(Direction.LEFT).getOrigin().y;

        // Do we need to change our access point?
        if(Math.abs(oy - ny) < 1e-4) {
            // They are on the same height. Check if the new edge is more to the right.
            if(getEdge().getOrigin().x < rectangleEdges.get(Direction.LEFT).getOrigin().x) {
                setEdge(rectangleEdges.get(Direction.LEFT));
            }
        } else if(oy > ny) {
            // The edge is definitely placed lower. Set it as an access point.
            setEdge(rectangleEdges.get(Direction.LEFT));
        }
    }

    /**
     * Resolve non-simplistic patterns, by moving backwards in counter-clockwise order.
     *
     * @param edge The edge to start the resolution from.
     */
    private void resolveBackwards(Edge edge) {
//        if(true) return;

        // What is the direction we start at?
        Direction targetDirection = edge.getDirection().opposite();
        Direction terminateDirection = edge.getDirection();

        // The current closest candidate for a pair.
        Edge candidate = null;

        // Move backwards on the chain until we find an edge that:
        // - is moving in the opposite direction;
        // - which has an origin above the target of e.
        // We should terminate when no such edge is found before a turn opposite to the direction of the previous.
        Iterator<Edge> reverse = edge.reverseIterator();
        reverse.next();
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
                    Edge n = new Edge(point, edge.getPrevious().getDirection());

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
        if(candidate == null) {
            // We found no candidate. We are good.
            return;
        }

        Point2d point = candidate.getPrevious().getIntersection(edge);
        Edge n = new Edge(point, edge.getDirection());

        n.setPrevious(candidate.getPrevious());
        n.setNext(edge.getNext());
    }

    /**
     * Resolve non-simplistic patterns, by moving forward in clockwise order.
     *
     * @param edge The edge to start the resolution from.
     */
    private void resolveForwards(Edge edge) {
//        if(true) return;

        // What is the direction we start at?
        Direction targetDirection = edge.getDirection().opposite();
        Direction terminateDirection = edge.getDirection();

        // The current closest candidate for a pair.
        Edge candidate = null;

        // Move forwards on the chain until we find an edge that:
        // - is moving in the opposite direction;
        // - which has an origin above the target of e.
        // We should terminate when no such edge is found (which is when we are back at the direction of our edge).
        for(Edge next : edge.getNext()) {
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
        if(candidate == null) {
            // We found no candidate. We are good.
            return;
        }

        Point2d point = candidate.getNext().getIntersection(edge);
        Edge n = new Edge(point, edge.getNext().getDirection());

        n.setPrevious(edge);
        n.setNext(candidate.getNext().getNext());
    }
}
