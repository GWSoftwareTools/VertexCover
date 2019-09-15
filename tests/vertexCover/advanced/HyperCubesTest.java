package vertexCover.advanced;

import core.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HyperCubesTest {

    Graph g = new Graph();

    @BeforeEach
    void BuildUp() {
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);
    }

    @Test
    void getRectsTest() {
        Set<Integer> onlyRect = HyperCubes.getRects(g).iterator().next();
        assertEquals(onlyRect, new HashSet<>(Arrays.asList(1, 2, 3, 4)));

        //added a cross-connection that is NOT allowed for a rectangle
        g.addEdge(1,4);
        assertEquals(0, HyperCubes.getRects(g).size());

        g.deleteEdge(1,4);
        g.addEdge(1,5);
        g.addEdge(2,6);
        g.addEdge(5,6);
        assertEquals(2, HyperCubes.getRects(g).size());
    }
}