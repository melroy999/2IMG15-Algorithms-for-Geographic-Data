package agd.gui;

import agd.store.gui.Line;
import agd.store.gui.Point;
import agd.store.gui.Rectangle;
import agd.store.gui.Square;
import agd.store.instance.ProblemInstance;
import agd.store.instance.WeightedPointList;
import agd.store.math.Point2d;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Set;

/**
 * The panel in which the result and intermediate steps of our algorithm can be displayed.
 */
public class DrawPanel extends JPanel {
    // Reference to the GUI this panel is part of.
    private final GUI gui;

    // The clearance we want at the borders.
    private static final int clearance = 120;

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
        if(gui.core.instance != null) {
            // Gather the information we require.
            ProblemInstance instance = gui.core.instance;
            WeightedPointList points = instance.getPoints();
            Set<Integer> invalids = instance.getInvalidPoints();

            // Save the old transform.
            AffineTransform old = g2.getTransform();

            // The scaling factor we should use.
            Dimension dimensions = gui.getDisplayPanelDimensions();
            double scale = (dimensions.height - 2 * clearance) / (double) (instance.maxy - instance.miny);

            // Translate to an inverted y-axis.
            g2.translate(0, getHeight() - 1);

            // Set a scaling that will fit our data, inverting the y-axis.
            g2.scale(1, -1);

            // Translate to the correct clearance.
            g2.translate(clearance, clearance);

            // Draw what we need.
            drawRectangles(g2, scale, points, invalids);
            drawErrorLines(g2, scale, points);
            drawPoints(g2, scale, points);

            // Restore the transform.
            g2.setTransform(old);
        }
    }

    private void drawPoints(Graphics2D g, double s, WeightedPointList points) {
        points.forEach(p -> new Point(p.scale(s), s * 0.07).draw(g));
        points.forEach(p -> new Point(p.c.scale(s), Color.red, s * 0.07).draw(g));
    }

    private void drawRectangles(Graphics2D g, double s, WeightedPointList points, Set<Integer> invalids) {
        ProblemInstance i = gui.core.instance;

        new Rectangle(s * i.minx, s * i.miny, s * (i.maxx - i.minx), s * (i.maxy - i.miny)).draw(g);
        points.forEach(p ->
        {
            Point2d q = new Point2d(p.bl.x, p.bl.y).scale(s);
            new Square(q.x, q.y, s * p.weight, invalids.contains(p.id) ? Color.red : Color.black).draw(g);
        });
    }

    private void drawErrorLines(Graphics2D g, double s, WeightedPointList points) {
        points.forEach(p -> new Line(p.scale(s), p.c.scale(s)).draw(g));
    }
}
