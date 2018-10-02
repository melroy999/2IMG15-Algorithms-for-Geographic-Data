package agd.data.sweepline;

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


        return null;
    }
}
