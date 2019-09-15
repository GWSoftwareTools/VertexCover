package vertexCover.advanced;

import org.junit.jupiter.api.Test;
import vertexCover.advanced.SearchTree;
import core.Graph;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Just checks if we get the correct results for the graphs we
 */
class SearchTreeTest {

    @Test
    void main() throws IOException {
        File dir = new File("data");
        int[] results = {2630, 38, 1088, 2203, 149, 59, 594, 158, 30, 34, 20, 14, 10, 4, 21190};
        int pos = 0;

        for (File file : dir.listFiles()) {
            Graph g = new Graph(file);
            assertEquals(results[pos++], SearchTree.minVertexCover(g));
        }
    }
}