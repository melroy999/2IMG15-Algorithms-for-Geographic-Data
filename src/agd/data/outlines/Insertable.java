package agd.data.outlines;

/**
 * An interface used in outlines that allow for the insertion of rectangles.
 */
public interface Insertable {
    /**
     * Insert the given rectangle into the outline.
     *
     * @param rectangle The rectangle to insert into the outline.
     */
    void insert(OutlineRectangle rectangle);
}
