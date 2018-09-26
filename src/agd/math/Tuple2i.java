package agd.math;

import java.util.Objects;

/**
 * A simple tuple 2d coordinate object.
 */
public abstract class Tuple2i<T> {
    // The x and y-coordinates of the tuple.
    public final int x, y;

    /**
     * Define a tuple by giving an x and y-coordinates.
     *
     * @param x The x-coordinate of the tuple.
     * @param y The y-coordinate of the tuple.
     */
    public Tuple2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Define a tuple with coordinates 0, 0.
     */
    public Tuple2i() {
        this(0, 0);
    }

    /**
     * Add the given vector to this vector and return it as a new vector.
     *
     * @param t The vector we want to take the sum of.
     * @return The sum between this vector and vector t, as a new instance.
     */
    public T add(Tuple2i t) {
        return get(x + t.x, y + t.y);
    }

    /**
     * Subtract the given vector from this vector and return it as a new vector.
     *
     * @param t The vector we want to subtract.
     * @return The subtraction between this vector and vector t, as a new instance.
     */
    public T sub(Tuple2i t) {
        return get(x - t.x, y - t.y);
    }

    /**
     * Scale this tuple by the given amount.
     *
     * @param s The scaling factor.
     * @return The tuple scaled, as a new instance.
     */
    public T scale(int s) {
        return get(s * x, s * y);
    }

    /**
     * Calculate the euclidean distance between this tuple and the given tuple.
     *
     * @param t The tuple we want to measure the euclidean distance to from this tuple.
     * @return The euclidean distance between this tuple and the tuple t.
     */
    public double distance(Tuple2i t) {
        return Math.sqrt(Math.pow(this.x - t.x, 2) + Math.pow(this.y - t.y, 2));
    }

    /**
     * Create a new instance of a tuple2d subclass.
     *
     * @param x The x-coordinate of the tuple.
     * @param y The y-coordinate of the point.
     * @return An instance of the desired type, that is an extension of Tuple2d.
     */
    protected abstract T get(int x, int y);

    /**
     * Check whether the tuples are equal to one another.
     *
     * @param o The object we want to check the equality of.
     * @return Whether the tuples have the same x and y coordinates.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2i<?> tuple2i = (Tuple2i<?>) o;
        return x == tuple2i.x &&
                y == tuple2i.y;
    }

    /**
     * Get an unique hash code.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }
}
