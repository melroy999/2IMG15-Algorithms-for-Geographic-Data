package agd.solver;

import agd.core.Core;
import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.data.outlines.*;
import agd.data.output.HalfGridPoint;
import agd.data.util.OutlineDimensions;
import agd.data.util.QuadTreeNode;
import agd.math.Point2d;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ComplexOutlineMergeSolver extends AbstractSolver {

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

    @SuppressWarnings("Duplicates")
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points, SortingOptions option) {
        // Find the centre of mass.
        Point2d centre = getCentreAndInit(instance, points);

        // Find the distance between the centre point and all of the points, and sort on distance.
        Comparator<WeightedPoint> comparator = getPointComparator(option, centre);

        // Create a quadtree in which we will check for overlapping rectangles.
        QuadTreeNode<OutlineRectangle> tree = initializeQuadTree(instance);

        // The list of outlines that have been generated.
        Set<AbstractOutline> outlines = new HashSet<>();

        PriorityQueue<WeightedPoint> queue = new PriorityQueue<>(comparator);
        queue.addAll(instance.getPoints());

        // Insert the points into the plane one by one, using the outline for placement resolution.
        while(!queue.isEmpty()) {
            // Remove the first element from the queue.
            WeightedPoint p = queue.poll();

            // Do we overlap with any of the rectangles that are currently stored within the tree?
            OutlineRectangle rectangle = getOutlineRectangle(p.c, p, false);
            List<OutlineRectangle> intersections = tree.query(rectangle);

            // Create a new outline or put the rectangle in an existing outline.
            if(!intersections.isEmpty()) {
                if(!insertExistingOutline(tree, outlines, intersections, p, centre, points)) {
                    // We have failed to place the point, and have to retry.
                    queue.add(p);
                }
            } else {
                insertNewOutline(tree, outlines, rectangle, p, points);
            }
        }

        printSolution(outlines);
    }

    private static void insertNewOutline(QuadTreeNode<OutlineRectangle> tree, Set<AbstractOutline> outlines, OutlineRectangle rectangle, WeightedPoint p, ArrayList<HalfGridPoint> points) {
        // We know that the default centre point for p is a valid placement with no overlaps.
        Point2d placement = p.c;

        // Check which rectangles it touches.
        List<OutlineRectangle> query = tree.query(new OutlineRectangle(rectangle, true));
        if(!query.isEmpty()) {
            // TODO merge the outlines using the rectangle. We know that it touches all neighbors.
            // It might be an idea to insert it into all the affected outlines, and then merge after.
            // We can also just insert it into one of them, say the first.

            // Find the neighboring outlines. Insert the rectangle in the first of them, and merge the others.
            List<AbstractOutline> intersectingOutlines = query.stream().map(OutlineRectangle::getOutline).distinct().collect(Collectors.toList());
            intersectingOutlines.sort((a, b) -> -Integer.compare(a.getRectangles().size(), b.getRectangles().size()));

            ((ComplexOutline) intersectingOutlines.get(0)).insert(rectangle);

            // Do the merging, using only the edges on our inserted rectangle as targets.
            for(AbstractOutline outline : intersectingOutlines) {
                // Do the merge

                // Sanitize: i.e. make sure that we do not have to consecutive edges in the same direction.
                // Also do an intersection check on edges that follow the same direction.
            }

            // Add the rectangle to the tree.
            tree.insert(rectangle);
        } else {
            // Create a new outline, which will set a pointer in the rectangle to the outline.
            outlines.add(new ComplexOutline(rectangle));

            // Add the rectangle to the tree.
            tree.insert(rectangle);
        }

        // Add the chosen rectangle to the tree and result.
        points.set(p.i, HalfGridPoint.make(placement, p));
    }

    private static boolean insertExistingOutline(QuadTreeNode<OutlineRectangle> tree, Set<AbstractOutline> outlines, List<OutlineRectangle> intersections, WeightedPoint p, Point2d centre, ArrayList<HalfGridPoint> points) {
        // Which distinct outlines do we intersect with? Sort them on rectangle size in decreasing order.
        List<AbstractOutline> intersectingOutlines = intersections.stream().map(OutlineRectangle::getOutline).distinct().collect(Collectors.toList());
        intersectingOutlines.sort((a, b) -> -Integer.compare(a.getRectangles().size(), b.getRectangles().size()));

        List<OutlineRectangle> firstConflicts = null;

        // For each of the outlines, attempt an insertion.
        for(AbstractOutline outline : intersectingOutlines) {
            // Create a buffered variant of the outline, and get a preferred placement.
            BufferedOutline bOutline = new BufferedOutline((ComplexOutline) outline, 0.5 * p.w);
            Point2d placement = bOutline.projectAndSelect(p);
            OutlineRectangle result = getOutlineRectangle(placement, p, false);

            // Is the placement viable? I.e. is the spot free in the tree?
            List<OutlineRectangle> conflicts = tree.query(result);

            // Remember the rectangles we had a conflict with during the first placement.
            if(firstConflicts == null) {
                firstConflicts = conflicts;
            }

            if(conflicts.isEmpty()) {

                // We can freely add the selected position.
                ((ComplexOutline) outline).insert(result);
                tree.insert(result);

                // Add the chosen rectangle to the tree and result.
                points.set(p.i, HalfGridPoint.make(placement, p));

                return true;
            }
        }

        // Merge the outlines that are in conflict when placing the rectangle in the foremost outline.
        AbstractOutline source = intersectingOutlines.get(0);
        intersectingOutlines = firstConflicts.stream().map(OutlineRectangle::getOutline).distinct().collect(Collectors.toList());

        outlines.remove(source);

        // We have been unable to place the point without causing conflicts. We have to merge outlines.
        for(AbstractOutline outline : intersectingOutlines) {
            // Merge all the intersecting outlines.
            source = merge(source, outline);
            outlines.remove(outline);
        }

        outlines.add(source);

        // Next, we postpone the insertion of the point.
        return false;
    }

    private static ComplexOutline merge(AbstractOutline o1, AbstractOutline o2) {
        OutlineDimensions d1 = o1.getDimensions();
        OutlineDimensions d2 = o2.getDimensions();

//        System.out.println(o1.getRectangles().size() + "-" + o2.getRectangles().size());

        // Find the bounds.
        int xmin = Math.min(d1.x, d2.x);
        int xmax = Math.max(d1.x + d1.width, d2.x + d2.width);
        int ymin = Math.min(d1.y, d2.y);
        int ymax = Math.max(d1.y + d1.height, d2.y + d2.height);

        // Create a new outline.
        OutlineRectangle rectangle = new OutlineRectangle(xmin, ymin, xmax - xmin, ymax - ymin, null, false);

        List<OutlineRectangle> rectangles = new ArrayList<>();
        rectangles.addAll(o1.getRectangles());
        rectangles.addAll(o2.getRectangles());

        if(d1.contains(d2) || d2.contains(d1)) {
            return new ComplexOutline(rectangle, rectangles);
        } else if(d1.intersects(d2)) {
//            System.out.println("Intersection.");
//            System.out.println("Area " + (d1.width * d1.height + d2.width * d2.height - d1.intersection(d2).width * d1.intersection(d2).height) + " to " + rectangle.width * rectangle.height);

            // TODO combine the two dimensions into an outline.
            // TODO ALT: merge the two outlines.
            return new ComplexOutline(rectangle, rectangles);
        } else {
//            System.out.println("No intersection.");
//            System.out.println("Area " + (d1.width * d1.height + d2.width * d2.height - d1.intersection(d2).width * d1.intersection(d2).height) + " to " + rectangle.width * rectangle.height);

            // Convert the outline to a square.
            return new ComplexOutline(rectangle, rectangles);
        }
    }
}
