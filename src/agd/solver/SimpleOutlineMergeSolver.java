package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.data.outlines.*;
import agd.data.output.HalfGridPoint;
import agd.data.util.QuadTreeNode;
import agd.math.Point2d;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleOutlineMergeSolver extends SimpleOutlineSolver {
    /**
     * Solve the given problem instance.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points   The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {
        solve(instance, points, SortingOptions.CENTROID);
    }

    public enum SortingOptions {
        CENTROID, CORNER, CLOSEST_POINT, MANHATTAN_CENTROID, FURTHEST
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

        // Find the distance between the centre point and all of the points, and sort on distance.
        List<WeightedPoint> sortedPoints;
        switch (option) {
            case CENTROID:
                sortedPoints = getSortOnDefaultCentroid(instance.getPoints(), centre);
                break;
            case CORNER:
                sortedPoints = getSortOnCornerPoints(instance.getPoints(), centre);
                break;
            case CLOSEST_POINT:
                sortedPoints = getSortOnClosestPointOnBorder(instance.getPoints(), centre);
                break;
            case FURTHEST:
                sortedPoints = getSortOnFurthestCornerPoints(instance.getPoints(), centre);
                break;
            case MANHATTAN_CENTROID:
            default:
                sortedPoints = getSortOnManhattanCentroid(instance.getPoints(), centre);
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
        List<AbstractOutline> outlines = new ArrayList<>();
        int mergeCounter = 0;

        // Insert the points into the plane one by one, using the outline for placement resolution.
        for(WeightedPoint p : sortedPoints) {
            OutlineRectangle rectangle = getOutlineRectangle(p.c, p);
            List<OutlineRectangle> intersections = tree.query(rectangle);

            // The resulting placements.
            OutlineRectangle result;
            Point2d placement;

            if(!intersections.isEmpty()) {
                // Which outlines do we intersect with?
                List<AbstractOutline> intersectingOutlines = intersections.stream().map(
                        OutlineRectangle::getOutline).distinct().collect(Collectors.toList());

                if(intersectingOutlines.size() > 1) {
                    System.out.println("Intersecting with multiple outline groups.");
                }

                // If we have more than 1, we can choose.

                // Find the associated outline.
                intersectingOutlines.sort((a, b) -> -Integer.compare(a.getRectangles().size(), b.getRectangles().size()));
                SimpleOutline outline = (SimpleOutline) intersectingOutlines.get(0);
                BufferedOutline bOutline = new BufferedOutline(outline, 0.5 * p.w);
                placement = bOutline.projectAndSelect(p, centre);
//                placement = bOutline.projectAndSelect(p);
                result = getOutlineRectangle(placement, p);
                outline.insert(result);

                // We want to merge smaller outlines with the larger outline.
                if(intersectingOutlines.size() > 1) {
                    for(int i = 1; i < intersectingOutlines.size(); i++) {
                        // Merge outline i with outline 0.
                        mergeCounter++;
                        merge(outline, (SimpleOutline) intersectingOutlines.get(i), points, tree);
                    }
                }
            } else {
                // Create a new outline, which will set a pointer in the rectangle to the outline.
                outlines.add(new SimpleOutline(rectangle));
                placement = p.c;
                result = rectangle;
            }

            // Add the chosen rectangle to the tree and result.
            points.set(p.i, HalfGridPoint.make(placement, p));

            if(result.getOutline() == null) {
                System.out.println();
            }

            tree.insert(result);
        }

        System.out.println("We have generated " + outlines.size() + " outline groups, of which we merged " + mergeCounter + ".");

        // Print the entire solution.
        StringBuilder result = new StringBuilder();
        result.append("\\resizebox{\\textwidth}{!}{% <------ Don't forget this %\n");
        result.append("\\begin{tikzpicture}[x=5mm, y=5mm, baseline, trim left]\n");
        result.append("\\tikz {\n");

        for(AbstractOutline outline : outlines) {
            // Combine everything into one figure.
            result.append(outline.toTikzCode() + "\n");
        }

        result.append("}\n");
        result.append("\\end{tikzpicture}\n");
        result.append("}");

        StringSelection selection = new StringSelection(result.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public static void merge(SimpleOutline outline, SimpleOutline target, ArrayList<HalfGridPoint> points, QuadTreeNode<OutlineRectangle> tree) {
        // Find all the weighted points that are in the outline.
        List<WeightedPoint> weightedPoints = target.getRectangles().stream().map(r -> r.owner).collect(Collectors.toList());

        // Remove all the rectangles from the quadtree.
        target.getRectangles().forEach(tree::delete);

        // Sort them on the distance to the center of the outline.
        Rectangle dim = outline.getDimensions();
        Point2d centre = new Point2d(dim.x + 0.5 * dim.width, dim.y + 0.5 * dim.height);
        weightedPoints = getSortOnClosestPointOnBorder(weightedPoints, centre);

        // Insert them all into this outline.
        for(WeightedPoint p : weightedPoints) {
            BufferedOutline bOutline = new BufferedOutline(outline, 0.5 * p.w);
            Point2d placement = bOutline.projectAndSelect(p, centre);
            OutlineRectangle result = getOutlineRectangle(placement, p);
            outline.insert(result);

            // Add the chosen rectangle to the tree and result.
            points.set(p.i, HalfGridPoint.make(placement, p));
            tree.insert(result);
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
}