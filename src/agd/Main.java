package agd;

import agd.core.Core;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // The application starts when initializing the engine.
        SwingUtilities.invokeLater(Core::getCore);
    }
}
