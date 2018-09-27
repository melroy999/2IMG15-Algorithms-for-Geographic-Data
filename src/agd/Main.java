package agd;

import agd.core.Core;
import agd.data.input.WeightedPoint;
import agd.data.util.EntryRectangle;
import agd.data.util.QuadTreeNode;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        // The application starts when initializing the engine.
        SwingUtilities.invokeLater(Core::getCore);

//        HashSet<EntryRectangle> intersections = new HashSet<>();
//
//        QuadTreeNode root = new QuadTreeNode(new Rectangle(0, 0, 50, 50));
//        EntryRectangle r1 = new EntryRectangle(0, 0, 10, 10, new WeightedPoint(5, 5, 10, 0));
//        EntryRectangle r2 = new EntryRectangle(1, 1, 10, 10, new WeightedPoint(6, 6, 10, 1));
//
//        root.insert(r1);
//        intersections.clear();
//        root.query(r2, intersections);
//        System.out.println("Number of intersections: " + intersections.size());
//        root.delete(r1);
//        intersections.clear();
//        root.query(r2, intersections);
//        System.out.println("Number of intersections: " + intersections.size());
//
//        root.clear();
//        root.insert(r1);
//        root.insert(r2);
//        intersections.clear();
//        root.query(r2, intersections);
//        System.out.println("Number of intersections: " + intersections.size());
    }
}
