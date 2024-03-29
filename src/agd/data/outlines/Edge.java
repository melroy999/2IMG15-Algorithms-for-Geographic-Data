package agd.data.outlines;

import agd.math.Point2d;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An edge data structure for the outline, which has pointers to the previous and next edges.
 */
public class Edge implements Iterable<Edge>, Comparable<Edge> {
    // An unique identifier for an edge, used within equality checks.
    private static int ID_COUNTER = 0;
    private final int id;

    // The origin point of the edge.
    private final Point2d origin;

    // The direction of the edge.
    private Direction direction;

    // Points to the previous and next edges.
    private Edge next, previous;

    /**
     * Create an outline edge with the given origin and direction.
     *
     * @param origin The origin point of the edge.
     * @param direction The direction of the edge.
     */
    public Edge(Point2d origin, Direction direction) {
        this.id = ID_COUNTER++;
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * Get the id of the edge.
     *
     * @return The id of an edge represented by an integer.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the origin position of the edge.
     *
     * @return The origin position of the edge, which is a pair of doubles.
     */
    public Point2d getOrigin() {
        return origin;
    }

    /**
     * Get the target position of the edge.
     *
     * @return The target position of the edge, which is a pair of doubles. Returns null if there is no target.
     */
    public Point2d getTarget() {
        return next == null ? null : next.origin;
    }

    /**
     * Get the direction of the edge.
     *
     * @return The direction of the edge, which is in {LEFT, UP, RIGHT, DOWN}.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Get the edge that preceded this edge.
     *
     * @return The previous edge.
     */
    public Edge getPrevious() {
        return previous;
    }

    /**
     * Set the edge that preceded this edge and maintain the opposing next pointer.
     *
     * @param previous The edge that should be considered the previously encountered edge.
     */
    public void setPrevious(Edge previous) {
        this.previous = previous;
        previous.next = this;

        // Is the direction still valid?
        Direction dir = Direction.getDirection(previous.origin, origin);
        if(dir != previous.direction && previous.length() > 1e-4) {
            previous.direction = dir;
        }
    }

    /**
     * Get the edge that succeeded this edge.
     *
     * @return The next edge.
     */
    public Edge getNext() {
        return next;
    }

    /**
     * Set the edge that succeeded this edge and maintain the opposing previous pointer.
     *
     * @param next The edge that should be considered the next encountered edge.
     */
    public void setNext(Edge next) {
        this.next = next;
        next.previous = this;

        // Is the direction still valid?
        Direction dir = Direction.getDirection(origin, next.origin);
        if(dir != direction && length() > 1e-4) {
            direction = dir;
        }
    }

    /**
     * Get the length of the edge.
     *
     * @return The euclidean distance between the origin and target points. Maximum double value if target is not set.
     */
    public double length() {
        if(next == null) {
            return Double.MAX_VALUE;
        } else {
            return origin.distance(getTarget());
        }
    }

    /**
     * Project the given point onto the line segment.
     *
     * @param p The point to project on the line segment.
     * @return A point on the line segment that has the shortest euclidean distance to p.
     */
    public Point2d project(Point2d p) {
        // Find the bounds of the projection.
        double xmin = Math.min(getOrigin().x, getTarget().x);
        double xmax = Math.max(getOrigin().x, getTarget().x);
        double ymin = Math.min(getOrigin().y, getTarget().y);
        double ymax = Math.max(getOrigin().y, getTarget().y);

        // Make sure that the point is a half-width point. I.e., round to the nearest half.
        Point2d target = new Point2d(Math.max(xmin, Math.min(xmax, p.x)), Math.max(ymin, Math.min(ymax, p.y)));

        // Project the point.
        return new Point2d(Math.round(target.x * 2) / 2.0, Math.round(target.y * 2) / 2.0);
    }

    /**
     * Find the intersection point of two edges.
     *
     * @param conflict An edge perpendicular to this edge.
     * @return The intersection point of the two edges.
     */
    public Point2d getIntersection(Edge conflict) {
        // Based on the orientation of the edge, find the correct intersection point.
        if(direction.isHorizontal) {
            return new Point2d(conflict.origin.x, origin.y);
        } else {
            return new Point2d(origin.x, conflict.origin.y);
        }
    }

    public boolean doIntersect(Edge conflict) {
        Point2d i = getIntersection(conflict);

        // The point is on this edge.
        if(getOrigin().distance(i) + i.distance(getTarget()) <= length()) {
            // The point is on the conflict edge.
            if(conflict.getOrigin().distance(i) + i.distance(conflict.getTarget()) <= conflict.length()) {
                // Is the intersection valid?
                if(direction == conflict.direction) {
                    // If they have the same direction, it always is.
                    return true;
                } else if(direction != conflict.direction.opposite()) {
                    // The direction is on a different axis. We don't want any endpoint to coincide with i.
                    return !(i.epsilonEquals(getOrigin(), 1e-4)
                            || i.epsilonEquals(getTarget(), 1e-4)
                            || i.epsilonEquals(conflict.getOrigin(), 1e-4)
                            || i.epsilonEquals(conflict.getTarget(), 1e-4));
                }
            }
        }

        return false;
    }

    /**
     * Check whether two edges in opposite direction touch one another.
     *
     * @param conflict The potential touching edge.
     * @return True if the two lines share a line segment, false otherwise.
     */
    public boolean doTouch(Edge conflict) {
        // Are they upon the same line?
        if(direction.isHorizontal) {
            if(Math.abs(origin.y - conflict.origin.y) > 1e-4) return false;
        } else {
            if(Math.abs(origin.x - conflict.origin.x) > 1e-4) return false;
        }

        Point2d diff1, diff2;

        // Note that we should include endpoints here, since we could only have the corner points in common.
        if(direction == Direction.RIGHT || direction == Direction.UP) {
            /*
             * This edge has the leftmost/bottommost point.
             * We have the following options:
             *
             * this.origin -------- this.target              conflict.target -------- conflict.origin
             *
             * this.origin -------- conflict.target -------- this.target -------- conflict.origin
             *
             * conflict.target -------- conflict.origin      this.origin -------- this.target
             */

            // Check if the distance between the two points in the plane of choice is positive.
            diff1 = getTarget().sub(conflict.getTarget());
            diff2 = conflict.getOrigin().sub(getOrigin());
        } else {
            /*
             * This edge has the rightmost/topmost point.
             * We have the following options:
             *
             * conflict.origin -------- conflict.target      this.target -------- this.origin
             *
             * conflict.origin -------- this.target -------- conflict.target -------- this.origin
             *
             * this.target -------- this.origin              conflict.origin -------- conflict.target
             */
            diff1 = conflict.getTarget().sub(getTarget());
            diff2 = getOrigin().sub(conflict.getOrigin());
        }
        return direction.isHorizontal ? diff1.x > -1e-4 && diff2.x > 1e-4 : diff1.y > -1e-4 && diff2.y > 1e-4;
    }

    /**
     * Get the position of a point p relative to the origin, following the direction of the edge.
     *
     * @param p The point to check the relative position of.
     * @return ON if the points are equal, LEFT or RIGHT otherwise, depending on the position.
     */
    public Relative getRelativePosition(Point2d p) {
        if(origin.epsilonEquals(p, 1e-4)) {
            return Relative.ON;
        } else {
            // Note that a point is right of the origin if it is past the origin on the line.
            switch (direction) {
                case RIGHT: return origin.x < p.x ? Relative.AFTER : Relative.BEFORE;
                case LEFT: return origin.x > p.x ? Relative.AFTER : Relative.BEFORE;
                case UP: return origin.y < p.y ? Relative.AFTER : Relative.BEFORE;
                case DOWN:
                default: return origin.y > p.y ? Relative.AFTER : Relative.BEFORE;
            }
        }
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Edge> iterator() {
        return new Iterator<Edge>() {
            // The edge that we started our iteration cycle with.
            Edge start = Edge.this;

            // The current edge that we are observing.
            Edge current = null;

            // Infinite looping protection. We can never do more iterations than there are edges.
            int i = 0;

            @Override
            public boolean hasNext() {
                return current == null || current.next != start;
            }

            @Override
            public Edge next() {
                if(current == null) {
                    current = start;
                } else {
                    current = current.next;
                }

                // Increment i, and check whether we have exceeded the unique id counter.
                if(i > Edge.ID_COUNTER) {
                    throw new RuntimeException("The iterator has detected an infinite loop.");
                }
                i++;

                return current;
            }

            @Override
            public void remove(){
                throw new UnsupportedOperationException("Remove not implemented.");
            }
        };
    }

    /**
     * Returns an iterator over elements of type {@code T} that iterates in reverse direction.
     *
     * @return an Iterator.
     */
    public Iterator<Edge> reverseIterator() {
        return new Iterator<Edge>() {
            // The edge that we started our iteration cycle with.
            Edge start = Edge.this;

            // The current edge that we are observing.
            Edge current = null;

            // Infinite looping protection. We can never do more iterations than there are edges.
            int i = 0;

            @Override
            public boolean hasNext() {
                return current == null || current.previous != start;
            }

            @Override
            public Edge next() {
                if(current == null) {
                    current = start;
                } else {
                    current = current.previous;
                }

                // Increment i, and check whether we have exceeded the unique id counter.
                i++;
                if(i > Edge.ID_COUNTER) {
                    throw new RuntimeException("The iterator has detected an infinite loop.");
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
     * Get the collection of edges in the form of a list.
     *
     * @return A list containing all the edges in clockwise order.
     */
    public List<Edge> toList() {
        List<Edge> list = new ArrayList<>();
        iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return id == edge.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return id + " " + origin + "-" + direction + "->" + (next == null ? "null" : next.origin.toString());
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Edge o) {
        return Integer.compare(id, o.id);
    }

    /**
     * Relative position of a point to the origin, following the direction of the line.
     */
    public enum Relative {
        BEFORE, ON, AFTER
    }

    /**
     * The direction which the edge is moving in.
     */
    public enum Direction {
        LEFT(true), UP(false), RIGHT(true), DOWN(false);

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

        public static Direction getDirection(Point2d p1, Point2d p2) {
            if(p1.x < p2.x) {
                return RIGHT;
            } else if(p1.x > p2.x) {
                return LEFT;
            } else {
                if(p1.y < p2.y) {
                    return UP;
                } else {
                    return DOWN;
                }
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
         * The direction left of the current direction.
         *
         * @return The direction to the left.
         */
        public Direction left() {
            //noinspection Duplicates
            switch (this) {
                case UP: return LEFT;
                case RIGHT: return UP;
                case DOWN: return RIGHT;
                default: return DOWN;
            }
        }

        /**
         * The direction right of the current direction.
         *
         * @return The direction to the right.
         */
        public Direction right() {
            //noinspection Duplicates
            switch (this) {
                case UP: return RIGHT;
                case DOWN: return DOWN;
                case LEFT: return LEFT;
                default: return UP;
            }
        }
    }
}
