package agd.data.sweepline;

import java.util.ArrayList;
import java.util.List;

public class IntervalTree {

    private Interval root;

    public Interval getRoot() {
        return root;
    }

    public void setRoot(Interval root) {
        this.root = root;
    }

    // Needed functions: Add interval, check intersection
    // Possibly needed functions: Remove interval, update interval

    /**
     * addInterval, function that adds a given interval to the tree
     *
     * @param root : The interval representing the root of the tree
     * @param node : The interval we want to add
     * @return : Returns the chain of intervals representing the tree
     */
    public Interval addInterval(Interval root, Interval node){

        // If the root of a (sub)tree is null, we return the node
        if (root == null) {
            return node;
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
     * @param tree : The interval tree to with overlap with
     * @param interval : The given interval to check against @tree with
     * @return : Returns an arraylist containing all intervals in @tree that overlap with @interval
     */
    public ArrayList<Interval> checkInterval(Interval tree, Interval interval){

        ArrayList<Interval> overlap = new ArrayList<>();
        // If the root of a (sub)tree is null, we return an empty list
        if (tree == null) {
            return overlap;
        }

        // Check if we have an overlap with current interval in interval tree add to list if we do
        if (!((tree.getEnd() < interval.getStart()) || (tree.getStart() > interval.getEnd()))) {
            overlap.add(tree);
        }

        // Check if we need to recurse to the right, start of root is greater than the end of the interval
        if (!(tree.getRight() == null) || (tree.getStart() > interval.getEnd())) {
            overlap.addAll(checkInterval(tree.getRight(), interval));
        }

        // Recurse to the left
        overlap.addAll(checkInterval(tree.getLeft(), interval));

        return overlap;
    }

    /**
     * deleteInterval, function that deletes a given interval, @target, from the intervaltree, @tree, if present
     *
     * @param tree : The interval tree
     * @param target : The given interval to delete from @tree
     * @return : Returns the interval tree where @target has been deleted from @tree if present
     */
    public Interval deleteInterval(Interval tree, Interval target){

        // If the tree is empty, return the empty tree
        if (tree == null) {
            return null;
        }

        // Check if we have the correct interval in the tree
        if (tree.getId() == target.getId()) {
            // node with only one child or no child
            if (tree.getLeft() == null)
                return tree.getRight();
            else if (tree.getRight() == null)
                return tree.getLeft();

            // node with two children: Get the inorder successor (smallest in the right subtree)
            Interval successor = getLeft(tree.getRight());
            tree.setDepth(successor.getDepth());
            tree.setEnd(successor.getEnd());
            tree.setStart(successor.getStart());
            tree.setId(successor.getId());

            // Delete the inorder successor
            tree.setRight(deleteInterval(tree.getRight(), successor));
        }

        // Check if we need to recurse to the right
        if (!(tree.getRight() == null) || (tree.getStart() > target.getEnd())) {
            tree.setRight(deleteInterval(tree.getRight(), target));
        }

        // Recurse to the left
        tree.setLeft(deleteInterval(tree.getLeft(), target));

        return tree;
    }

    Interval getLeft(Interval tree)
    {
        while (tree.getLeft() != null)
        {
            tree = tree.getLeft();
        }
        return tree;
    }

    public void printTree(Interval tmp) {
        if (tmp == null) {
            return;
        }

        if (tmp.getLeft() != null) {
            printTree(tmp.getLeft());
        }

        System.out.print(tmp);

        if (tmp.getRight() != null) {
            printTree(tmp.getRight());
        }
    }
}