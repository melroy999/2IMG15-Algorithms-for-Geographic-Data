package agd.solver;

import org.junit.jupiter.api.Test;
import sun.reflect.generics.tree.Tree;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class OutlineTestTest {

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
}