package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import agd.math.Point2d;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
class LineSegmentTest {

    // The line segments.
    private List<LineSegment> segments;

    @BeforeEach
    void setUp() {
        OutlineEdge[] edges = new OutlineEdge[] {
            new OutlineEdge(new Point2d(3, 0), OutlineEdge.Direction.LEFT),
            new OutlineEdge(new Point2d(0, 0), OutlineEdge.Direction.UP),
            new OutlineEdge(new Point2d(0, 3), OutlineEdge.Direction.RIGHT),
            new OutlineEdge(new Point2d(3, 3), OutlineEdge.Direction.DOWN)
        };

        for(int i = 0; i < edges.length; i++) {
            edges[i].setNext(edges[(i + 1) % edges.length]);
        }

        segments = Arrays.stream(edges).map(LineSegment::new).collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Neighboring edges within a simple outline should not have intersections with one another.
     */
    @Test
    void selfIntersectionTest() {
        // None of the edges should intersect with neighboring edges of the same simple outline.
        for(int i = 0; i < segments.size(); i++) {
            boolean result = segments.get(i).intersects(segments.get((i + 1) % segments.size()));
            Assertions.assertFalse(result, "Neighboring edges in a simple outline report intersections.");

            result = segments.get((i + 1) % segments.size()).intersects(segments.get(i));
            Assertions.assertFalse(result, "Neighboring edges in a simple outline report intersections.");
        }
    }

    /**
     * Check whether we can detect simple vertical intersections.
     */
    @Test
    void simpleVerticalIntersectionTest() {
        OutlineEdge e = new OutlineEdge(new Point2d(1, -1), OutlineEdge.Direction.UP);
        OutlineEdge next = new OutlineEdge(new Point2d(1, 4), OutlineEdge.Direction.LEFT);
        e.setNext(next);
        LineSegment segment = new LineSegment(e);

        // We should have two different intersections, namely both the horizontal lines.
        for(LineSegment s : segments) {
            boolean result = segment.intersects(s);

            if(s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }

            result = s.intersects(segment);

            if(s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }
        }
    }

    /**
     * Check whether we can detect simple horizontal intersections.
     */
    @Test
    void simpleHorizontalIntersectionTest() {
        OutlineEdge e = new OutlineEdge(new Point2d(-1, 1), OutlineEdge.Direction.RIGHT);
        OutlineEdge next = new OutlineEdge(new Point2d(4, 1), OutlineEdge.Direction.DOWN);
        e.setNext(next);
        LineSegment segment = new LineSegment(e);

        // We should have two different intersections, namely both the horizontal lines.
        for(LineSegment s : segments) {
            boolean result = segment.intersects(s);

            if(!s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }

            result = s.intersects(segment);

            if(!s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }
        }
    }

    /**
     * Check whether we can detect intersections with endpoints on edges in vertical cases.
     */
    @Test
    void borderVerticalIntersectionTest() {
        OutlineEdge e = new OutlineEdge(new Point2d(1, 0), OutlineEdge.Direction.UP);
        OutlineEdge next = new OutlineEdge(new Point2d(1, 3), OutlineEdge.Direction.LEFT);
        e.setNext(next);
        LineSegment segment = new LineSegment(e);

        // We should have two different intersections, namely both the horizontal lines.
        for(LineSegment s : segments) {
            boolean result = segment.intersects(s);

            if(s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }

            result = s.intersects(segment);

            if(s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }
        }
    }

    /**
     * Check whether we can detect intersections with endpoints on edges in horizontal cases.
     */
    @Test
    void borderHorizontalIntersectionTest() {
        OutlineEdge e = new OutlineEdge(new Point2d(0, 1), OutlineEdge.Direction.RIGHT);
        OutlineEdge next = new OutlineEdge(new Point2d(3, 1), OutlineEdge.Direction.DOWN);
        e.setNext(next);
        LineSegment segment = new LineSegment(e);

        // We should have two different intersections, namely both the horizontal lines.
        for(LineSegment s : segments) {
            boolean result = segment.intersects(s);

            if(!s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }

            result = s.intersects(segment);

            if(!s.edge.getDirection().isHorizontal) {
                Assertions.assertTrue(result, "Did not detect an intersection, while there should be one.");
            } else {
                // There should be no intersection.
                Assertions.assertFalse(result, "Detected an intersection between two vertical lines.");
            }
        }
    }
}