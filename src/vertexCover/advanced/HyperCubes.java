package vertexCover.advanced;

import core.Graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class HyperCubes {

    /**Goes through every vertex, checks for 2 neighbours that aren't connected and looks if they are connected to
     * another 4th vertex.
     *
     * @param g The target graph
     * @return A set of all rectangles in the graph (a circle of size 4). The rectangle may NOT have connections
     * between two opposing vertices
     */
    static Set<Set<Integer>> getRects(Graph g) {
        Set<Set<Integer>> result = new HashSet<>();
        for (int key : g.getVertices()) {

            for (int a : g.getNeighbours(key)) {
                for (int b : g.getNeighbours(key)) {
                    if (!g.adjacent(a,b) && a!=b) {

                        for (int fourth : g.getNeighbours(a)) {
                            if (g.adjacent(fourth,b) && !g.adjacent(key, fourth) && fourth != key) {
                                result.add(new HashSet<>(Arrays.asList(key,a,b,fourth)));
                            }
                        }
                    }
                }
            }
        }

        //the same rectangle will be found multiple times from all 4 directions
//        for (Set a : result) {
//            for (Set b : result) {
//                if (a.equals(b) && a!=b) {
//                    result.remove(b);
//                }
//            }
//        }

        return result;
    }

    static Set<Set<Integer>> getCubes(Graph g) {
        return null;
    }

    static HashMap<Integer, Integer> vertexMapping (Graph g, Set<Integer> setA, Set <Integer> setB) {
        HashMap<Integer, Integer> map = new HashMap<>();

        //makes sure the vertices are ONLY connect to each other
        for (int x : setA) {
            Set<Integer> s = new HashSet<>(g.getNeighbours(x));
            s.retainAll(setB);
            if (s.size() == 1) {
                int y = s.iterator().next();
                s = new HashSet<>(g.getNeighbours(y));
                s.retainAll(setA);
                if (s.size() == 1) {
                    map.put(x,y);
                    map.put(y,x);
                }
            }
        }

        return map;
    }
}
