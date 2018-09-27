package agd.data.util;

import agd.data.input.WeightedPoint;

import java.awt.*;

/**
 * A data structure representing a triangle, with a pointer to the appropriate point.
 */
public class EntryRectangle extends Rectangle {
    // The weighted point this rectangle is associated to.
    public final WeightedPoint owner;

    public EntryRectangle(int x, int y, int width, int height, WeightedPoint owner) {
        super(x, y, width, height);
        this.owner = owner;
    }

    // TODO possibly add a better equality and hashcode.
}
