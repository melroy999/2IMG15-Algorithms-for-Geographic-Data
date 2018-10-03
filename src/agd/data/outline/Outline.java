package agd.data.outline;

/**
 * A class that represents an outline of a number of adjacent rectangles.
 */
public class Outline {
    // Access point to one of the edges in the outline.
    private OutlineEdge edge;

    /**
     * Create a new outline.
     *
     * @param rectangle The rectangle which defines the original outline.
     */
    public Outline(OutlineRectangle rectangle) {
        // A left directional edge in a rectangle can only be the bottom edge.
        edge = rectangle.createOutline(OutlineEdge.Direction.LEFT);
        rectangle.setOutline(this);
    }

    /**
     * Insert the given rectangle into the outline.
     *
     * @param rectangle The rectangle to insert into the outline.
     */
    public void insert(OutlineRectangle rectangle) {
        // TODO insertion


        rectangle.setOutline(this);
    }


}
