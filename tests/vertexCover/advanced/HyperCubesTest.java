package vertexCover.advanced;

import core.Graph;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HyperCubesTest {

    Graph g = new Graph();

    @Test
    void getRectsTest() {
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
        //square 1
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);

        //square 2
        g.addEdge(5, 6);
        g.addEdge(5, 7);
        g.addEdge(6, 8);
        g.addEdge(7, 8);

        //connection
        g.addEdge(1, 5);
        g.addEdge(2, 6);
        g.addEdge(3, 7);
        g.addEdge(4, 8);

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
}