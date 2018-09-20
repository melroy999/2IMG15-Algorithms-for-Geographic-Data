package agd.gui;

import agd.core.Core;

import javax.swing.*;
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

    // Components of the GUI.
    private JPanel rootPanel;
    private JPanel displayPanel;
    private JButton openFileButton;
    private JPanel controlPanel;
    private JPanel filePanel;
    private JPanel settingsPanel;
    private JButton saveFileButton;
    private JCheckBox magic1CheckBox;
    private JCheckBox magic2CheckBox;
    private JLabel minErrorLabel;
    private JLabel errorLabel;

    private GUI(Core core) {
        this.core = core;
        init();
    }

    private void init() {
        openFileButton.addActionListener(e -> {
            int status = fc.showOpenDialog(rootPanel);

            if(status == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                core.fileHandler.importFile(selectedFile);
            }
        });
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

    /**
     * Create the GUI components that are not automatically created.
     */
    private void createUIComponents() {
        // Obviously, we have to initialize our drawing panel.
        displayPanel = new DrawPanel(this);
    }

    public void setMinError() {
        if(core.instance != null) {
            minErrorLabel.setText(String.format(Locale.ROOT, "%.3f", core.instance.getMinimumError()));
        } else {
            minErrorLabel.setText("0");
        }
    }

    public void setError() {
        if(core.instance != null) {
            errorLabel.setText(String.format(Locale.ROOT, "%.3f", core.instance.getTotalError()));
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
