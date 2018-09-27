package agd.data.util;

import agd.data.input.WeightedPoint;

import java.awt.*;

/**
 * A data structure representing a triangle, with a pointer to the appropriate point.
 */
public class EntryRectangle extends Rectangle {
    public final WeightedPoint owner;

    public EntryRectangle(int x, int y, int width, int height, WeightedPoint owner) {
        super(x, y, width, height);
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EntryRectangle rectangle = (EntryRectangle) o;
        return owner != null ? owner.equals(rectangle.owner) : rectangle.owner == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
