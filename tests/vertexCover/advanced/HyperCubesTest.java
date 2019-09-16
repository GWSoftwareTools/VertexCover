package vertexCover.advanced;

import core.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HyperCubesTest {

    private Graph g = new Graph();

    @BeforeEach
    void createCube() {
        //square A
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);

        //square B
        g.addEdge(5, 6);
        g.addEdge(5, 7);
        g.addEdge(6, 8);
        g.addEdge(7, 8);

        //connection
        g.addEdge(1, 5);
        g.addEdge(2, 6);
        g.addEdge(3, 7);
        g.addEdge(4, 8);
    }

    @Test
    void getRectsTest() {
        g = new Graph(); //reset
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);

        Set<Integer> onlyRect = HyperCubes.getRects(g).iterator().next();
        assertEquals(onlyRect, new HashSet<>(Arrays.asList(1, 2, 3, 4)));
        for (Set<Integer> s : HyperCubes.getRects(g)) {
            for (int i : s) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

        //added a cross-connection that is NOT allowed for a rectangle
        g.addEdge(1,4);
        assertEquals(0, HyperCubes.getRects(g).size());

        g.deleteEdge(1,4);
        g.addEdge(1,5);
        g.addEdge(2,6);
        g.addEdge(5,6);
        assertEquals(2, HyperCubes.getRects(g).size());
    }

    @Test
    void vertexMappingTest() {
        Set<Integer> setA = new HashSet<>(Arrays.asList(1,2,3,4));
        Set<Integer> setB = new HashSet<>(Arrays.asList(5,6,7,8));
        Map<Integer, Integer> map = HyperCubes.vertexMapping(g, setA, setB);
        for (int i : map.keySet()) {
            System.out.println(i + " " + map.get(i));
        }
        assertEquals(5, map.get(1));
        assertEquals(1, map.get(5));

        assertEquals(6, map.get(2));
        assertEquals(2, map.get(6));

        assertEquals(3, map.get(7));
        assertEquals(7, map.get(3));

        assertEquals(8, map.get(4));
        assertEquals(4, map.get(8));
    }

    @Test
    void hasHyperConnectionTest() {
        Set<Integer> setA = new HashSet<>(Arrays.asList(1,2,3,4));
        Set<Integer> setB = new HashSet<>(Arrays.asList(5,6,7,8));
        Map<Integer, Integer> map = HyperCubes.vertexMapping(g, setA, setB);
        assertTrue(HyperCubes.hasHyperConnection(g, map, setA));

        //the adjacency-properties of both sets are changed by the map now. This is not allowed for a hypercube.
        //Imagine this as crossing edges in a cube which we need to detect.
        //The mapping is changed in both directions, only one direction is needed though.
        map.put(1,6);
        map.put(6,1);
        map.put(2,5);
        map.put(5,2);
        assertFalse(HyperCubes.hasHyperConnection(g, map, setA));
    }

    @Test
    void getCubesTest() {
        Set<Set<Integer>> cubes = HyperCubes.getCubes(g);
        assertEquals(1,cubes.size());
    }
}