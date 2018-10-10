package agd.data.outlines;

import agd.math.Point2d;

import java.util.List;

/**
 * Create a buffered outline of an existing outline.
 */
public class BufferedOutline extends AbstractOutline {
    /**
     * A constructor that should be used when basing an outline on another outline.
     *
     * @param outline The outline to use as the original outline.
     * @param w The width of the buffering zone.
     */
    public BufferedOutline(SimpleOutline outline, double w) {
        super(outline.getRectangles());
        setEdge(createOutline(outline, w));
    }

    /**
     * Generate the buffered outline associated with the given outline.
     *
     * @param outline The outline to buffer.
     * @param w The width of the buffering zone.
     * @return The edge to use as the access point.
     */
    private Edge createOutline(AbstractOutline outline, double w) {
        // Keep the last encountered edge such that we can set next and previous references.
        Edge last = null;
        Edge first = null;

        for(Edge e : outline) {
            // Translate each edge to the correct position.
            Point2d p = e.getOrigin().add(e.getDirection().bufferVector().scale(w));

            // Check whether we have made a left turn.
            if(e.getPrevious().getDirection().isLeftTurn(e.getDirection())) {
                p = p.add(e.getDirection().leftTurnVector().scale(2 * w));
            }

            // Create a new edge and set the appropriate pointers.
            Edge bufferedEdge = new Edge(p, e.getDirection());

            if(last != null) {
                last.setNext(bufferedEdge);
            }
            last = bufferedEdge;

            if(first == null) {
                first = bufferedEdge;
            }
        }

        // Finalize the cycle.
        assert last != null;
        last.setNext(first);

        // Return the first edge that we have set.
        return first;
    }
}
