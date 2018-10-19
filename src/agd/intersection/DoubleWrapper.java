package agd.intersection;

public class DoubleWrapper implements Comparable<DoubleWrapper> {
    // The value of the double.
    private final double v;

    public DoubleWrapper(double v) {
        this.v = v;
    }

    @Override
    public int compareTo(DoubleWrapper o) {
        if(Math.abs(o.v - v) < 1e-4) {
            return 0;
        }
        return Double.compare(v, o.v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleWrapper that = (DoubleWrapper) o;

        return compareTo(that) == 0;
    }
}
