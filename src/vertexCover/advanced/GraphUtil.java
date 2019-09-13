package vertexCover.advanced;

import core.Instance;
import core.Graph;

import java.util.*;

import static vertexCover.advanced.SearchTree.applyRules;

/**Provides advanced information about a graph. The core functionality for graphs are in the graph class.
 * Some of the methods are only useful for the vertex cover problem, not for other issues.
 */
public class GraphUtil {

    /**
     * @param g The target graph.
     * @return The ID of a vertex with maximum degree (number of neighbours). If there are multiple vertices with
     * a maximum degree, this method picks an arbitrary vertex of them, because it uses an iterator which goes through
     * the vertices in a order we can't influence.
     */
    public static Integer maxDegreeID(Graph g) {
        Integer bestKey = null;
        int maxDegree = 0;
        for (int key : g.getVertices()) {
            int degree = g.degree(key);
            if (bestKey == null || degree > maxDegree) {
                bestKey = key;
                maxDegree = degree;
            }
        }
        return bestKey;
    }

    /**
     * This method uses an iterator through the vertices. Therefore, if you don't change the graph, if you call this method
     * 2 times in a row, it will return the same pair both times. Therefore it does NOT return a random pair.
     *
     * This method is used as utility for other methods who need a pair of vertices that are connected.
     *
     * @param g The target graph.
     * @return A pair of vertices (an int-array of size 2 containing their ID's)
     */
    private static int[] anyPair(Graph g) {
        for (int key : g.getVertices()) {
            if (g.getNeighbors(key).size() > 0) {
                Iterator<Integer> it = g.getNeighbors(key).iterator();
                return new int[]{key, it.next()};
            }
        }
        return null;    //normally doesn't happen
    }

    /**In EVERY case the solution of this method is a valid value for k of the vertex cover problem. It may or may not 
     * be optimal, bot in many cases, it is surprisingly close.
     * It works by always removing the vertex with the highest degree and adding 1 to the result.
     * If you can for example reduce the graph by remove the max-degree-vertex 5 times, the value 5 is an upper-bound.
     *
     * @param g The target graph.
     * @return The result of our heuristic algorithm for an upper bound of the vertex cover problem.
     */
    static int getUpperBound(Graph g) {
        int safePoint = g.getSafePoint();
        int k = 0;
        int edgeCount = g.getEdgeCount();
        while (edgeCount != 0) {
            Integer max = maxDegreeID(g);
            edgeCount -= g.degree(max);
            g.deleteVertex(max);
            k++;
        }
        g.restore(safePoint);
        return k;
    }

    /**
     *
     * @param g The graph we want to test. Does NOT need to be a copy (is copied inside of method).
     * @param applyRules This method doesn't work unless triangles are removed beforehand. If the reduction rules
     *  were already applied, "applyRules" can be set to false. If it needs to be done inside this method, set it to true.
     * @return A value that is lower or equal to any valid K for a vertex cover.
     */
    public static int getLowerBound(Graph g, boolean applyRules) {
        int safePoint = g.getSafePoint();
        int change = 0;
        int min = 0;

        if (applyRules) {
            Instance inst = applyRules(g);
            change = inst.k;
            g = inst.graph;
        }

        int edgeCount = g.getEdgeCount();
        while (edgeCount != 0) {
            int[] pair = anyPair(g);
            edgeCount -= g.degree(pair[0]);
            g.deleteVertex(pair[0]);
            edgeCount -= g.degree(pair[1]);
            g.deleteVertex(pair[1]);
            min++;
        }
        g.restore(safePoint);
        return min + change;
    }
}