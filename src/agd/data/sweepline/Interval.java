package agd.data.sweepline;

public class Interval implements Comparable<Interval> {

    // Variables
    // y-coords
    private int start;
    private int end;
    // Furthest x-coord
    private int depth;

    private int id;
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
    public Interval(int start, int end, int depth, int id) {
        this.start = start;
        this.end = end;
        this.depth = depth;
        this.id = id;
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

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getId() {
        return id;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Interval o) {
        return this.start == o.start ? Integer.compare(this.end, o.end) : Integer.compare(this.start, o.start);
    }

    @Override
    public String toString() {
        return "[" + this.getStart() + ", " + this.getEnd() + ", " + this.getId() +  ", " + this.getDepth() + "]";
    }
}
