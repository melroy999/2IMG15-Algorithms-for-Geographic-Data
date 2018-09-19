package agd.store.instance;

import java.util.ArrayList;

public class WeightedPointList extends ArrayList<WeightedPoint> {

    public WeightedPointList() {
    }

    public WeightedPointList(WeightedPointList points) {
        super(points);
    }

    /**
     * Add a point to the weighted point list.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param weight The weight of the point.
     */
    public void add(double x, double y, int weight) {
        add(new WeightedPoint(x, y, weight, size()));
    }
}
