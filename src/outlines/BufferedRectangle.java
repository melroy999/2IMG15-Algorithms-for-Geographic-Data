package outlines;

import agd.data.outlines.OutlineRectangle;
import agd.data.util.EntryRectangle;

import java.awt.*;

public class BufferedRectangle extends OutlineRectangle {

    private BufferedRectangle(int x, int y, int width) {
        super(x, y, width, null);
    }

    public static BufferedRectangle getRectangle(Rectangle r, int weight) {
        return new BufferedRectangle(2 * r.x - weight, 2 * r.y - weight, 2 * (r.width + weight));
    }
}
