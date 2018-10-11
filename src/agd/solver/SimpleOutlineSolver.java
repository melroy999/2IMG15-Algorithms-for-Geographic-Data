package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.data.outlines.*;
import agd.data.output.HalfGridPoint;
import agd.data.util.QuadTreeNode;
import agd.math.Point2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleOutlineSolver extends AbstractSolver {
    @Override
    public void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points) {
        // Find the centre of mass.
        Point2d centre = new Point2d();
        for(WeightedPoint p : instance.getPoints()) {
            centre = centre.add(p.c);
        }
        centre = centre.scale(1d / instance.getPoints().size());

        // Find the distance between the centre point and all of the points, and sort on distance.
        List<WeightedPoint> sortedPoints = getSortOnDefaultCentroid(instance.getPoints(), centre);
//        List<WeightedPoint> sortedPoints = getSortOnCornerPoints(instance.getPoints(), centre);
//        List<WeightedPoint> sortedPoints = getSortOnClosestPointOnBorder(instance.getPoints(), centre);
//        List<WeightedPoint> sortedPoints = getSortOnManhattanCentroid(instance.getPoints(), centre);

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
            System.out.println("Placing point " + p);

            OutlineRectangle rectangle = getOutlineRectangle(p.c, p);
            List<OutlineRectangle> intersections = tree.query(rectangle);

            // The resulting placements.
            OutlineRectangle result;
            Point2d placement;

            if(!intersections.isEmpty()) {
                // Which outlines do we intersect with?
                List<AbstractOutline> outlines = intersections.stream().map(
                        OutlineRectangle::getOutline).distinct().collect(Collectors.toList());

                if(outlines.size() > 1) {
                    System.out.println("Intersecting with multiple outline groups.");
                }

                // Find the associated outline.
//                System.out.println();
                SimpleOutline outline = (SimpleOutline) outlines.get(0);
//                System.out.println(outline.toLatexFigure());
                BufferedOutline bOutline = new BufferedOutline(outline, 0.5 * p.w);

                placement = bOutline.projectAndSelect(p, centre);
//                placement = bOutline.projectAndSelect(p);

                result = getOutlineRectangle(placement, p);
                outline.insert(result);
            } else {
                // Create a new outline, which will set a pointer in the rectangle to the outline.
                new SimpleOutline(rectangle);
                placement = p.c;
                result = rectangle;
            }

            // Add the chosen rectangle to the tree and result.
            points.add(HalfGridPoint.make(placement, p));

            if(result.getOutline() == null) {
                System.out.println();
            }

            tree.insert(result);
        }
    }

    /**
     * Convert the preferred placement of a weighted point to a outline rectangle.
     *
     * @param p The chosen placement for the center point.
     * @return An outline rectangle with the point c at the center.
     */
    private static OutlineRectangle getOutlineRectangle(Point2d p, WeightedPoint o) {
        return new OutlineRectangle(
                (int) Math.round(p.x - 0.5 * o.w),
                (int) Math.round(p.y - 0.5 * o.w),
                o.w,
                o
        );
    }

    private static List<WeightedPoint> getSortOnDefaultCentroid(List<WeightedPoint> points, final Point2d c) {
        return points.stream().sorted(Comparator.comparingDouble(p -> p.distance2(c))).collect(Collectors.toList());
    }

    private static List<WeightedPoint> getSortOnManhattanCentroid(List<WeightedPoint> points, final Point2d c) {
        return points.stream().sorted(Comparator.comparingDouble(p -> p.manhattan(c))).collect(Collectors.toList());
    }

    private static List<WeightedPoint> getSortOnCornerPoints(List<WeightedPoint> points, final Point2d c) {
        return points.stream().sorted(Comparator.comparingDouble(p -> getClosestCornerPoint(p, c).distance2(c))).collect(Collectors.toList());
    }

    private static List<WeightedPoint> getSortOnClosestPointOnBorder(List<WeightedPoint> points, final Point2d c) {
        return points.stream().sorted(Comparator.comparingDouble(p -> getClosestPointOnBorder(p, c).distance2(c))).collect(Collectors.toList());
    }

    private static Point2d getClosestCornerPoint(WeightedPoint p, final Point2d c) {
        double hw = 0.5 * p.w;
        Point2d[] points = new Point2d[] {
                new Point2d(p.x + hw, p.y + hw),
                new Point2d(p.x - hw, p.y + hw),
                new Point2d(p.x + hw, p.y - hw),
                new Point2d(p.x - hw, p.y - hw)
        };

        double min = Double.MAX_VALUE;
        Point2d best = null;

        for(Point2d q : points) {
            double distance = c.distance2(q);
            if(distance < min) {
                min = distance;
                best = q;
            }
        }

        return best;
    }

    private static Point2d getClosestPointOnBorder(WeightedPoint p, final Point2d c) {
        OutlineRectangle r = getOutlineRectangle(p.c, p);

        double min = Double.MAX_VALUE;
        Point2d best = null;

        for(Edge e : r.createOutline(Edge.Direction.LEFT)) {
            Point2d q = e.project(c);
            double distance = c.distance2(q);
            if(distance < min) {
                min = distance;
                best = q;
            }
        }

        return best;
    }
}
