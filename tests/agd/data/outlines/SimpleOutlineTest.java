package agd.data.outlines;

import agd.data.input.WeightedPoint;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Duplicates")
class SimpleOutlineTest {
    private static WeightedPoint p = new WeightedPoint(0, 0, 3, 0);

    @BeforeAll
    static void start() {
        System.out.println("\\begin{document}");
    }

    @AfterAll
    static void end() {
        System.out.println("\\end{document}");
    }

    //region Useless tests
//    @Test
//    void insert0() {
//        OutlineRectangle r = new OutlineRectangle(0, 0, 3, p);
//        SimpleOutline outline = new SimpleOutline(r);
//        System.out.println(outline.toLatexFigure());
//    }
//
//    @Test
//    void insert1() {
//        OutlineRectangle r = new OutlineRectangle(0, 0, 3, p);
//        SimpleOutline outline = new SimpleOutline(r);
//
//        OutlineRectangle r2 = new OutlineRectangle(3, 0, 3, p);
//        outline.insert(r2);
//
//        System.out.println(outline.toLatexFigure());
//    }
//
//    @Test
//    void insert2() {
//        OutlineRectangle r = new OutlineRectangle(0, 0, 3, p);
//        SimpleOutline outline = new SimpleOutline(r);
//
//        OutlineRectangle r2 = new OutlineRectangle(0, 3, 3, p);
//        outline.insert(r2);
//
//        System.out.println(outline.toLatexFigure());
//    }
//
//    @Test
//    void insert3() {
//        OutlineRectangle r = new OutlineRectangle(0, 0, 3, p);
//        SimpleOutline outline = new SimpleOutline(r);
//
//        OutlineRectangle r2 = new OutlineRectangle(0, -3, 3, p);
//        outline.insert(r2);
//
//        System.out.println(outline.toLatexFigure());
//    }
//
//    @Test
//    void insert4() {
//        OutlineRectangle r = new OutlineRectangle(0, 0, 3, p);
//        SimpleOutline outline = new SimpleOutline(r);
//
//        OutlineRectangle r2 = new OutlineRectangle(-3, 0, 3, p);
//        outline.insert(r2);
//
//        System.out.println(outline.toLatexFigure());
//    }
    //endregion

    @Test
    void insert5() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 8, p);
        SimpleOutline outline = new SimpleOutline(r);


        OutlineRectangle r1 = new OutlineRectangle(0, 8, 2, p);
        OutlineRectangle r2 = new OutlineRectangle(4, 8, 4, p);
        outline.insert(r1);
        outline.insert(r2);

        System.out.println(outline.toLatexFigure());
    }

    @Test
    void insert6() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 8, p);
        SimpleOutline outline = new SimpleOutline(r);


        OutlineRectangle r1 = new OutlineRectangle(4, 8, 4, p);
        outline.insert(r1);

        System.out.println(outline.toLatexFigure());
    }

    @Test
    void insert7() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 2, p);
        SimpleOutline outline = new SimpleOutline(r);

        OutlineRectangle r1 = new OutlineRectangle(-2, -2, 2, p);
        outline.insert(r1);

        System.out.println(outline.toLatexFigure());
    }

    @Test
    void insert8() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 2, p);
        SimpleOutline outline = new SimpleOutline(r);

        OutlineRectangle r1 = new OutlineRectangle(2, 2, 2, p);
        outline.insert(r1);

        System.out.println(outline.toLatexFigure());
    }

    @Test
    void insert9() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 2, p);
        SimpleOutline outline = new SimpleOutline(r);

        OutlineRectangle r1 = new OutlineRectangle(2, -2, 2, p);
        outline.insert(r1);

        System.out.println(outline.toLatexFigure());
    }

    @Test
    void insert10() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 2, p);
        SimpleOutline outline = new SimpleOutline(r);

        OutlineRectangle r1 = new OutlineRectangle(-2, 2, 2, p);
        outline.insert(r1);

        System.out.println(outline.toLatexFigure());
    }


    @Test
    void insert11() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 4, p);
        SimpleOutline outline = new SimpleOutline(r);

        OutlineRectangle r1 = new OutlineRectangle(0, 4, 2, p);
        outline.insert(r1);

        OutlineRectangle r2 = new OutlineRectangle(2, 4, 1, p);
        outline.insert(r2);

        System.out.println(outline.toLatexFigure());
    }

    @Test
    void insert12() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 4, p);
        SimpleOutline outline = new SimpleOutline(r);

        OutlineRectangle r1 = new OutlineRectangle(0, 4, 2, p);
        outline.insert(r1);

        OutlineRectangle r2 = new OutlineRectangle(2, 4, 2, p);
        outline.insert(r2);

        System.out.println(outline.toLatexFigure());
    }

    @Test
    void insert13() {
        OutlineRectangle r = new OutlineRectangle(0, 0, 4, p);
        SimpleOutline outline = new SimpleOutline(r);

        OutlineRectangle r1 = new OutlineRectangle(0, 4, 1, p);
        outline.insert(r1);

        OutlineRectangle r2 = new OutlineRectangle(1, 4, 2, p);
        outline.insert(r2);

        System.out.println(outline.toLatexFigure());
    }

}