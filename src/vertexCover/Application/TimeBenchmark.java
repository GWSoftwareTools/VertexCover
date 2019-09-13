package vertexCover.Application;

import core.Graph;

import java.io.*;
import java.util.concurrent.*;

/**
 * This class is just used for checking the runtime and getting information about the graphs.
 * It does NOT check if the results are correct. For this, please go to the test-folder.
 */
public class TimeBenchmark {
    // If you want to run the calculations multiple times to get a more precise result by averaging the times,
    // this print-stream makes sure the results and info about the graph are only printed once (on the last run)
    private final static PrintStream ignore = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) {
            // do nothing
        }
    });

    public static void main(String[] args) throws IOException {
        int runs = 100;
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
        PrintStream old = System.out;
        long startTime;
        int successfulRuns = 0;
        for (File file : dir.listFiles()) {
            System.out.println("### " + file.getName() + ":");
            System.setOut(ignore);
            Graph g = new Graph(file);
            startTime = System.nanoTime();

            for (int i = 1; i <= runs; i++) {
                if (i == runs) {
                    System.setOut(old);
                }

                //We use threads with a timeout so you can also include graphs that are too big to handle.
                //After TIMEOUT seconds, the calculations on this graph are cancelled and the next graph is started.
                Future f = null;
                final long TIMEOUT = 300; // sec
                try {
                    f = es.submit(() -> PrettyText.printResult(g));
                    f.get(TIMEOUT, TimeUnit.SECONDS);
                    successfulRuns++;
                } catch (TimeoutException e) {
                    f.cancel(true);
                    System.setOut(old);
                    System.out.println("Timeout after " + TIMEOUT + " seconds!");
                } catch (InterruptedException e) {
                    System.setOut(old);
                    System.out.println("Interrupt!");
                } catch (ExecutionException e) {
                    System.setOut(old);
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