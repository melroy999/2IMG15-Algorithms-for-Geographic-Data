package agd.data.util;

import agd.data.outlines.AbstractOutline;

import java.awt.*;

public class OutlineDimensions extends Rectangle {
    // The outline this rectangle is associated to.
    public final AbstractOutline owner;

    public OutlineDimensions(int x, int y, int width, int height, AbstractOutline owner) {
        super(x, y, width, height);
        this.owner = owner;
    }
}
