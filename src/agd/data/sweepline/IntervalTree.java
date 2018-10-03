package agd.data.sweepline;

import java.util.List;

public class IntervalTree {

    private Interval root;

    // Needed functions: Add interval, check intersection
    // Possibly needed functions: Remove interval, update interval

    /**
     * addInterval function that adds a given interval to the tree
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
            if (root.getRight() == null) {
                root.setRight(node);
            } else {
                addInterval(root.getRight(), node);
            }
        }

        // Else, place the interval on the right
        else {
            if (root.getLeft() == null) {
                root.setLeft(node);
            } else {
                addInterval(root.getLeft(), node);
            }
        }

        return root;
    }

    public static List<Interval> checkInterval(Interval tree, Interval interval){


        return null;
    }
}
