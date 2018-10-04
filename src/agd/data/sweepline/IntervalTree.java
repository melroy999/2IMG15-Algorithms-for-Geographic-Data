package agd.data.sweepline;

import java.util.ArrayList;

public class IntervalTree {

    private Interval root;

    // Needed functions: Add interval, check intersection
    // Possibly needed functions: Remove interval, update interval

    /**
     * addInterval, function that adds a given interval to the tree
     *
     * @param root : The interval representing the root of the tree
     * @param node : The interval we want to add
     * @return : Returns the chain of intervals representing the tree
     */
    public static Interval addInterval(Interval root, Interval node){

        // If the root of a (sub)tree is null, we return the node
        if (root == null) {
            return node;
        }

        // Check if the max value of a (sub)tree should be updated
        if (node.getMax() > root.getMax()) {
            root.setMax(node.getMax());
        }

        // Check if the interval "node" should be placed on the left
        // Placed on the left when root interval == node interval
        // Or when root.start > node.start or root.end > node.end
        if (root.compareTo(node) >= 0) {
            if (root.getLeft() == null) {
                root.setLeft(node);
            } else {
                addInterval(root.getLeft(), node);
            }
        }

        // Else, place the interval on the right
        else {
            if (root.getRight() == null) {
                root.setRight(node);
            } else {
                addInterval(root.getRight(), node);
            }
        }

        return root;
    }

    /**
     * checkInterval, function that finds all intersections of intervals in the interval tree with a given interval
     *
     * @param tree: The interval tree to with overlap with
     * @param interval: The given interval to check against @tree with
     * @return : Returns an arraylist containing all intervals in @tree that overlap with @interval
     */
    public static ArrayList<Interval> checkInterval(Interval tree, Interval interval){

        ArrayList<Interval> overlap = new ArrayList<>();
        // If the root of a (sub)tree is null, we return an empty list
        if (tree == null) {
            return overlap;
        }

        // Check if we have an overlap with current interval in interval tree add to list if we do
        if (!((tree.getEnd() < interval.getStart()) || (tree.getStart() > interval.getEnd()))) {
            overlap.add(tree);
        }

        // Check if we need to recurse to the left, max of left subtree is greater than the start of the interval
        if ((tree.getLeft() != null) && (tree.getLeft().getMax() >= interval.getStart())) {
            overlap.addAll(checkInterval(tree.getLeft(), interval));
        }

        // Recurse to the right
        overlap.addAll(checkInterval(tree.getRight(), interval));

        return overlap;
    }
}