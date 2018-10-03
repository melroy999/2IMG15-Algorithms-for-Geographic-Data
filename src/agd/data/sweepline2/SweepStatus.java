package agd.data.sweepline2;

import java.util.TreeSet;

public class SweepStatus {
    // The status, represented as a tree set.
    private TreeSet<LineSegment> status = new TreeSet<>();

    /**
     * Insert the given line segment into the sweep status.
     *
     * @param ls The line segment to add to the sweep status.
     */
    public void insert(LineSegment ls) {
        status.add(ls);
    }

    /**
     * Insert the given line segment into the sweep status.
     *
     * @param ls The line segment to add to the sweep status.
     */
    public void remove(LineSegment ls) {
        status.remove(ls);
    }

    /**
     * Find the line segment that is located directly above the given line segment (if it exists).
     *
     * @param ls The line segment for which we want to find a neighbor above.
     * @return The line segment above the given line segment if it exist, null otherwise.
     */
    public LineSegment above(LineSegment ls) {
        return status.higher(ls);
    }

    /**
     * Find the line segment that is located directly below the given line segment (if it exists).
     *
     * @param ls The line segment for which we want to find a neighbor below.
     * @return The line segment below the given line segment if it exist, null otherwise.
     */
    public LineSegment below(LineSegment ls) {
        return status.lower(ls);
    }
}
