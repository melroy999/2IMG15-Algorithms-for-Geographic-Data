package agd.solver;

import agd.core.Core;
import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.data.outlines.*;
import agd.data.output.HalfGridPoint;
import agd.data.util.OutlineDimensions;
import agd.data.util.QuadTreeNode;
import agd.math.Point2d;
import javafx.util.Pair;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleOutlineMergeSolver extends SimpleOutlineSolver {
    private static boolean binarySearch = false;

    /**
     * Solve the given problem instance.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points   The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {
        Core core = Core.getCore();
        binarySearch = core.gui.binarySearchCheckBox.isSelected();

        solve(instance, points, (SortingOptions) core.gui.sortSelector.getSelectedItem());
    }

    public enum SortingOptions {
        MANHATTAN_CENTROID, CENTROID, CORNER, CLOSEST_POINT, FURTHEST, NONE, SIZE_ASC, SIZE_DESC, X, Y, ROTATION, MAX_BASED, MIN_BASED
    }

    @SuppressWarnings("Duplicates")
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points, SortingOptions option) {
        // Find the centre of mass.
        Point2d centre = new Point2d();
        for(WeightedPoint p : instance.getPoints()) {
            centre = centre.add(p.c);
        }
        centre = centre.scale(1d / instance.getPoints().size());

        // Initialize the array of points to hold null for every entry.
        for(int i = 0; i < instance.getPoints().size(); i++) {
            points.add(null);
        }
        Point2d c = centre;

        // Find the distance between the centre point and all of the points, and sort on distance.
        Comparator<WeightedPoint> comparator;
        switch (option) {
            case CENTROID:
                comparator = getCentroidComparator(centre);
                break;
            case CORNER:
                comparator = getCornerPointComparator(centre);
                break;
            case CLOSEST_POINT:
                comparator = getBorderPointComparator(centre);
                break;
            case FURTHEST:
                comparator = getFurthestPointComparator(centre);
                break;
            case NONE:
                comparator = (a1, a2) -> 0;
                break;
            case SIZE_ASC:
                comparator = Comparator.comparingInt(a -> a.w);
                break;
            case SIZE_DESC:
                comparator = (a1, a2) -> -Integer.compare(a1.w, a2.w);
                break;
            case X:
                comparator = Comparator.comparingDouble(a -> a.x);
                break;
            case Y:
                comparator = Comparator.comparingDouble(a -> a.y);
                break;
            case ROTATION:
                comparator = getRotationalComparator(centre);
                break;
            case MAX_BASED:
                comparator = Comparator.comparingDouble(a -> Math.max(Math.abs(a.x - c.x), Math.abs(a.y - c.y)));
                break;
            case MIN_BASED:
                comparator = Comparator.comparingDouble(a -> Math.min(Math.abs(a.x - c.x), Math.abs(a.y - c.y)));
                break;
            case MANHATTAN_CENTROID:
            default:
                comparator = getManhattanCentroidComparator(centre);
                break;
        }

        // Create a quadtree in which we will check for overlapping rectangles.
        int width = instance.max_x - instance.min_x;
        int height = instance.max_y - instance.min_y;
        QuadTreeNode<OutlineRectangle> tree = new QuadTreeNode<>(
                new Rectangle(
                        instance.min_x - 20 * width,
                        instance.min_y - 20 * height,
                        41 * width,
                        41 * height
                )
        );

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

//        System.out.println("We have generated " + outlines.size() + " outline groups.");
        printSolution(outlines);
    }

    private static void insertNewOutline(QuadTreeNode<OutlineRectangle> tree, Set<AbstractOutline> outlines, OutlineRectangle rectangle, WeightedPoint p, ArrayList<HalfGridPoint> points) {
        // Create a new outline, which will set a pointer in the rectangle to the outline.
        outlines.add(new SimpleOutline(rectangle));
        Point2d placement = p.c;
        tree.insert(rectangle);

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
            BufferedOutline bOutline = new BufferedOutline((SimpleOutline) outline, 0.5 * p.w);
            Point2d placement = bOutline.projectAndSelect(p);
            OutlineRectangle result = getOutlineRectangle(placement, p, false);

            // Is the placement viable? I.e. is the spot free in the tree?
            List<OutlineRectangle> conflicts = tree.query(result);

            // Remember the rectangles we had a conflict with during the first placement.
            if(firstConflicts == null) {
                firstConflicts = conflicts;
            }

            if(conflicts.isEmpty()) {
                Pair<Point2d, OutlineRectangle> closestPlacement = findCloserPlacement(placement, result, p, tree);

                if(binarySearch) {
                    if(closestPlacement.getKey().distance2(placement) < 1e-4) {
                        // We haven't found a better position, add it to the outline.
                        ((SimpleOutline) outline).insert(closestPlacement.getValue());
                    } else {
                        // Create a new outline.
                        outlines.add(new SimpleOutline(closestPlacement.getValue()));
                    }

                    tree.insert(closestPlacement.getValue());
                    points.set(p.i, HalfGridPoint.make(closestPlacement.getKey(), p));
                } else {

                    // We can freely add the selected position.
                    ((SimpleOutline) outline).insert(result);
                    tree.insert(result);

                    // Add the chosen rectangle to the tree and result.
                    points.set(p.i, HalfGridPoint.make(placement, p));
                }

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
            break;
        }

        outlines.add(source);

        // Next, we postpone the insertion of the point.
        return false;
    }

    private static Pair<Point2d, OutlineRectangle> findCloserPlacement(Point2d placement, OutlineRectangle rectangle, WeightedPoint p, QuadTreeNode<OutlineRectangle> tree) {
        // The best candidates.
        OutlineRectangle candidateRectangle = rectangle;

        Point2d candidatePlacement = placement;
        Point2d invalidPlacement = p.c;

        while(candidatePlacement.distance(invalidPlacement) > p.w) {
            Point2d halfway = invalidPlacement.interpolate(candidatePlacement, 0.5);
            Point2d altPlacement = HalfGridPoint.make(halfway, p).point();
            OutlineRectangle altResult = getOutlineRectangle(halfway, p, false);

            if(tree.query(altResult).isEmpty()) {
                if(candidatePlacement.epsilonEquals(altPlacement, 1e-4)) break;

                // Our halfway point is the new candidate.
                candidatePlacement = altPlacement;
                candidateRectangle = altResult;
            } else {
                if(invalidPlacement.epsilonEquals(altPlacement, 1e-4)) break;

                // We have to look above the halfway point.
                invalidPlacement = altPlacement;
            }
        }

        return new Pair<>(candidatePlacement, candidateRectangle);

    }

    private void printSolution(Set<AbstractOutline> outlines) {

        // Print the entire solution.
        StringBuilder result = new StringBuilder();
        result.append("\\resizebox{\\textwidth}{!}{% <------ Don't forget this %\n");
        result.append("\\begin{tikzpicture}[x=5mm, y=5mm, baseline, trim left]\n");
        result.append("\\tikz {\n");

        for(AbstractOutline outline : outlines) {
            // Combine everything into one figure.
            result.append(outline.toTikzCode()).append("\n");
        }

        result.append("}\n");
        result.append("\\end{tikzpicture}\n");
        result.append("}");

        StringSelection selection = new StringSelection(result.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private static SimpleOutline merge(AbstractOutline o1, AbstractOutline o2) {
        OutlineDimensions d1 = o1.getDimensions();
        OutlineDimensions d2 = o2.getDimensions();

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
            return new SimpleOutline(rectangle, rectangles);
        } else if(d1.intersects(d2)) {
//            System.out.println("Intersection.");
//            System.out.println("Area " + (d1.width * d1.height + d2.width * d2.height - d1.intersection(d2).width * d1.intersection(d2).height) + " to " + rectangle.width * rectangle.height);

            // TODO combine the two dimensions into an outline.
            // TODO ALT: merge the two outlines.
            return new SimpleOutline(rectangle, rectangles);
        } else {
//            System.out.println("No intersection.");
//            System.out.println("Area " + (d1.width * d1.height + d2.width * d2.height - d1.intersection(d2).width * d1.intersection(d2).height) + " to " + rectangle.width * rectangle.height);

            // Convert the outline to a square.
            return new SimpleOutline(rectangle, rectangles);
        }
    }

    /**
     * Convert the preferred placement of a weighted point to a outline rectangle.
     *
     * @param p The chosen placement for the center point.
     * @return An outline rectangle with the point c at the center.
     */
    protected static OutlineRectangle getOutlineRectangle(Point2d p, WeightedPoint o) {
        return new OutlineRectangle(
                (int) Math.round(p.x - 0.5 * o.w),
                (int) Math.round(p.y - 0.5 * o.w),
                o.w,
                o
        );
    }

    /**
     * Convert the preferred placement of a weighted point to a outline rectangle.
     *
     * @param p The chosen placement for the center point.
     * @return An outline rectangle with the point c at the center.
     */
    protected static OutlineRectangle getOutlineRectangle(Point2d p, WeightedPoint o, boolean includeBorders) {
        return new OutlineRectangle(
                (int) Math.round(p.x - 0.5 * o.w),
                (int) Math.round(p.y - 0.5 * o.w),
                o.w,
                o,
                includeBorders
        );
    }

    private static Comparator<WeightedPoint> getRotationalComparator(final Point2d c) {
        return Comparator.comparingDouble(a -> getAngleDegree(c, a));
    }

    private static double getAngleDegree(Point2d origin, Point2d target) {
        double n = 270 - (Math.atan2(origin.y - target.y, origin.x - target.x)) * 180 / Math.PI;
        return n % 360;
    }
}
