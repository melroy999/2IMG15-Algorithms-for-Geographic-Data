package agd.file;

import agd.core.Core;
import agd.store.instance.ProblemInstance;

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
            ProblemInstance instance = new ProblemInstance();

            // Read the header of the file to find the id of the instance, viewport bounds and number of points.
            instance.id = scanner.nextInt();
            instance.minx = scanner.nextInt();
            instance.maxx = scanner.nextInt();
            instance.miny = scanner.nextInt();
            instance.maxy = scanner.nextInt();

            // Add all of the new points.
            int n = scanner.nextInt();
            while(n > 0) {
                instance.points.add(scanner.nextDouble(), scanner.nextDouble(), scanner.nextInt());
                n--;
            }

            // Notify the core that we have a new problem to solve.
            core.updateProblemInstance(instance);

        } catch (FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
