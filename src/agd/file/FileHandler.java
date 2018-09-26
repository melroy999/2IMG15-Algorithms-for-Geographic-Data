package agd.file;

import agd.core.Core;
import agd.data.input.ProblemInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
}
