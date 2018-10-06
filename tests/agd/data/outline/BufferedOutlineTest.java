package agd.data.outline;

import agd.data.sweepline2.LineSegment;
import agd.math.Point2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("Duplicates")
class BufferedOutlineTest {

    // The line segments.
    private Outline outline;

    @BeforeEach
    void setUp() {
        OutlineEdge[] edges = new OutlineEdge[] {
                new OutlineEdge(new Point2d(6, 0), OutlineEdge.Direction.LEFT),
                new OutlineEdge(new Point2d(0, 0), OutlineEdge.Direction.UP),
                new OutlineEdge(new Point2d(0, 8), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(2, 8), OutlineEdge.Direction.DOWN),
                new OutlineEdge(new Point2d(2, 6), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(4, 6), OutlineEdge.Direction.UP),
                new OutlineEdge(new Point2d(4, 8), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(6, 8), OutlineEdge.Direction.DOWN)
        };

        for(int i = 0; i < edges.length; i++) {
            edges[i].setNext(edges[(i + 1) % edges.length]);
        }

        outline = new Outline(edges[0]);
    }

    /**
     * Create a buffered outline with width zero, which should result in an equal shape.
     */
    @Test
    void constructUnitBufferedOutline() {
        BufferedOutline bOutline = new BufferedOutline(outline, 0);
        bOutline.sanitizeOutline();

        List<OutlineEdge> edges = new ArrayList<>();
        outline.getEdge().iterator().forEachRemaining(edges::add);

        List<OutlineEdge> bEdges = new ArrayList<>();
        bOutline.getEdge().iterator().forEachRemaining(bEdges::add);

        Assertions.assertEquals(edges.size(), bEdges.size(), "The two outlines are of unequal length.");
        for(int i = 0; i < edges.size(); i++) {
            Assertions.assertEquals(edges.get(i), bEdges.get(i));
        }
    }

    /**
     * Create a buffered outline with width one.
     */
    @Test
    void constructWidthOneBufferedOutline() {
        BufferedOutline bOutline = new BufferedOutline(outline, 1);
        bOutline.sanitizeOutline();

        OutlineEdge[] edges = new OutlineEdge[] {
                new OutlineEdge(new Point2d(7, -1), OutlineEdge.Direction.LEFT),
                new OutlineEdge(new Point2d(-1, -1), OutlineEdge.Direction.UP),
                new OutlineEdge(new Point2d(-1, 9), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(3, 9), OutlineEdge.Direction.DOWN),
                new OutlineEdge(new Point2d(3, 7), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(3, 7), OutlineEdge.Direction.UP),
                new OutlineEdge(new Point2d(3, 9), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(7, 9), OutlineEdge.Direction.DOWN)
        };

        for(int i = 0; i < edges.length; i++) {
            edges[i].setNext(edges[(i + 1) % edges.length]);
        }

        List<OutlineEdge> bEdges = new ArrayList<>();
        bOutline.getEdge().iterator().forEachRemaining(bEdges::add);

        Assertions.assertEquals(edges.length, bEdges.size(), "The two outlines are of unequal length.");
        for(int i = 0; i < bEdges.size(); i++) {
            Assertions.assertEquals(edges[i], bEdges.get(i));
        }
    }

    /**
     * Create a buffered outline with width one.
     */
    @Test
    void constructWidthTwoBufferedOutline() {
        BufferedOutline bOutline = new BufferedOutline(outline, 2);
        bOutline.sanitizeOutline();

        OutlineEdge[] edges = new OutlineEdge[] {
                new OutlineEdge(new Point2d(8, -2), OutlineEdge.Direction.LEFT),
                new OutlineEdge(new Point2d(-2, -2), OutlineEdge.Direction.UP),
                new OutlineEdge(new Point2d(-2, 10), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(8, 10), OutlineEdge.Direction.DOWN)
        };

        for(int i = 0; i < edges.length; i++) {
            edges[i].setNext(edges[(i + 1) % edges.length]);
        }

        List<OutlineEdge> bEdges = new ArrayList<>();
        bOutline.getEdge().iterator().forEachRemaining(bEdges::add);

        Assertions.assertEquals(edges.length, bEdges.size(), "The two outlines are of unequal length.");
        for(int i = 0; i < bEdges.size(); i++) {
            Assertions.assertEquals(edges[i], bEdges.get(i));
        }
    }

    /**
     * Create a buffered outline with width one.
     */
    @Test
    void constructWidthThreeBufferedOutline() {
        BufferedOutline bOutline = new BufferedOutline(outline, 2);
        bOutline.sanitizeOutline();

        OutlineEdge[] edges = new OutlineEdge[] {
                new OutlineEdge(new Point2d(9, -3), OutlineEdge.Direction.LEFT),
                new OutlineEdge(new Point2d(-3, -3), OutlineEdge.Direction.UP),
                new OutlineEdge(new Point2d(-3, 11), OutlineEdge.Direction.RIGHT),
                new OutlineEdge(new Point2d(9, 11), OutlineEdge.Direction.DOWN)
        };

        for(int i = 0; i < edges.length; i++) {
            edges[i].setNext(edges[(i + 1) % edges.length]);
        }

        List<OutlineEdge> bEdges = new ArrayList<>();
        bOutline.getEdge().iterator().forEachRemaining(bEdges::add);

        Assertions.assertEquals(edges.length, bEdges.size(), "The two outlines are of unequal length.");
        for(int i = 0; i < bEdges.size(); i++) {
            Assertions.assertEquals(edges[i], bEdges.get(i));
        }
    }
}