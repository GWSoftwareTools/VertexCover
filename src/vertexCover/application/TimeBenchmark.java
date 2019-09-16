package vertexCover.application;

import core.Graph;

import java.io.*;
import java.util.concurrent.*;

/**
 * This class is just used for checking the runtime and getting information about the graphs.
 * It does NOT check if the results are correct. For this, please go to the test-folder.
 */
public class TimeBenchmark {

    public static void main(String[] args) throws IOException {
        int runs = 1000;
        long timeStart = System.nanoTime();
        runAllFiles(runs);
        long completeTime = System.nanoTime() - timeStart;
        System.out.println("Avg time for 1 run through all files: " + PrettyText.prettyTime(completeTime / runs)
                + "\n###################################################");
    }

    private static void runAllFiles(int runs) throws IOException {
        System.out.println("Running the algorithm on each file " + runs + " times and printing the last result:\n");
        ExecutorService es = Executors.newSingleThreadExecutor();

        //the name of the folder which contains the text files for the graphs
        File dir = new File("data");
        long startTime;
        int successfulRuns = 0;
        for (File file : dir.listFiles()) {
            System.out.println("### " + file.getName() + ":");
            Graph g = new Graph(file);
            PrettyText.printAnalysis(g);
            startTime = System.nanoTime();

            for (int i = 1; i <= runs; i++) {

                //We use threads with a timeout so you can also include graphs that are too big to handle.
                //After TIMEOUT seconds, the calculations on this graph are cancelled and the next graph is started.
                final long TIMEOUT = 300; // sec
                Future f;
                if (i == runs) {
                    f = es.submit(() -> PrettyText.printResult(g, true));
                } else {
                    f = es.submit(() -> PrettyText.printResult(g, false));
                }
                try {
                    f.get(TIMEOUT, TimeUnit.SECONDS);
                    successfulRuns++;
                } catch (TimeoutException e) {
                    f.cancel(true);
                    System.out.println("Timeout after " + TIMEOUT + " seconds!");
                } catch (InterruptedException e) {
                    System.out.println("Interrupt!");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(String.format(PrettyText.formatter, "Average time:")
                    + PrettyText.prettyTime((System.nanoTime() - startTime) / runs) + "\n"
                    + String.format(PrettyText.formatter, "Runs in time:") + successfulRuns
                    + "\n\n---------------------------------------------------\n");
        }
        es.shutdown();
    }
}