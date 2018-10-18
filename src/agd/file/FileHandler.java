package agd.file;

import agd.core.Core;
import agd.data.input.ProblemInstance;
import agd.data.output.ProblemSolution;
import agd.solver.SimpleOutlineMergeSolver;

import java.io.*;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static agd.solver.SimpleOutlineMergeSolver.*;

/**
 * A class that handles the I/O.
 */
public class FileHandler {
    // The core instance this file handler belongs to.
    private final Core core;

    /**
     * Create a handler for file input and output.
     *
     * @param core The core of the program we are a slave of.
     */
    public FileHandler(Core core) {
        this.core = core;
    }

    /**
     * Import the given file.
     *
     * @param file The plain text file which has problem instance data within.
     */
    public void importFile(File file) {
        try(Scanner scanner = new Scanner(file)) {
            // Notify the core that we have a new problem to solve.
            core.solveProblemInstance(ProblemInstance.readInstance(scanner));

        } catch (FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    public void batchResolutionFile(File input, File output) {
        try {
            ZipFile zip = new ZipFile(input);

            Enumeration<? extends ZipEntry> entries = zip.entries();

            // Iterate over all the entries.
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();

                if(entry.getName().endsWith(".txt")) {

                    double min = Double.MAX_VALUE;
                    ProblemSolution solution = null;


                    for (SimpleOutlineMergeSolver.SortingOptions option : SimpleOutlineMergeSolver.SortingOptions.values()) {
                        try (Scanner scanner = new Scanner(new InputStreamReader(zip.getInputStream(entry)))) {

                            core.gui.sortSelector.setSelectedItem(option);

                            // Notify the core that we have a new problem to solve.
                            core.solveProblemInstance(ProblemInstance.readInstance(scanner));

                            if (min > core.solution.getTotalError()) {
                                min = core.solution.getTotalError();
                                solution = core.solution;
                            }

                        } catch(FileNotFoundException | NoSuchElementException e){
                            e.printStackTrace();
                        }
                    }

                    if (solution != null) {
                        System.out.println(solution.getTotalError());
                        core.solution = solution;
                        exportFile(new File(output, entry.getName()));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportFile(File file) {
        String filename = file.toString();

        if(!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        try(FileWriter fw = new FileWriter(filename)) {
            // Write the result to the desired file.
            if(core.solution != null) {
                fw.write(core.solution.output());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
