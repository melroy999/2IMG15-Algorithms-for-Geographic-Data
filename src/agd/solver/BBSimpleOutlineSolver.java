package agd.solver;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BBSimpleOutlineSolver extends SimpleOutlineSolver {
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

    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points, SortingOptions option) {
        // Find the centre of mass.
        Point2d centre = new Point2d();
        for(WeightedPoint p : instance.getPoints()) {
            centre = centre.add(p.c);
        }
        centre = centre.scale(1d / instance.getPoints().size());

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
        // TODO choose a reliable bound for the quadtree.

        int width = instance.max_x - instance.min_x;
        int height = instance.max_y - instance.min_y;
        QuadTreeNode<OutlineDimensions> tree = new QuadTreeNode<>(
                new Rectangle(
                        instance.min_x - 20 * width,
                        instance.min_y - 20 * height,
                        41 * width,
                        41 * height
                )
        );

        // The list of outlines that have been generated.
        List<AbstractOutline> outlines = new ArrayList<>();

        // Insert the points into the plane one by one, using the outline for placement resolution.
        for(WeightedPoint p : sortedPoints) {
            OutlineRectangle rectangle = getOutlineRectangle(p.c, p);
            List<OutlineDimensions> intersections = tree.query(rectangle);

            // The resulting placements.
            OutlineDimensions result;
            Point2d placement;

            if(!intersections.isEmpty()) {

                if(intersections.size() > 1) {
                    System.out.println("Intersecting with multiple outline groups.");
                }

                // If we have more than 1, we can choose.

                // Find the associated outline.
                intersections.sort(Comparator.comparingInt(a -> a.owner.getRectangles().size()));
                SimpleOutline outline = (SimpleOutline) intersections.get(0).owner;

                // Create the buffered outline and project onto it.
                BufferedOutline bOutline = new BufferedOutline(outline, 0.5 * p.w);
                placement = bOutline.projectAndSelect(p, centre);
//                placement = bOutline.projectAndSelect(p);

                // Place the new rectangle.
                OutlineDimensions originalDimensions = outline.getDimensions();
                outline.insert(getOutlineRectangle(placement, p));
                result = outline.getDimensions();

                // Remove the original before adding the new one.
                tree.delete(originalDimensions);
            } else {
                // Create a new outline, which will set a pointer in the rectangle to the outline.
                SimpleOutline outline = new SimpleOutline(rectangle);
                outlines.add(outline);
                placement = p.c;
                result = outline.getDimensions();
            }

            // Add the chosen rectangle to the tree and result.
            points.add(HalfGridPoint.make(placement, p));
            tree.insert(result);
        }

        System.out.println("We have generated " + outlines.size() + " outline groups.");

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
}
