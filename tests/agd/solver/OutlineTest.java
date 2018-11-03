package agd.solver;

import agd.data.input.WeightedPoint;
import agd.data.outlines.OutlineRectangle;
import org.junit.jupiter.api.Test;
import outlines.Edge;
import outlines.Outline;

import java.util.List;
import java.util.TreeSet;

class OutlineTest {
    private static WeightedPoint p = new WeightedPoint(0, 0, 0, 0);

//    @Test
//    void setMinus1() {
//        TreeSet<Outline.Interval> source = new TreeSet<>();
//        source.add(new Outline.Interval(1, 3));
//        source.add(new Outline.Interval(4, 7));
//        source.add(new Outline.Interval(8, 10));
//        source.add(new Outline.Interval(11, 13));
//        TreeSet<Outline.Interval> minus = new TreeSet<>();
//        minus.add(new Outline.Interval(2, 5));
//        minus.add(new Outline.Interval(6, 9));
//
//        TreeSet<Outline.Interval> result = Outline.setMinus(source, minus);
//        result.forEach(System.out::println);
//    }
//
//    @Test
//    void setMinus2() {
//        TreeSet<Outline.Interval> source = new TreeSet<>();
//        source.add(new Outline.Interval(1, 3));
//        source.add(new Outline.Interval(4, 7));
//        source.add(new Outline.Interval(8, 10));
//        source.add(new Outline.Interval(11, 13));
//        TreeSet<Outline.Interval> minus = new TreeSet<>();
//        minus.add(new Outline.Interval(-2, 0));
//        minus.add(new Outline.Interval(2, 5));
//        minus.add(new Outline.Interval(6, 9));
//        minus.add(new Outline.Interval(12, 14));
//
//        TreeSet<Outline.Interval> result = Outline.setMinus(source, minus);
//        result.forEach(System.out::println);
//    }
//
//    @Test
//    void flush1() {
//        Outline.Interval[] intervals = new Outline.Interval[]{
//            new Outline.Interval(0, 2),
//            new Outline.Interval(2, 3),
//            new Outline.Interval(3, 5)
//        };
//
//        TreeSet<Outline.Interval> starting = new TreeSet<>();
//        starting.add(intervals[0]);
//        starting.add(intervals[1]);
//        starting.add(intervals[2]);
//        TreeSet<Outline.Interval> ending = new TreeSet<>();
//        TreeSet<Outline.Interval> active = new TreeSet<>();
//        active.add(intervals[0]);
//        active.add(intervals[1]);
//        active.add(intervals[2]);
//
//        TreeSet<Outline.Interval> result = Outline.flush(starting, ending, active);
//        result.forEach(System.out::println);
//    }
//
//    @Test
//    void flush2() {
//        Outline.Interval[] intervals = new Outline.Interval[]{
//                new Outline.Interval(0, 2),
//                new Outline.Interval(2, 3),
//                new Outline.Interval(3, 5),
//                new Outline.Interval(2, 3)
//        };
//
//        TreeSet<Outline.Interval> starting = new TreeSet<>();
//        starting.add(intervals[3]);
//        TreeSet<Outline.Interval> ending = new TreeSet<>();
//        ending.add(intervals[1]);
//        TreeSet<Outline.Interval> active = new TreeSet<>();
//        active.add(intervals[0]);
//        active.add(intervals[3]);
//        active.add(intervals[2]);
//
//        TreeSet<Outline.Interval> result = Outline.flush(starting, ending, active);
//        result.forEach(System.out::println);
//    }

//    @Test
//    void constructOutline() {
//        TreeSet<OutlineRectangle> rectangles = new TreeSet<>();
//        rectangles.add(new OutlineRectangle(0, 1, 2, p));
//        rectangles.add(new OutlineRectangle(0, 3, 1, p));
//        rectangles.add(new OutlineRectangle(1, 3, 1, p));
//        rectangles.add(new OutlineRectangle(0, 4, 2, p));
//        rectangles.add(new OutlineRectangle(2, 0, 4, p));
//        rectangles.add(new OutlineRectangle(4, 4, 2, p));
//        rectangles.add(new OutlineRectangle(4, 8, 3, p));
//        rectangles.add(new OutlineRectangle(7, 10, 1, p));
//        rectangles.add(new OutlineRectangle(7, 6, 4, p));
//        rectangles.add(new OutlineRectangle(6, 2, 4, p));
//        rectangles.add(new OutlineRectangle(8, 10, 4, p));
//        rectangles.add(new OutlineRectangle(8, 14, 4, p));
//        rectangles.add(new OutlineRectangle(12, 2, 1, p));
//        rectangles.add(new OutlineRectangle(11, 3, 4, p));
//        rectangles.add(new OutlineRectangle(12, 7, 4, p));
//        rectangles.add(new OutlineRectangle(12, 11, 1, p));
//        rectangles.add(new OutlineRectangle(13, 11, 2, p));
//        rectangles.add(new OutlineRectangle(15, 12, 1, p));
//        rectangles.add(new OutlineRectangle(15, 5, 2, p));
//        rectangles.add(new OutlineRectangle(17, 5, 3, p));
//        rectangles.add(new OutlineRectangle(17, 3, 2, p));
//        rectangles.add(new OutlineRectangle(20, 7, 1, p));
//
//        rectangles.add(new OutlineRectangle(-1, 13, 2, p));
//        rectangles.add(new OutlineRectangle(1, 12, 2, p));
//        rectangles.add(new OutlineRectangle(1, 14, 1, p));
//
//        rectangles.forEach(r -> System.out.println("\\draw (" + r.x + ", " + r.y + ") rectangle (" + (r.x + r.width) + ", " + (r.y + r.height) + ");"));
//
//        List<Edge> edges = Outline.createOutline(rectangles);
//        edges.forEach(System.out::println);
//    }
//
//    @Test
//    void constructOutlineBuffered() {
//        TreeSet<OutlineRectangle> rectangles = new TreeSet<>();
//        rectangles.add(new OutlineRectangle(0, 1, 2, p));
//        rectangles.add(new OutlineRectangle(0, 3, 1, p));
//        rectangles.add(new OutlineRectangle(1, 3, 1, p));
//        rectangles.add(new OutlineRectangle(0, 4, 2, p));
//        rectangles.add(new OutlineRectangle(2, 0, 4, p));
//        rectangles.add(new OutlineRectangle(4, 4, 2, p));
//        rectangles.add(new OutlineRectangle(4, 8, 3, p));
//        rectangles.add(new OutlineRectangle(7, 10, 1, p));
//        rectangles.add(new OutlineRectangle(7, 6, 4, p));
//        rectangles.add(new OutlineRectangle(6, 2, 4, p));
//        rectangles.add(new OutlineRectangle(8, 10, 4, p));
//        rectangles.add(new OutlineRectangle(8, 14, 4, p));
//        rectangles.add(new OutlineRectangle(12, 2, 1, p));
//        rectangles.add(new OutlineRectangle(11, 3, 4, p));
//        rectangles.add(new OutlineRectangle(12, 7, 4, p));
//        rectangles.add(new OutlineRectangle(12, 11, 1, p));
//        rectangles.add(new OutlineRectangle(13, 11, 2, p));
//        rectangles.add(new OutlineRectangle(15, 12, 1, p));
//        rectangles.add(new OutlineRectangle(15, 5, 2, p));
//        rectangles.add(new OutlineRectangle(17, 5, 3, p));
//        rectangles.add(new OutlineRectangle(17, 3, 2, p));
//        rectangles.add(new OutlineRectangle(20, 7, 1, p));
//
//        rectangles.add(new OutlineRectangle(-1, 13, 2, p));
//        rectangles.add(new OutlineRectangle(1, 12, 2, p));
//        rectangles.add(new OutlineRectangle(1, 14, 1, p));
//
//        rectangles.forEach(r -> System.out.println("\\draw (" + r.x + ", " + r.y + ") rectangle (" + (r.x + r.width) + ", " + (r.y + r.height) + ");"));
//
//        List<Edge> edges = Outline.createOutline(rectangles, 1.5);
//        edges.forEach(System.out::println);
//    }
}