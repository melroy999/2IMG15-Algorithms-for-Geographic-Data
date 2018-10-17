package agd.gui;

import agd.core.Core;
import agd.data.input.ProblemInstance;
import agd.solver.SimpleOutlineMergeSolver;
import agd.solver.SimpleOutlineMergeSolver.SortingOptions;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

public class GUI {
    // Reference to the singleton instance.
    private static GUI gui;

    // The core instance this GUI belongs to.
    protected final Core core;

    // The frame of the GUI.
    private static JFrame frame;

    // A static file chooser, to avoid slow loading issues.
    private static JFileChooser fc = new JFileChooser(); //now declared globally
    private static JFileChooser fs = new JFileChooser(); //now declared globally

    // Components of the GUI.
    private JPanel rootPanel;
    private JPanel displayPanel;
    private JButton openFileButton;
    private JPanel controlPanel;
    private JPanel filePanel;
    private JPanel settingsPanel;
    private JButton saveFileButton;
    private JLabel minErrorLabel;
    private JLabel errorLabel;
    public JCheckBox validateOutputCheckBox;
    public JComboBox<SolverOptions> solverSelector;
    public JComboBox<SortingOptions> sortSelector;
    private JButton recalculateButton;

    private GUI(Core core) {
        this.core = core;
        init();

        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileFilter(new FileNameExtensionFilter("text file", "txt"));

        fs.setDialogType(JFileChooser.SAVE_DIALOG);
        fs.setSelectedFile(new File("output.txt"));
        fs.setFileFilter(new FileNameExtensionFilter("text file", "txt"));
    }

    private void init() {
        openFileButton.addActionListener(e -> {
            int status = fc.showOpenDialog(rootPanel);

            if(status == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                core.fileHandler.importFile(selectedFile);
            }
        });

        saveFileButton.addActionListener(e -> {
            int status = fc.showSaveDialog(rootPanel);

            if(status == JFileChooser.APPROVE_OPTION) {
                core.fileHandler.exportFile(fc.getSelectedFile());
            }
        });

        recalculateButton.addActionListener(e -> {
                if(core.instance != null) {
                    // Notify the core that we have a new problem to solve.
                    core.solveProblemInstance(core.instance);
                }
            }
        );
    }

    /**
     * Repaint the game panel.
     */
    public void redrawDisplayPanel() {
        displayPanel.repaint();
    }

    /**
     * Create a GUI instance, and make it visible.
     *
     * @return The singleton GUI instance.
     */
    public static GUI createAndShow(Core core) {
        if(gui == null) {
            try {
                // Make the application look like a windows application.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Initialize the singleton instance.
            frame = new JFrame("[2IMG15] Algorithms for Geographic Data");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            gui = new GUI(core);
            frame.setContentPane(gui.rootPanel);


            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setPreferredSize(screenSize);
            frame.setMinimumSize(screenSize);
            frame.pack();
            frame.setVisible(true);
        }

        // Return the instance of the GUI.
        return gui;
    }

    public enum SolverOptions {
        SimpleSweep, SimpleOutlines
    }

    /**
     * Create the GUI components that are not automatically created.
     */
    private void createUIComponents() {
        // Obviously, we have to initialize our drawing panel.
        displayPanel = new DrawPanel(this);

        solverSelector = new JComboBox<>(SolverOptions.values());
        sortSelector = new JComboBox<>(SortingOptions.values());
    }

    public void setMinError() {
        if(core.solution != null) {
            minErrorLabel.setText(String.format(Locale.ROOT, "%.3f", core.solution.getMinimumError()));
        } else {
            minErrorLabel.setText("0");
        }
    }

    public void setError() {
        if(core.solution != null) {
            errorLabel.setText(String.format(Locale.ROOT, "%.3f", core.solution.getTotalError()));
        } else {
            errorLabel.setText("0");
        }
    }

    /**
     * Get the size of the game panel.
     *
     * @return The size of the game panel as a dimension object.
     */
    public Dimension getDisplayPanelDimensions() {
        return displayPanel.getSize();
    }
}
