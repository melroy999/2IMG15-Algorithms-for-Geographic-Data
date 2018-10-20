package agd.solver;

import agd.data.input.WeightedPoint;
import agd.data.outlines.OutlineRectangle;
import org.junit.jupiter.api.Test;
import sun.reflect.generics.tree.Tree;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class OutlineTestTest {
    private static WeightedPoint p = new WeightedPoint(0, 0, 0, 0);

    @Test
    void setMinus1() {
        TreeSet<OutlineTest.Interval> source = new TreeSet<>();
        source.add(new OutlineTest.Interval(1, 3));
        source.add(new OutlineTest.Interval(4, 7));
        source.add(new OutlineTest.Interval(8, 10));
        source.add(new OutlineTest.Interval(11, 13));
        TreeSet<OutlineTest.Interval> minus = new TreeSet<>();
        minus.add(new OutlineTest.Interval(2, 5));
        minus.add(new OutlineTest.Interval(6, 9));

        TreeSet<OutlineTest.Interval> result = OutlineTest.setMinus(source, minus);
        result.forEach(System.out::println);
    }

    @Test
    void setMinus2() {
        TreeSet<OutlineTest.Interval> source = new TreeSet<>();
        source.add(new OutlineTest.Interval(1, 3));
        source.add(new OutlineTest.Interval(4, 7));
        source.add(new OutlineTest.Interval(8, 10));
        source.add(new OutlineTest.Interval(11, 13));
        TreeSet<OutlineTest.Interval> minus = new TreeSet<>();
        minus.add(new OutlineTest.Interval(-2, 0));
        minus.add(new OutlineTest.Interval(2, 5));
        minus.add(new OutlineTest.Interval(6, 9));
        minus.add(new OutlineTest.Interval(12, 14));

        TreeSet<OutlineTest.Interval> result = OutlineTest.setMinus(source, minus);
        result.forEach(System.out::println);
    }

    @Test
    void flush1() {
        OutlineTest.Interval[] intervals = new OutlineTest.Interval[]{
            new OutlineTest.Interval(0, 2),
            new OutlineTest.Interval(2, 3),
            new OutlineTest.Interval(3, 5)
        };

        TreeSet<OutlineTest.Interval> starting = new TreeSet<>();
        starting.add(intervals[0]);
        starting.add(intervals[1]);
        starting.add(intervals[2]);
        TreeSet<OutlineTest.Interval> ending = new TreeSet<>();
        TreeSet<OutlineTest.Interval> active = new TreeSet<>();
        active.add(intervals[0]);
        active.add(intervals[1]);
        active.add(intervals[2]);

        TreeSet<OutlineTest.Interval> result = OutlineTest.flush(starting, ending, active);
        result.forEach(System.out::println);
    }

    @Test
    void flush2() {
        OutlineTest.Interval[] intervals = new OutlineTest.Interval[]{
                new OutlineTest.Interval(0, 2),
                new OutlineTest.Interval(2, 3),
                new OutlineTest.Interval(3, 5),
                new OutlineTest.Interval(2, 3)
        };

        TreeSet<OutlineTest.Interval> starting = new TreeSet<>();
        starting.add(intervals[3]);
        TreeSet<OutlineTest.Interval> ending = new TreeSet<>();
        ending.add(intervals[1]);
        TreeSet<OutlineTest.Interval> active = new TreeSet<>();
        active.add(intervals[0]);
        active.add(intervals[3]);
        active.add(intervals[2]);

        TreeSet<OutlineTest.Interval> result = OutlineTest.flush(starting, ending, active);
        result.forEach(System.out::println);
    }

    @Test
    void construct() {
        TreeSet<OutlineRectangle> rectangles = new TreeSet<>();
        rectangles.add(new OutlineRectangle(0, 1, 2, p));
        rectangles.add(new OutlineRectangle(0, 3, 1, p));
        rectangles.add(new OutlineRectangle(1, 3, 1, p));
        rectangles.add(new OutlineRectangle(0, 4, 2, p));
        rectangles.add(new OutlineRectangle(2, 0, 4, p));
        rectangles.add(new OutlineRectangle(4, 4, 2, p));
        rectangles.add(new OutlineRectangle(4, 8, 3, p));
        rectangles.add(new OutlineRectangle(7, 10, 1, p));
        rectangles.add(new OutlineRectangle(7, 6, 4, p));
        rectangles.add(new OutlineRectangle(6, 2, 4, p));
        rectangles.add(new OutlineRectangle(8, 10, 4, p));
        rectangles.add(new OutlineRectangle(8, 14, 4, p));
        rectangles.add(new OutlineRectangle(12, 2, 1, p));
        rectangles.add(new OutlineRectangle(11, 3, 4, p));
        rectangles.add(new OutlineRectangle(12, 7, 4, p));
        rectangles.add(new OutlineRectangle(12, 11, 1, p));
        rectangles.add(new OutlineRectangle(13, 11, 2, p));
        rectangles.add(new OutlineRectangle(15, 12, 1, p));
        rectangles.add(new OutlineRectangle(15, 5, 2, p));
        rectangles.add(new OutlineRectangle(17, 5, 3, p));
        rectangles.add(new OutlineRectangle(17, 3, 2, p));
        rectangles.add(new OutlineRectangle(20, 7, 1, p));

        rectangles.forEach(r -> System.out.println("\\draw (" + r.x + ", " + r.y + ") rectangle (" + (r.x + r.width) + ", " + (r.y + r.height) + ");"));

        OutlineTest.createOutline(rectangles);
    }
}