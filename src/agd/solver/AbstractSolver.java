package agd.solver;

import agd.data.input.ProblemInstance;
import agd.data.input.WeightedPoint;
import agd.data.outlines.AbstractOutline;
import agd.data.outlines.Edge;
import agd.data.outlines.OutlineRectangle;
import agd.data.output.HalfGridPoint;
import agd.data.output.ProblemSolution;
import agd.data.util.QuadTreeNode;
import agd.math.Point2d;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * A class that can be extended to create solvers for the problem.
 */
public abstract class AbstractSolver {
    public enum SortingOptions {
        MANHATTAN_CENTROID, CENTROID, CORNER, CLOSEST_POINT, FURTHEST, NONE, SIZE_ASC, SIZE_DESC, X, Y, ROTATION, MAX_BASED, MIN_BASED
    }

    static QuadTreeNode<OutlineRectangle> initializeQuadTree(ProblemInstance instance) {
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

    static Point2d getCentreAndInit(ProblemInstance instance, ArrayList<HalfGridPoint> points) {
        Point2d centre = new Point2d();
        for(WeightedPoint p : instance.getPoints()) {
            centre = centre.add(p.c);
        }
        centre = centre.scale(1d / instance.getPoints().size());

        // Initialize the array of points to hold null for every entry.
        for(int i = 0; i < instance.getPoints().size(); i++) {
            points.add(null);
        }
        return centre;
    }

    static Comparator<WeightedPoint> getPointComparator(SortingOptions option, final Point2d c) {
        switch (option) {
            case CENTROID:
                return getCentroidComparator(c);
            case CORNER:
                return getCornerPointComparator(c);
            case CLOSEST_POINT:
                return getBorderPointComparator(c);
            case FURTHEST:
                return getFurthestPointComparator(c);
            case NONE:
                return Comparator.comparingInt(a -> a.i);
            case SIZE_ASC:
                return Comparator.comparingInt(a -> a.w);
            case SIZE_DESC:
                return (a1, a2) -> -Integer.compare(a1.w, a2.w);
            case X:
                return Comparator.comparingDouble(a -> a.x);
            case Y:
                return Comparator.comparingDouble(a -> a.y);
            case ROTATION:
                return getRotationalComparator(c);
            case MAX_BASED:
                return Comparator.comparingDouble(a -> Math.max(Math.abs(a.x - c.x), Math.abs(a.y - c.y)));
            case MIN_BASED:
                return Comparator.comparingDouble(a -> Math.min(Math.abs(a.x - c.x), Math.abs(a.y - c.y)));
            case MANHATTAN_CENTROID:
            default:
                return getManhattanCentroidComparator(c);
        }
    }

    private static Comparator<WeightedPoint> getRotationalComparator(final Point2d c) {
        return Comparator.comparingDouble(a -> getAngleDegree(c, a));
    }

    private static double getAngleDegree(Point2d origin, Point2d target) {
        double n = 270 - (Math.atan2(origin.y - target.y, origin.x - target.x)) * 180 / Math.PI;
        return n % 360;
    }

    private static Comparator<WeightedPoint> getCentroidComparator(final Point2d c) {
        return Comparator.comparingDouble(p -> p.distance2(c));
    }

    private static Comparator<WeightedPoint> getManhattanCentroidComparator(final Point2d c) {
        return Comparator.comparingDouble(p -> p.manhattan(c));
    }

    private static Comparator<WeightedPoint> getCornerPointComparator(final Point2d c) {
        return Comparator.comparingDouble(p -> getClosestCornerPoint(p, c).distance2(c));
    }

    private static Comparator<WeightedPoint> getBorderPointComparator(final Point2d c) {
        return Comparator.comparingDouble(p -> getClosestPointOnBorder(p, c).distance2(c));
    }

    private static Comparator<WeightedPoint> getFurthestPointComparator(final Point2d c) {
        return Comparator.comparingDouble(p -> getFurthestCornerPoint(p, c).distance2(c));
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

    private static Point2d getFurthestCornerPoint(WeightedPoint p, final Point2d c) {
        double hw = 0.5 * p.w;
        Point2d[] points = new Point2d[] {
                new Point2d(p.x + hw, p.y + hw),
                new Point2d(p.x - hw, p.y + hw),
                new Point2d(p.x + hw, p.y - hw),
                new Point2d(p.x - hw, p.y - hw)
        };

        double max = -Double.MAX_VALUE;
        Point2d best = null;

        for(Point2d q : points) {
            double distance = c.distance2(q);
            if(distance > max) {
                max = distance;
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

    public static void printSolution(Set<AbstractOutline> outlines) {

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

    /**
     * Solve the given problem instance.
     *
     * @param instance The problem instance that contains all the required data.
     * @param points The list of placed points.
     */
    public abstract void solve(ProblemInstance instance, ArrayList<HalfGridPoint> points);
}
