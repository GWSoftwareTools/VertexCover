package vertexCover.advanced;

import core.Graph;

import java.util.*;

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
        Set<Set<Integer>> cubes = new HashSet<>();
        Set<Set<Integer>> rects = getRects(g);

        for (Set<Integer> setA : rects) {
            for (Set<Integer> setB : rects) {
                if (!setA.equals(setB)) {

                    Map<Integer, Integer> map = vertexMapping(g, setA, setB);
                    if (hasHyperConnection(g, map, setA)) {
                        HashSet<Integer> cube = new HashSet<>();
                        cube.addAll(setA);
                        cube.addAll(setB);
                        cubes.add(cube);
                    }
                }
            }
        }
        return cubes;
    }

    static Map<Integer, Integer> vertexMapping (Graph g, Set<Integer> setA, Set<Integer> setB) {
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

    /**Works ONLY with a correct map from "vertexMapping"
     *
     * @param g The target graph
     * @param map Bijective map from the vertex-set "s" to another vertex-set.
     * @param set One of the sets from the map, doesn't matter which one
     * @return Checks if for EVERY adjacent pair of vertices in one set their mappings in the other set
     * are also adjacent.
     */
    static boolean hasHyperConnection (Graph g, Map<Integer, Integer> map, Set<Integer> set) {
        for (int a : set) {
            for (int b : g.getNeighbours(a)) {
                if(set.contains(b) && !g.adjacent(map.get(a), map.get(b))) {    //only neighbours in set
                    return false;
                }
            }
        }
        return true;
    }
}