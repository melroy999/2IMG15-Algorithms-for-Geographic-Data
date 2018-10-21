package agd.solver;

import agd.core.Core;
import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.data.output.HalfGridPoint;
import agd.data.util.QuadTreeNode;
import agd.math.Point2d;
import outlines.Outline;
import outlines.OutlineRectangle;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class OutlineMergeSolver extends AbstractSolver {
    /**
     * Solve the given problem instance.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points   The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {
        Core core = Core.getCore();
        solve(instance, points, (SortingOptions) core.gui.sortSelector.getSelectedItem());
    }

    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points, SortingOptions option) {
        // Find the centre of mass.
        Point2d centre = getCentreAndInit(instance, points);

        // Find the distance between the centre point and all of the points, and sort on distance.
        Comparator<WeightedPoint> comparator = getPointComparator(option, centre);

        // Create a quadtree in which we will check for overlapping rectangles.
        QuadTreeNode<OutlineRectangle> tree = initializeQuadTree2(instance);

        // The list of outlines that have been generated.
        Set<Outline> outlines = new HashSet<>();

        PriorityQueue<WeightedPoint> queue = new PriorityQueue<>(comparator);
        queue.addAll(instance.getPoints());

        // Insert the points into the plane one by one, using the outline for placement resolution.
        while(!queue.isEmpty()) {
            // Remove the first element from the queue.
            WeightedPoint p = queue.poll();

            // Do we overlap with any of the rectangles that are currently stored within the tree?
            OutlineRectangle rectangle = getOutlineRectangle2(p.c, p);
            List<OutlineRectangle> intersections = tree.query(rectangle);

            // Create a new outline or put the rectangle in an existing outline.
            if(!intersections.isEmpty()) {
                if(!insertExistingOutline(tree, outlines, intersections, p, points)) {
                    // We have failed to place the point, and have to retry.
                    queue.add(p);
                }
            } else {
                insertNewOutline(tree, outlines, rectangle, p, points);
            }
        }
    }

    private static void insertNewOutline(QuadTreeNode<OutlineRectangle> tree, Set<Outline> outlines, OutlineRectangle rectangle, WeightedPoint p, ArrayList<HalfGridPoint> points) {
        // We know that the default centre point for p is a valid placement with no overlaps.
        Point2d placement = p.c;

        // Create a new outline, which will set a pointer in the rectangle to the outline.
        outlines.add(new Outline(rectangle));

        // Add the rectangle to the tree.
        tree.insert(rectangle);

        // Add the chosen rectangle to the tree and result.
        points.set(p.i, HalfGridPoint.make(placement, p));
    }

    private static boolean insertExistingOutline(QuadTreeNode<OutlineRectangle> tree, Set<Outline> outlines, List<OutlineRectangle> intersections, WeightedPoint p, ArrayList<HalfGridPoint> points) {
        // Which distinct outlines do we intersect with? Sort them on rectangle size in increasing order.
        List<Outline> intersectingOutlines = intersections.stream().map(OutlineRectangle::getOutline).distinct().collect(Collectors.toList());
        intersectingOutlines.sort(Comparator.comparingInt(a -> a.getRectangles().size()));

        List<OutlineRectangle> firstConflicts = null;

        // For each of the outlines, attempt an insertion.
        for(Outline outline : intersectingOutlines) {
            // Buffer the outline.
            Point2d placement = outline.projectAndSelect(p, 0.5 * p.w);
            OutlineRectangle result = getOutlineRectangle2(placement, p);

            // Is the placement viable? I.e. is the spot free in the tree?
            List<OutlineRectangle> conflicts = tree.query(result);

            // Remember the rectangles we had a conflict with during the first placement.
            if(firstConflicts == null) {
                firstConflicts = conflicts;
            }

            // If we have no conflicts, we can place the rectangle freely.
            if(conflicts.isEmpty()) {

                // We can freely add the selected position.
                outline.insert(result);
                tree.insert(result);

                // Add the chosen rectangle to the tree and result.
                points.set(p.i, HalfGridPoint.make(placement, p));

                return true;
            }
        }

        // We have to merge some outlines.
        Outline source = intersectingOutlines.get(0);
        intersectingOutlines = firstConflicts.stream().map(OutlineRectangle::getOutline).distinct().collect(Collectors.toList());

        // Merge all the intersecting outlines into the source outline.
        for(Outline outline : intersectingOutlines) {
            source.merge(outline);
            outlines.remove(outline);
        }

        // Next, we postpone the insertion of the point.
        return false;
    }

    /**
     * Convert the preferred placement of a weighted point to a outline rectangle.
     *
     * @param p The chosen placement for the center point.
     * @return An outline rectangle with the point c at the center.
     */
    protected static OutlineRectangle getOutlineRectangle2(Point2d p, WeightedPoint o) {
        return new OutlineRectangle(
                (int) Math.round(p.x - 0.5 * o.w),
                (int) Math.round(p.y - 0.5 * o.w),
                o.w,
                o
        );
    }

    static QuadTreeNode<OutlineRectangle> initializeQuadTree2(ProblemInstance instance) {
        int width = instance.max_x - instance.min_x;
        int height = instance.max_y - instance.min_y;
        return new QuadTreeNode<>(
                new Rectangle(
                        instance.min_x - 20 * width,
                        instance.min_y - 20 * height,
                        41 * width,
                        41 * height
                )
        );
    }
}
