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
    }
}
