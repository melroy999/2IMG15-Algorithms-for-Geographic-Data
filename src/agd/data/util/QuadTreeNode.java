package agd.data.util;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An efficient data structure for rectangle collision detection.
 */
// TODO make our own implementation of a rectangle holding the id of the associated point, with enhanced equality checks.
public class QuadTreeNode<T extends EntryRectangle> {
    // The maximum number of nodes that are allowed to be inside of the node before splitting.
    private static final int NO_MAX_ENTRIES = 5;

    // The children of the node in the quad tree.
    private final List<QuadTreeNode<T>> children;

    // The rectangular bounding box of the node.
    private final Rectangle box;

    // The rectangles that are stored within the node (if the node is a leaf).
    private final List<T> entries = new ArrayList<>();

    /**
     * Create a new quad tree node with the given bounding box.
     *
     * @param box The bounding box of the node in the quad tree.
     */
    public QuadTreeNode(Rectangle box) {
        this.box = box;
        this.children = new ArrayList<>(4);
    }

    /**
     * Insert rectangle r into the quad tree.
     *
     * @param r The rectangle to insert into the quad tree.
     */
    public void insert(T r) {
        // First, check whether r has overlap with the bounding box.
        if(box.intersects(r)) {
            if(children.stream().noneMatch(Objects::nonNull)) {
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
                children.forEach(c -> c.insert(r));
            }
        }
    }

    /**
     * Remove the given rectangle from the quad tree if it exists.
     *
     * @param r The rectangle that should be deleted from the quad tree.
     */
    public void delete(T r) {
        if(box.intersects(r)) {
            if(children.stream().noneMatch(Objects::nonNull)) {
                // We are querying a leaf node. Delete it if it is here.
                entries.remove(r);
            } else {
                // We have to find which of the children the rectangle overlaps with.
                children.forEach(c -> c.delete(r));
            }
        }
    }

    /**
     * Query the given rectangular area for intersecting rectangles.
     *
     * @param r The rectangular area to query.
     */
    public List<T> query(Rectangle r) {
        Set<T> intersections = new HashSet<>();

        // Using the recursive definition to find all potential intersecting rectangles.
        query(r, intersections);

        // Find which rectangles intersect.
        return intersections.stream().filter(c -> c.intersects(r)).collect(Collectors.toList());
    }

    /**
     * Query the given rectangular area for intersecting rectangles.
     *
     * @param r The rectangular area to query.
     * @param intersections A set holding rectangles in the quad tree that intersect with the given area.
     */
    private void query(Rectangle r, Set<T> intersections) {
        if(box.intersects(r)) {
            if(children.stream().noneMatch(Objects::nonNull)) {
                // We are querying a leaf node. Do a naive check.
                intersections.addAll(entries);
            } else {
                // We have to find which of the children the rectangle overlaps with.
                children.forEach(c -> c.query(r, intersections));
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
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) != null) {
                children.get(i).clear();
                children.set(i, null);
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
        children.set(0, new QuadTreeNode<>(new Rectangle(x, y, halfWidth, halfHeight)));
        children.set(1, new QuadTreeNode<>(new Rectangle(x + halfWidth, y, halfWidth2, halfHeight)));
        children.set(2, new QuadTreeNode<>(new Rectangle(x, y + halfHeight, halfWidth, halfHeight2)));
        children.set(3, new QuadTreeNode<>(new Rectangle(x + halfWidth, y + halfHeight, halfWidth2, halfHeight2)));

        // Push the current list of entries to the children through insertions.
        entries.forEach(this::insert);

        // TODO it might still be better to store certain nodes in internal nodes in case they are in more than one cell.

        // Clear the list of entries.
        entries.clear();
    }
}
