package agd.data.outline;

import agd.math.Point2d;

import java.util.Iterator;

/**
 * A data structure that consists of edges linked to one another through next and previous pointers.
 */
public class OutlineEdge implements Iterable<OutlineEdge> {
    // The starting point of the edge.
    private final Point2d origin;

    // The direction of the edge.
    private final Direction direction;

    // Pointers to the previous and next edge.
    private OutlineEdge previous, next;

    /**
     * Create an outline edge moving in the given (clockwise ordered) direction.
     *
     * @param origin The origin position of the edge.
     * @param direction The direction of the edge.
     */
    public OutlineEdge(Point2d origin, Direction direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * Get the edge that preceded this edge.
     *
     * @return The previous edge.
     */
    public OutlineEdge getPrevious() {
        return previous;
    }

    /**
     * Set the edge that preceded this edge and maintain the opposing next pointer.
     *
     * @param previous The edge that should be considered the previously encountered edge.
     */
    public void setPrevious(OutlineEdge previous) {
        this.previous = previous;
        previous.next = this;
    }

    /**
     * Get the edge that succeeded this edge.
     *
     * @return The next edge.
     */
    public OutlineEdge getNext() {
        return next;
    }

    /**
     * Set the edge that succeeded this edge and maintain the opposing previous pointer.
     *
     * @param next The edge that should be considered the next encountered edge.
     */
    public void setNext(OutlineEdge next) {
        this.next = next;
        next.previous = this;
    }

    /**
     * Get the starting position of the edge.
     *
     * @return The origin of the edge.
     */
    public Point2d getOrigin() {
        return origin;
    }

    /**
     * Get the end position of the edge.
     *
     * @return Null if the edge is not succeeded by another edge, the origin of the succeeding edge otherwise.
     */
    public Point2d getTarget() {
        return next == null ? null : next.origin;
    }

    /**
     * The travel direction of the edge, which should result in a clockwise ordering.
     *
     * @return The direction of the edge.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Find the intersection point of two edges.
     *
     * @param conflict An edge perpendicular to this edge.
     * @return The intersection point of the two edges.
     */
    private Point2d getIntersection(OutlineEdge conflict) {
        // Based on the orientation of the edge, find the correct intersection point.
        if(direction.isHorizontal) {
            return new Point2d(conflict.origin.x, origin.y);
        } else {
            return new Point2d(origin.x, conflict.origin.y);
        }
    }

    /**
     * Resolve an intersection between two edges by adding a new point and splitting the edges in two.
     *
     * @param conflict The edge that intersects with this edge.
     */
    public void resolveIntersection(OutlineEdge conflict) {
        if(direction.isHorizontal == conflict.direction.isHorizontal) {
            throw new IllegalArgumentException("Intersections between edges with the same direction is unsupported");
        }

        // Find the intersection point.
        Point2d i = getIntersection(conflict);

        // Create the new edges that originate from the intersection point.
        OutlineEdge s1 = new OutlineEdge(i, direction);
        OutlineEdge s2 = new OutlineEdge(i, conflict.direction);

        // Resolve the intersection.
        // Note that the starting segment is always followed by the new segment of the other direction.
        setNext(s2);
        conflict.setNext(s1);
        next.setPrevious(s1);
        conflict.next.setPrevious(s2);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<OutlineEdge> iterator() {
        return new Iterator<OutlineEdge>() {
            // The edge that we started our iteration cycle with.
            OutlineEdge start = OutlineEdge.this;

            // The current edge that we are observing.
            OutlineEdge current = start;

            @Override
            public boolean hasNext() {
                return current.next != start;
            }

            @Override
            public OutlineEdge next() {
                current = current.next;
                return current;
            }

            @Override
            public void remove(){
                throw new UnsupportedOperationException("Remove not implemented.");
            }
        };
    }

    /**
     * Get a reliable access point to the curve that determines the outside of the outline.
     *
     * @return The lowest edge in the plane that is part of the outline.
     */
    public OutlineEdge getLowestEdge() {
        OutlineEdge lowest = null;

        // Iterate over all edges and find the horizontal edge with the lowest y-coordinate.
        for(OutlineEdge e : this) {
            if(e.direction.isHorizontal) {
                if(lowest == null || lowest.origin.y > e.origin.y) {
                    lowest = e;
                }
            }
        }

        return lowest;
    }

    // TODO add new edges based on a neighboring rectangle.

    /**
     * The direction of the outline edge.
     */
    public enum Direction {
        UP(false), DOWN(false), LEFT(true), RIGHT(true);

        // Whether the direction is horizontal or not.
        public final boolean isHorizontal;

        /**
         * Create a direction and set the correct horizontal flag.
         *
         * @param isHorizontal Whether the direction should be considered a horizontal direction.
         */
        Direction(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }

        /**
         * The direction that is opposite to the direction of this direction.
         *
         * @return The opposite direction.
         */
        public Direction opposite() {
            //noinspection Duplicates
            switch (this) {
                case UP: return DOWN;
                case DOWN: return UP;
                case LEFT: return RIGHT;
                default: return LEFT;
            }
        }
    }
}
