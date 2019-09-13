package vertexCover.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import core.Graph;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Graph g = new Graph();

    @BeforeEach
    void BuildUp() {
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(3, 4);
    }

    @Test
    void addVertex() {
        assertFalse(g.contains(0));
        g.addVertex(0);
        assertTrue(g.contains(0));
    }

    @Test
    void addEdge() {
        assertEquals(2, g.degree(1));
        g.addEdge(1, 4);
        assertEquals(3, g.degree(1));
    }

    @Test
    void deleteVertex() {
        assertEquals(2, g.degree(1));
        g.deleteEdge(1, 3);
        assertEquals(1, g.degree(1));
    }

    @Test
    void deleteEdge() {
        assertEquals(2, g.degree(1));
        g.deleteVertex(2);
        assertEquals(1, g.degree(1));
    }

    @Test
    void contains() {
        assertTrue(g.contains(1));
        g.deleteVertex(1);
        assertFalse(g.contains(1));
    }

    @Test
    void degree() {
        assertEquals(2, g.degree(1));
        g.deleteVertex(2);
        assertEquals(1, g.degree(1));
    }

    @Test
    void adjacent() {
        assertFalse(g.adjacent(1, 4));
        assertTrue(g.adjacent(1, 2));
    }

    @Test
    void getNeighbors() {
        Set<Integer> nb = g.getNeighbours(1);
        assertTrue(nb.contains(2));
        assertTrue(nb.contains(3));
        assertFalse(nb.contains(0));
    }

    @Test
    void size() {
        assertEquals(4, g.size());
    }

    @Test
    void getEdgeCount() {
        assertEquals(3, g.getEdgeCount());
    }

    @Test
    void getVertices() {
        assertEquals(4, g.getVertices().size());
        assertTrue(g.getVertices().contains(1));
        assertFalse(g.getVertices().contains(0));
    }

    /**
     * Before we try to solve for the vertex cover, the graph is split into disjoint subGraphs, meaning that if 2 or more
     * parts of the graph are disconnected, we solve vertex cover for each of these parts individually and then add up
     * the results for the parts.
     * This way we try to reduce the depth in which the recursive core-algorithm stacks.
     * <p>
     * For small graphs the overhead of doing this may not be worth it, but on big instances it appears beneficial.
     */
    @Test
    void isConnected_getSubgraph_getVertexSubset_getDisjointGraphs() {
        Graph g = new Graph();
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(3, 4);
        assertTrue(g.isConnected());
        assertEquals(4, g.getConnectedGraph(1).getVertices().size());     //should be the old graph
        assertEquals(4, g.connectedVertices(1).size());
        assertTrue(g.connectedVertices(1).contains(1));
        assertTrue(g.connectedVertices(1).contains(4));

        g = new Graph();      //Graph is RESET!! Please notice
        g.addEdge(6, 7);
        assertTrue(g.isConnected());
        assertEquals(2, g.getConnectedGraph(6).getVertices().size());     //the new isolated graph (6,7)

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        assertFalse(g.isConnected());
        assertFalse(g.getConnectedGraph(1).getVertices().contains(6));
        assertEquals(3, g.getConnectedGraph(1).getVertices().size());

        assertEquals(2, g.getDisjointGraphs().size());
    }
}