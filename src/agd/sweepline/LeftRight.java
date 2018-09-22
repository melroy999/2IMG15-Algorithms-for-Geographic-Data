package agd.sweepline;

// Sweep line algorithm that handles points from left to right
public class LeftRight {
    // Variables

    /**
     * Translate all points to their lower-left region corner
     *
     * @param TODO: find suitable data structure for point storage
     */
    private void translatePoints (){

        // p(x, y)→round(p(x, y)−0.5wx)

    }

    /**
     * Sort the translated points by x-coord, making sure that the order can be reverted to the original
     */
    private void sortPoints () {

    }

    // TODO: Use a sweep line algorithm to start placing square regions, tracking the regions that have been placed
    // TODO: Figure out the above

    // Status: Coords of corners of currently placed squares

    // Events: -Lower left region corner reached. Place square if possible or move to the right until possible to place
    //          and add square corner coords to status
    //         -Lower right region corner reached. Remove square corner coords from status
}
