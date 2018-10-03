package agd.data.sweepline;

public class Interval implements Comparable<Interval> {

    // Variables
    // y-coords
    private int start;
    private int end;
    // The max y-coord
    private int max;
    // Furthest x-coord
    private int depth;
    // Intervals to the left and right of this interval in the interval tree
    private Interval left;
    private Interval right;

    /**
     * Constructor for an interval
     *
     * @param start : Start of the interval, smallest y-coord
     * @param end : End of the interval, largest y-coord
     * @param depth : x-coord of interval
     */
    public Interval(int start, int end, int depth) {
        this.start = start;
        this.end = end;
        this.depth = depth;
        this.max = end;
    }

    // Getters and setters for above variables
    public Interval getLeft() {
        return left;
    }

    public void setLeft(Interval left) {
        this.left = left;
    }

    public Interval getRight() {
        return right;
    }

    public void setRight(Interval right) {
        this.right = right;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public int compareTo(Interval o) {
        return this.start == o.start ? Integer.compare(this.end, o.end) : Integer.compare(this.start, o.start);
    }
}
