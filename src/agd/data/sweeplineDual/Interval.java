package agd.data.sweeplineDual;

public class Interval implements Comparable<Interval> {

    // Variables
    // y-coords
    private int start;
    private int end;
    // Furthest x-coord
    private int minDepth;
    private int maxDepth;

    private int id;
    // Intervals to the left and right of this interval in the interval tree
    private Interval left;
    private Interval right;

    /**
     * Constructor for an interval
     *
     * @param start : Start of the interval, smallest y-coord
     * @param end : End of the interval, largest y-coord
     * @param minDepth : the min x-coord of interval
     * @param maxDepth : the max x-coord of interval
     * @param id
     */
    public Interval(int start, int end, int minDepth, int maxDepth, int id) {
        this.start = start;
        this.end = end;
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
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

    public int getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
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
        return "[" + this.getStart() + ", " + this.getEnd() + ", " + this.getId() +  ", " + this.getMaxDepth() + "]";
    }
}
