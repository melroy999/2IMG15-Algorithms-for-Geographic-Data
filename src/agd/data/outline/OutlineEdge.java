package agd.data.outline;

import agd.data.sweepline2.LineSegment;
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
     * Decide which edge is first, given two edges in the same direction that intersect each other.
     * @param conflict The edge that we should check the relative position of.
     * @return Whether this occurs before the other in the direction of the edges.
     */
    private boolean isFirst(OutlineEdge conflict) {
        switch(this.direction) {
            case RIGHT: return this.origin.x <= conflict.origin.x;
            case LEFT: return this.origin.x >= conflict.origin.x;
            case UP: return this.origin.y <= conflict.origin.y;
            case DOWN:
            default: return this.origin.y >= conflict.origin.y;
        }
    }

    /**
     * Check whether the given edge has an intersection with this edge.
     *
     * @param conflict The edge that potentially intersects this edge.
     * @return Whether there exists an intersection between the two line segments.
     */
    public boolean hasIntersection(OutlineEdge conflict) {
        // What is the relative orientation of the two lines? Are they parallel?
        if(direction.isHorizontal == conflict.direction.isHorizontal) {
            // If they are, we might get the opportunity of eliminating one of the edges if the direction is the same.
            if(direction == conflict.direction) {
                // Do the line segments have an overlap? I.e., is one of the endpoints in between the other?
                double a1, a2, b1, b2;

                // Are they actually on the same height?
                if(direction.isHorizontal) {
                    if(Math.abs(origin.y - conflict.origin.y) > 1e-5) {
                        return false;
                    }
                } else {
                    if(Math.abs(origin.x - conflict.origin.x) > 1e-5) {
                        return false;
                    }

                }

                // Determine the intervals such that a1 <= a2 and b1 <= b2.
                //region interval switch
                switch (direction) {
                    case RIGHT:
                        a1 = origin.x;
                        a2 = next.origin.x;
                        b1 = conflict.origin.x;
                        b2 = conflict.next.origin.x;
                        break;
                    case LEFT:
                        a1 = next.origin.x;
                        a2 = origin.x;
                        b1 = conflict.next.origin.x;
                        b2 = conflict.origin.x;
                        break;
                    case UP:
                        a1 = origin.y;
                        a2 = next.origin.y;
                        b1 = conflict.origin.y;
                        b2 = conflict.next.origin.y;
                        break;
                    case DOWN:
                    default:
                        a1 = next.origin.y;
                        a2 = origin.y;
                        b1 = conflict.next.origin.y;
                        b2 = conflict.origin.y;
                }
                //endregion

                // Check if the intervals overlap.
                return a1 < b2 && b1 < a2;
            }
        } else {
            // Extend the two lines, and find the intersection points of those two lines.
            Point2d i = getIntersection(conflict);
            double a1, a2, b1, b2, a, b;

            // Determine whether we have an intersection by looking at the lengths of the line segments.
            if(direction.isHorizontal) {
                a1 = Math.abs(origin.x - i.x);
                a2 = Math.abs(next.origin.x - i.x);
                a = Math.abs(origin.x - next.origin.x);
                b1 = Math.abs(conflict.origin.y - i.y);
                b2 = Math.abs(conflict.next.origin.y - i.y);
                b = Math.abs(conflict.origin.y - conflict.next.origin.y);
            } else {
                a1 = Math.abs(origin.y - i.y);
                a2 = Math.abs(next.origin.y - i.y);
                a = Math.abs(origin.y - next.origin.y);
                b1 = Math.abs(conflict.origin.x - i.x);
                b2 = Math.abs(conflict.next.origin.x - i.x);
                b = Math.abs(conflict.origin.x - conflict.next.origin.x);
            }

            if(a1 < 1e-5 || a2 < 1e-5 || b1 < 1e-5 || b2 < 1e-5) {
                return false;
            }

            // use triangle inequality to find whether we are on the lines or not.
            return Math.abs(a1 + a2 - a) < 1e-5 && Math.abs(b1 + b2 - b) < 1e-5;
        }

        return false;
    }

    /**
     * Resolve an intersection between two edges by adding a new point and splitting the edges in two.
     *
     * @param conflict The edge that intersects with this edge.
     */
    public void resolveIntersection(OutlineEdge conflict) {
        // Is the intersection between edges of the same orientation?
        if(direction == conflict.direction) {
            System.out.println("Merging " + this + " and " + conflict);
            // We should do a simple merge, eliminating the latter edge.
            if(this.isFirst(conflict)) {
                this.setNext(conflict.next);
                conflict.previous = null;
                conflict.next = null;
                System.out.println("#1 Merged to " + this);
            } else {
                conflict.setNext(this.next);
                this.previous = null;
                this.next = null;
                System.out.println("#2 Merged to " + conflict);
            }
        } else {
            System.out.println("Solving intersection between " + this + " and " + conflict);
            // We have an intersection between horizontal and vertical line segments.
            Point2d i = getIntersection(conflict);
            OutlineEdge s1 = new OutlineEdge(i, direction);
            OutlineEdge s2 = new OutlineEdge(i, conflict.direction);

            // Resolve the intersection.
            // Note that the starting segment is always followed by the new segment of the other direction.
            setNext(s2);
            conflict.setNext(s1);
            next.setPrevious(s1);
            conflict.next.setPrevious(s2);
        }
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
            OutlineEdge current = null;

            @Override
            public boolean hasNext() {
                return current == null || current.next != start;
            }

            @Override
            public OutlineEdge next() {
                if(current == null) {
                    current = start;
                } else {
                    current = current.next;
                }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutlineEdge that = (OutlineEdge) o;

        if (!origin.equals(that.origin)) return false;
        if (direction != that.direction) return false;
        if (previous != null ? !previous.shallowEquals(that.previous) : that.previous != null) return false;
        return next != null ? next.shallowEquals(that.next) : that.next == null;
    }

    private boolean shallowEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutlineEdge that = (OutlineEdge) o;

        return origin.equals(that.origin) && direction == that.direction;
    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + direction.hashCode();
        result = 31 * result + (previous != null ? previous.shallowHashCode() : 0);
        result = 31 * result + (next != null ? next.shallowHashCode() : 0);
        return result;
    }

    private int shallowHashCode() {
        int result = origin.hashCode();
        result = 31 * result + direction.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "" + origin + "-" + direction + "->" + (next == null ? "null" : next.origin.toString());
    }

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

        /**
         * Get the offset directional vector associated with the direction.
         *
         * @return A vector with components length one indicating the direction in which a point should be translated.
         */
        public Point2d bufferVector() {
            switch (this) {
                case UP: return new Point2d(-1, -1);
                case RIGHT: return new Point2d(-1, 1);
                case DOWN: return new Point2d(1, 1);
                default: return new Point2d(1, -1);
            }
        }

        /**
         * Get the offset directional vector associated with a left corner cutoff.
         *
         * @return A vector with components length one indicating the direction in which a point should be translated.
         */
        public Point2d leftTurnVector() {
            switch (this) {
                case UP: return new Point2d(0, 1);
                case RIGHT: return new Point2d(1, 0);
                case DOWN: return new Point2d(0, -1);
                default: return new Point2d(-1, 0);
            }
        }

        /**
         * Check whether the current direction followed by the given direction is a left turn.
         *
         * @param direction The direction to check the relative position of.
         * @return True if the direction moves to the left, false otherwise.
         */
        public boolean isLeftTurn(Direction direction) {
            switch (this) {
                case UP: return direction == LEFT;
                case RIGHT: return direction == UP;
                case DOWN: return direction == RIGHT;
                default: return direction == DOWN;
            }
        }
    }
}
