package agd.core;

import agd.data.output.ProblemSolution;
import agd.file.FileHandler;
import agd.gui.GUI;
import agd.data.input.ProblemInstance;
import agd.solver.ComplexOutlineMergeSolver;
import agd.solver.OutlineMergeSolver;
import agd.solver.SimpleOutlineMergeSolver;
import agd.solver.SimpleSweep;

public class Core {
    // The singleton instance of the core.
    private static Core core;

    // Reference to the GUI component.
    public final GUI gui;

    // The file handler used to import and save files.
    public final FileHandler fileHandler;

    // The current instance we are attempting to solve.
    public ProblemInstance instance;

    // The solution of the current problem instance.
    public ProblemSolution solution;

    private Core() {
        // Create the GUI.
        gui = GUI.createAndShow(this);
        fileHandler = new FileHandler(this);
    }

    /**
     * Change the problem instance which we are currently attempting to solve.
     *
     * @param instance The new problem instance.
     */
    public void solveProblemInstance(ProblemInstance instance) {
        this.instance = instance;

        GUI.SolverOptions option = (GUI.SolverOptions) gui.solverSelector.getSelectedItem();

        if(option == GUI.SolverOptions.SimpleSweep) {
            System.out.println("Solver: SimpleSweep solving " + instance.id);
            this.solution = new ProblemSolution(instance, new SimpleSweep());
        } else if(option == GUI.SolverOptions.SimpleOutlines) {
            System.out.println("Solver: SimpleOutline(" + gui.sortSelector.getSelectedItem() + ") solving " + instance.id);
            this.solution = new ProblemSolution(instance, new SimpleOutlineMergeSolver());
        } else if(option == GUI.SolverOptions.Outlines) {
            System.out.println("Solver: Outline(" + gui.sortSelector.getSelectedItem() + ") solving " + instance.id);
            this.solution = new ProblemSolution(instance, new OutlineMergeSolver());
        } else {
            System.out.println("Solver: ComplexOutline(" + gui.sortSelector.getSelectedItem() + ") solving " + instance.id);
            this.solution = new ProblemSolution(instance, new ComplexOutlineMergeSolver());
        }

        gui.redrawDisplayPanel();
        gui.setMinError();
        gui.setError();
        System.out.println();
    }

    /**
     * Get the core instance, create it if it does not exist yet.
     *
     * @return The singleton core, created if it does not exist yet.
     */
    public static Core getCore() {
        if (core == null) core = new Core();
        return core;
    }
}
