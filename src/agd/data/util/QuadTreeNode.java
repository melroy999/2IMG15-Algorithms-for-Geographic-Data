package agd.data.util;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * An efficient data structure for rectangle collision detection.
 */
// TODO make our own implementation of a rectangle holding the id of the associated point, with enhanced equality checks.
public class QuadTreeNode {
    // The maximum number of nodes that are allowed to be inside of the node before splitting.
    private static final int NO_MAX_ENTRIES = 5;

    // The children of the node in the quad tree.
    private final QuadTreeNode[] children;

    // The rectangular bounding box of the node.
    private final Rectangle box;

    // The rectangles that are stored within the node (if the node is a leaf).
    private final List<EntryRectangle> entries = new ArrayList<>();

    /**
     * Create a new quad tree node with the given bounding box.
     *
     * @param box The bounding box of the node in the quad tree.
     */
    public QuadTreeNode(Rectangle box) {
        this.box = box;
        this.children = new QuadTreeNode[4];
    }

    /**
     * Insert rectangle r into the quad tree.
     *
     * @param r The rectangle to insert into the quad tree.
     */
    public void insert(EntryRectangle r) {
        // First, check whether r has overlap with the bounding box.
        if(box.intersects(r)) {
            if(Arrays.stream(children).noneMatch(Objects::nonNull)) {
                // We are attempting to insert r into a leaf node.
                entries.add(r);

                if(entries.size() > NO_MAX_ENTRIES && box.width != 1) {
                    // We need to split the node.
                    // TODO avoid infinite splitting behavior if all nodes are in a 1 by 1 cell.
                    // TODO test whether the guard box.width != 1 is sufficient.
                    split();
                }
            } else {
                // We have to find which of the children the rectangle overlaps with.
                Arrays.stream(children).forEach(c -> c.insert(r));
            }
        }
    }

    /**
     * Remove the given rectangle from the quad tree if it exists.
     *
     * @param r The rectangle that should be deleted from the quad tree.
     */
    public void delete(EntryRectangle r) {
        if(box.intersects(r)) {
            if(Arrays.stream(children).noneMatch(Objects::nonNull)) {
                // We are querying a leaf node. Delete it if it is here.
                entries.remove(r);
            } else {
                // We have to find which of the children the rectangle overlaps with.
                Arrays.stream(children).forEach(c -> c.delete(r));
            }
        }
    }

    /**
     * Query the given rectangular area for intersecting rectangles.
     *
     * @param r The rectangular area to query.
     * @param intersections A set holding rectangles in the quad tree that intersect with the given area.
     */
    public void query(Rectangle r, Set<EntryRectangle> intersections) {
        if(box.intersects(r)) {
            if(Arrays.stream(children).noneMatch(Objects::nonNull)) {
                // We are querying a leaf node. Do a naive check.
                entries.stream().filter(t -> t.intersects(r)).forEach(intersections::add);
            } else {
                // We have to find which of the children the rectangle overlaps with.
                Arrays.stream(children).forEach(c -> c.query(r, intersections));
            }
        }
    }

    /**
     * Clear the data in the quad tree.
     */
    public void clear() {
        // Clear whatever data we have in this node.
        entries.clear();

        // Clear all the children recursively.
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                children[i].clear();
                children[i] = null;
            }
        }
    }

    /**
     * Split the node into four new nodes.
     */
    private void split() {
        // Gather the required data for the split.
        // Note that the length might have been an odd number, which has to be compensated for.
        int halfWidth = box.width / 2;
        int halfWidth2 = box.width - halfWidth;
        int halfHeight = box.height / 2;
        int halfHeight2 = box.height - halfHeight;
        int x = box.x;
        int y = box.y;

        // Create the child nodes.
        children[0] = new QuadTreeNode(new Rectangle(x, y, halfWidth, halfHeight));
        children[1] = new QuadTreeNode(new Rectangle(x + halfWidth, y, halfWidth2, halfHeight));
        children[2] = new QuadTreeNode(new Rectangle(x, y + halfHeight, halfWidth, halfHeight2));
        children[3] = new QuadTreeNode(new Rectangle(x + halfWidth, y + halfHeight, halfWidth2, halfHeight2));

        // Push the current list of entries to the children through insertions.
        entries.forEach(this::insert);

        // Clear the list of entries.
        entries.clear();
    }
}
