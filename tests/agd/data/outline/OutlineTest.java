package agd.data.outline;

import agd.data.input.WeightedPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutlineTest {
    @Test
    void simpleTest() {
        List<OutlineRectangle> rectangles = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            rectangles.add(new OutlineRectangle(i * 3, 0, 3, new WeightedPoint(i * 3, 0, 3, 0)));
        }

        Outline outline = new Outline(rectangles.get(0));
        rectangles.stream().skip(1).forEach(outline::insert);
        System.out.println(outline.toLatexFigure());
    }

    @Test
    void simpleTest2() {
        List<OutlineRectangle> rectangles = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            rectangles.add(new OutlineRectangle(i * 3, i, 3, new WeightedPoint(i * 3, i, 3, 0)));
        }

        Outline outline = new Outline(rectangles.get(0));
        rectangles.stream().skip(1).forEach(outline::insert);
        System.out.println(outline.toLatexFigure());
    }

    @Test
    void simpleTest3() {
        List<OutlineRectangle> rectangles = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            rectangles.add(new OutlineRectangle(i * 3, 3 - i, 3, new WeightedPoint(i * 3, 3 - i, 3, 0)));
        }

        Outline outline = new Outline(rectangles.get(0));
        rectangles.stream().skip(1).forEach(outline::insert);
        System.out.println(outline.toLatexFigure());
    }
}