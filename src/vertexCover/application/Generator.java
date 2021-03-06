package vertexCover.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * This class generates custom graphs for testing your implementation.
 * This comes in useful if all the given graphs are to easy/hard for your current implementation and you can't really
 * get feedback for this reason.
 * <p>
 * Note: there may be less than vertexRange vertices and less than "lines" lines as the are generated randomly
 * and these variables just set the bounds.
 */
public class Generator {
    private final static int VERTEX_RANGE = 5000;
    private final static int EDGE_COUNT = 6000;
    private final static String PATH = "data\\_pers"; //uses underscore so that it's sorted at the start

    public static void main(String[] args) throws IOException {
        generateGraph(VERTEX_RANGE, EDGE_COUNT);
    }

    /**
     * @param vertexRange The maximum ID for the vertices.
     * @param lines       The number of edges.
     * @throws IOException because you maybe don't have permission to write on this path
     */
    private static void generateGraph(int vertexRange, int lines) throws IOException {
        File f = new File(PATH);
        Random r = new Random();
        BufferedWriter w = new BufferedWriter(new FileWriter(f));
        for (int i = 0; i < lines; i++) {

            //generates 2 DIFFERENT Integers
            int a = 0, b = 0;
            while (a == b) {
                a = r.nextInt(vertexRange);
                b = r.nextInt(vertexRange);
            }

            w.write(r.nextInt(vertexRange) + " " + r.nextInt(vertexRange) + "\n");
        }
        w.close();
        System.out.println("New graph generated");
        System.out.println("Vertex-Range: " + vertexRange);
        System.out.println("#Lines: " + lines);
    }
}