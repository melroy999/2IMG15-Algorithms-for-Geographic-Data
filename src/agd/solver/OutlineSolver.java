package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.data.outline.BufferedOutline;
import agd.data.outline.Outline;
import agd.data.outline.OutlineRectangle;
import agd.data.output.HalfGridPoint;
import agd.data.util.QuadTreeNode;
import agd.math.Point2d;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OutlineSolver extends AbstractSolver {
    /**
     * Solve the given problem instance.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points   The list of placed points.
     */
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {
        // Find the centre of mass.
        Point2d centre = new Point2d();
        for(WeightedPoint p : instance.getPoints()) {
            centre = centre.add(p.c);
        }
        centre = centre.scale(1d / instance.getPoints().size());

        // Find the distance between the centre point and all of the points, and sort on distance.
        Point2d finalCentre = centre;
        List<WeightedPoint> sortedPoints = instance.getPoints().stream().sorted(
                Comparator.comparingDouble(p -> p.distance(finalCentre))).collect(Collectors.toList());

        // Create a quadtree in which we will check for overlapping rectangles.
        // TODO choose a reliable bound for the quadtree.

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

        // Insert the points into the plane one by one, using the outline for placement resolution.
        for(WeightedPoint p : sortedPoints) {
            OutlineRectangle rectangle = p.getOutlineRectangle();
            List<OutlineRectangle> intersections = tree.query(rectangle);

            // The resulting placements.
            OutlineRectangle result;
            Point2d placement;

            if(!intersections.isEmpty()) {
                // Which outlines do we intersect with?
                List<Outline> outlines = intersections.stream().map(
                        OutlineRectangle::getOutline).distinct().collect(Collectors.toList());

                // Find the associated outline.
                Outline outline = outlines.get(0);
                BufferedOutline bOutline = new BufferedOutline(outline, 0.5 * p.w);
//                placement = bOutline.projectAndSelect(p, centre);
                placement = bOutline.projectAndSelect(p);
                result = p.getOutlineRectangle(placement);
                outline.insert(result);
            } else {
                // Create a new outline, which will set a pointer in the rectangle to the outline.
                new Outline(rectangle);
                placement = p.c;
                result = rectangle;
            }

            // Add the chosen rectangle to the tree and result.
            points.add(HalfGridPoint.make(placement, p));
            tree.insert(result);
        }
    }
}
