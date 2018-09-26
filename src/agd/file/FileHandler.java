package agd.file;

import agd.core.Core;
import agd.state.util.ProblemInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FileHandler {
    // The core instance this file handler belongs to.
    private final Core core;

    public FileHandler(Core core) {
        this.core = core;
    }

    public void importFile(File file) {
        try(Scanner scanner = new Scanner(file)) {

            // Create a wrapper that will hold all the data about the problem to solve.
            ProblemInstance instance = new ProblemInstance(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextInt());

            // Add all of the new points.
            int n = scanner.nextInt();
            while(n > 0) {
                instance.addPoint(scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt());
                n--;
            }

            // Notify the core that we have a new problem to solve.
            core.updateProblemInstance(instance);

        } catch (FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
