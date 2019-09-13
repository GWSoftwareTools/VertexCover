package vertexCover.main.heuristicsBenchmark;

import org.junit.jupiter.api.Test;
import vertexCover.advanced.GraphUtil;
import vertexCover.advanced.SearchTree;
import core.Instance;
import core.Graph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * For optimization we use heuristics for the upper- and lower bound of the vertex cover.
 * For the lower bound we find a (not optimal) set of not touching edges. We then know that for each of these
 * edges we need at least one vertex. Therefore, the real solution for the vertex cover problem must be greater-than
 * this number, as it also covers these edges.
 */
public class LowerBound {
    private static int[] results = {2630, 38, 1088, 2203, 149, 59, 594, 158, 30, 34, 20, 14, 10, 4, 21190};

    @Test
    void main() throws IOException {
        File dir = new File("data");
        int[] lowerBounds = new int[dir.list().length];

        int pos = 0;
        for (File f : dir.listFiles()) {
            Graph g = new Graph(f);

            Instance pre = SearchTree.applyRules(g);
            g = pre.graph;
            int change = pre.k;
            lowerBounds[pos++] = GraphUtil.getLowerBound(g, false) + change;
        }
        System.out.println(Arrays.toString(lowerBounds));
        double[] precision = new double[results.length];
        for (int i = 0; i < results.length; i++) {
            precision[i] = (double) lowerBounds[i] / (double) results[i];
        }
        System.out.println(Arrays.toString(precision));
        System.out.println(Arrays.stream(precision).average());
    }

    @Test
    void noPrep() throws IOException {
        System.out.println("\n" + GraphUtil.getLowerBound(new Graph(new File("data\\out.adjnoun_adjacency_adjacency")), true));
    }
}
