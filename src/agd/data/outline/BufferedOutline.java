package agd.data.outline;

import agd.math.Point2d;

import java.util.Iterator;

/**
 * A buffered version of an original outline.
 */
public class BufferedOutline {
    // Access point to one of the edges in the outline.
    private OutlineEdge edge;

    /**
     * Create a new buffered outline.
     *
     * @param outline The outline to buffer.
     * @param w The width of the buffering zone.
     */
    public BufferedOutline(Outline outline, double w) {
        createOutline(outline, w);
        sanitizeOutline();
    }

    /**
     * Generate the buffered outline associated with the given outline.
     *
     * @param outline The outline to buffer.
     * @param w The width of the buffering zone.
     */
    private void createOutline(Outline outline, double w) {
        // Keep the last encountered edge such that we can set next and previous references.
        OutlineEdge last = null;

        for(OutlineEdge e : outline.getEdge()) {
            // Translate each edge to the correct position.
            Point2d p = e.getOrigin().add(e.getDirection().bufferVector().scale(w));

            // Check whether we have made a left turn.
            if(e.getPrevious().getDirection().isLeftTurn(e.getDirection())) {
                p = p.add(e.getDirection().leftTurnVector().scale(2 * w));
            }

            // Create a new edge and set the appropriate pointers.
            OutlineEdge bufferedEdge = new OutlineEdge(p, e.getDirection());

            if(last != null) {
                last.setNext(bufferedEdge);
            }
            last = bufferedEdge;

            if(edge == null) {
                edge = bufferedEdge;
            }
        }

        // Finalize the cycle.
        assert last != null;
        last.setNext(edge);
    }


    /**
     * Remove the intersections within the outline.
     */
    private void sanitizeOutline() {
        // TODO sanitization through an intersection algorithm.
    }
}
