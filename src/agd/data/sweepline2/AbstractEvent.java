package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Set;

/**
 * Representation of the events in the sweep line algorithm.
 */
public abstract class AbstractEvent implements Comparable<AbstractEvent> {

    // The type of the event.
    private final EventType type;

    /**
     * Create a new abstract event of the given type.
     *
     * @param type The type of the event, which should be constant.
     */
    public AbstractEvent(EventType type) {
        this.type = type;
    }

    /**
     * Resolve the event using the sweep line data.
     *
     * @param events The current queue of sweep line events.
     * @param status The status of the sweep line.
     * @param intersections The set of currently found intersections.
     */
    public abstract void resolve(PriorityQueue<AbstractEvent> events, SweepStatus status, Set<Pair<OutlineEdge, OutlineEdge>> intersections);

    /**
     * Get the point the event ordering should be based on.
     *
     * @return The relative locations at which the event should be fired.
     */
    public abstract Point2d getPoint();

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
    public int compareTo(AbstractEvent o) {
        Point2d a = getPoint();
        Point2d b = o.getPoint();

        if(Math.abs(a.x - b.x) < 1e-4) {
            if(Math.abs(a.y - b.y) < 1e-4) {
                // Suppose that x and y are both equal.
                // In such a case, we want to base our order on the event type.
                // The order is as follows: LE < I < RE.
                return type.compareTo(o.type);
            } else {
                return Double.compare(a.y, b.y);
            }
        } else {
            return Double.compare(a.x, b.x);
        }
    }

    /**
     * The different types of events that may occur.
     */
    public enum EventType {
        LE, I, RE
    }
}
