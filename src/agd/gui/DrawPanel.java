package agd.gui;

import agd.data.input.WeightedPoint;
import agd.data.output.HalfGridPoint;
import agd.data.output.ProblemSolution;
import agd.gui.util.Line;
import agd.gui.util.Point;
import agd.gui.util.Rectangle;
import agd.gui.util.Square;
import agd.data.input.ProblemInstance;
import agd.math.Point2d;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Set;

/**
 * The panel in which the result and intermediate steps of our algorithm can be displayed.
 */
public class DrawPanel extends JPanel {
    // Reference to the GUI this panel is part of.
    private final GUI gui;

    // The clearance we want at the borders.
    private static final int clearance = 250;

    /**
     * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
     *
     * @param gui The gui this panel is part of.
     */
    DrawPanel(GUI gui) {
        this.gui = gui;
    }

    /**
     * Draw onto the canvas.
     *
     * @param g The graphics object to drawPoints on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Convert to a two-dimensional space graphics object.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Only draw the following if an instance is set in the core.
        if(gui.core.instance != null && gui.core.solution != null) {
            // Gather the information we require.
            ProblemInstance instance = gui.core.instance;

            ProblemSolution solution = gui.core.solution;
            List<HalfGridPoint> placedPoints = solution.getPoints();
            Set<Integer> invalids = solution.getInvalidPoints();

            // Save the old transform.
            AffineTransform old = g2.getTransform();

            // The scaling factor we should use.
            Dimension dimensions = gui.getDisplayPanelDimensions();
            double scale = (dimensions.height - 2 * clearance) / (double) (instance.max_y - instance.min_y);

            // Translate to an inverted y-axis.
            g2.translate(0, getHeight() - 1);

            // Set a scaling that will fit our data, inverting the y-axis.
            g2.scale(1, -1);

            // Translate to the correct clearance.
            g2.translate(clearance, clearance);

            // Draw what we need.
            drawRectangles(g2, scale, placedPoints, invalids);
            drawErrorLines(g2, scale, placedPoints);
            drawPoints(g2, scale, placedPoints);

            // Restore the transform.
            g2.setTransform(old);
        }
    }

    /**
     * Draw the points in our program as circles.
     *
     * @param g The graphics object to draw the object with.
     * @param s The scale at which the objects should be drawn.
     * @param points The list of points that are given in the problem solution.
     */
    private void drawPoints(Graphics2D g, double s, List<HalfGridPoint> points) {
        points.forEach(p -> new Point(p.o.scale(s), s * 0.07).draw(g));
        points.forEach(p -> new Point(p.point().scale(s), Color.red, s * 0.07).draw(g));
    }

    /**
     * Draw the points in our program as circles.
     *
     * @param g The graphics object to draw the object with.
     * @param s The scale at which the objects should be drawn.
     * @param points The list of points that are given in the problem solution.
     * @param invalids The points that are marked as invalid.
     */
    private void drawRectangles(Graphics2D g, double s, List<HalfGridPoint> points, Set<Integer> invalids) {
        ProblemInstance i = gui.core.instance;

        new Rectangle(s * i.min_x, s * i.min_y, s * (i.max_x - i.min_x), s * (i.max_y - i.min_y)).draw(g);
        points.forEach(p ->
        {
            Point2d q = p.point().sub(new Point2d(0.5 * p.o.w, 0.5 * p.o.w)).scale(s);
            new Square(q.x, q.y, s * p.o.w, invalids.contains(p.o.i) ? Color.red : Color.black).draw(g);
        });
    }

    /**
     * Draw the points in our program as circles.
     *
     * @param g The graphics object to draw the object with.
     * @param s The scale at which the objects should be drawn.
     * @param points The list of points that are given in the problem solution.
     */
    private void drawErrorLines(Graphics2D g, double s, List<HalfGridPoint> points) {
        points.forEach(p -> new Line(p.point().scale(s), p.o.scale(s)).draw(g));
    }
}
