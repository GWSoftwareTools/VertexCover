package vertexCover.advanced;

import core.Instance;
import core.Graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Searches for a minimal integer K which stands for the number of vertices you need at least to cover every vertex in
 * the input graph.
 */
public class SearchTree {
    /**
     * The main function that is called to calculate K.
     *
     * @param g The target graph.
     * @return K
     */
    public static int minVertexCover(Graph g) {
        //Here you see how the rules are applied beforehand to reduces the graph before the search for K even begins.
        //By how much the graph was simplified is stored in "change". It is added to the result in the end.
        Instance pre = applyRules(g);
        g = pre.graph;
        int change = pre.k;

        int result = 0;
        //Calculates K for each disjoint subGraph to reduce the runtime in hard instances.
        for (Graph disjointGraph : g.getDisjointGraphs()) {
            result += findK(disjointGraph);
        }
        return result + change;
    }

    /**
     * Actually calculates K for a given (sub-)graph. We know that the input graph is connected, because this method
     * is called in "minVertexCover". Would work on disconnected graphs too, but would take unnecessarily long.
     *
     * @param g The target graph
     * @return K
     */
    private static int findK(Graph g) {
        //Like usually, the reduction rules are applied.
        Instance inst = applyRules(g);
        g = inst.graph;
        int change = inst.k;

        int i = GraphUtil.getLowerBound(g, false);
        int maxK = GraphUtil.getUpperBound(g);

        int safePoint = g.getSafePoint();       //for the UndoStack

        for (; i < maxK; i++) {
            g.restore(safePoint);
            inst.k = i;
            if (solveI(inst)) {
                return i + change;
            }
        }
        return maxK + change;
    }

    /**
     * We have a separate method "solveI" for instances, because we can only use the high-degree-rules which removes
     * vertices with a degree greater than K in instances, not as a preparation for graphs because the don't have a K
     * to use for this rule.
     *
     * @param inst Target Instance. Does NOT need to be a copy.
     * @return True if solvable for the value K stored in the instance.
     */
    private static boolean solveI(Instance inst) {
        applyRules(inst);

        int minK = GraphUtil.getLowerBound(inst.graph, false);

        if (inst.k < minK)
            return false;
        if (!inst.graph.hasEdges())
            return true;

        int bestKey = GraphUtil.maxDegreeID(inst.graph);

        int safePoint = inst.graph.getSafePoint();
        int safeK = inst.k;
        inst.graph.deleteVertex(bestKey);
        inst.k -= 1;

        if (solveI(inst)) {
            return true;
        }

        inst.graph.restore(safePoint);
        inst.k = safeK - inst.graph.getNeighbors(bestKey).size();
        inst.graph.getNeighbors(bestKey).forEach(inst.graph::deleteVertex);
        return solveI(inst); // instNeighborsDelete
    }

    /**
     * Applies all reduction rules except the high-degree-rule, because graphs don't have value K to use for it.
     *
     * @param g The graph we want to prepare before solving
     * @return "i": An Instance object with an optimized graph. By how many points it was improved is stored
     * as a positive number in "i.k".
     * We need to return an instance because we need to store by how much it was changed, which we can't do in a graph.
     */
    public static Instance applyRules(Graph g) {
        Instance inst = new Instance(g, 0);
        //Can NOT use "prepareInstance", because this would use "removeHighDeg" which isn`t allowed in this context
        boolean hasRemoved = true;
        while (hasRemoved) {
            hasRemoved = removeCliques(inst) | removeP3(inst) | removeBigNeighbor(inst);
        }
        inst.k = -inst.k;

        return inst;
    }

    /**
     * Applies all rules we currently have implemented.
     *
     * @param inst Target instance
     * @return is void because we just change the parameter-object
     */
    private static void applyRules(Instance inst) {
        boolean hasRemoved = true;
        while (hasRemoved) {
            hasRemoved = removeP3(inst) | removeCliques(inst) | removeHighDeg(inst) | removeBigNeighbor(inst);
        }
    }

    /**
     * An clique is a set of vertices which are ALL connected to each other vertex in the clique. For example a single point,
     * two connected vertices or a triangle are (simple) cases of a clique. If we find a clique of size n and only n-1 vertices
     * are connected to a vertex outside of the clique, we can remove the clique and reduce the parent instance by n-1.
     * This is a generalization of the "singleton" and "degree-one" rule => It also works on arbitrarily big cliques.
     *
     * @param inst Target instance
     * @return True if this method changed the instance. False otherwise.
     */
    private static boolean removeCliques(Instance inst) {
        boolean changed = false;

        keyLoop:
        for (int key : inst.graph.getVertices()) {

            Set<Integer> neighbours = inst.graph.getNeighbors(key);

            //check all connections of the neighbours pairwise to verify that it is a clique
            for (int a : neighbours) {
                for (int b : neighbours) {
                    if (!(inst.graph.adjacent(a, b) || a == b)) {
                        continue keyLoop;
                    }
                }
            }

            int reduce = neighbours.size();
            neighbours.forEach(inst.graph::deleteVertex);
            inst.graph.deleteVertex(key);
            inst.k -= reduce;
            changed = true;
        }
        return changed;
    }

    /**
     * If a vertex "key" is ONLY connected to 2 neighbours "nb1" and "nb2", who are themselves not neighbours, we can
     * remove "key", and merge both neighbours together, which means deleting one of them and moving the connections of the
     * deleted one onto the remaining one. This is done in the method "mergeVertices". It doesn't really affect the runtime
     * in which direction the merge operation is done.
     *
     * @param inst Target instance
     * @return True if this method changed the instance. False otherwise.
     */
    private static boolean removeP3(Instance inst) {
        boolean changed = false;
        for (int key : inst.graph.getVertices()) {
            if (inst.graph.getNeighbors(key).size() == 2) {
                Iterator<Integer> it = inst.graph.getNeighbors(key).iterator();
                //Merge nb1 onto nb2
                int nb1 = it.next();
                int nb2 = it.next();
                if (!inst.graph.adjacent(nb1, nb2)) {
                    mergeVertices(inst.graph, nb1, nb2);
                    inst.graph.deleteVertex(key);
                    inst.k -= 1;
                    changed = true;
                }
            }
        }
        return changed;
    }

    /**
     * If there exist two adjacent vertices "v1" and "v2" and the set of neighbours of "v1" is a subset of the neighbours of
     * "v2", we can remove "v2" and reduce k by 1.
     * You can visualize this rule this way: As "v1" and "v2" are connected, at least one of the has to be included in the
     * vertex cover to cover the edge between them. Because "v2" also covers every edge "v1" covers, maybe even more, it
     * is in every case worth it to take it over "v1". If "v1" and "v2" have the same set of neighbours, this rule can
     * be applied in both direction with no difference.
     *
     * @param inst Target instance.
     * @return True if this method changed the instance. False otherwise.
     */
    private static boolean removeBigNeighbor(Instance inst) {
        boolean changed = false;
        for (int a : inst.graph.getVertices()) {
            neighborLoop:
            for (int b : inst.graph.getNeighbors(a)) {

                for (int i : inst.graph.getNeighbors(a)) {
                    if (!(inst.graph.getNeighbors(b).contains(i) || i == b)) {
                        continue neighborLoop;
                    }
                }

                inst.graph.deleteVertex(b);
                inst.k--;
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Not used yet, please ignore. Probably a bad idea.
     */
    private static boolean removeCubes(Instance inst) {
        boolean changed = false;
        HashSet<HashSet<Integer>> rectangles = new HashSet<>();

        for (int key : inst.graph.getVertices()) {
            Set<Integer> neighbours = inst.graph.getNeighbors(key);
            for (int a : neighbours) {
                for (int b : neighbours) {
                    if (a != b) {
                        Set<Integer> fourthVertexSet = inst.graph.getNeighbors(a);
                        fourthVertexSet.retainAll(inst.graph.getNeighbors(b));
                        fourthVertexSet.remove(key);

                        for (int fourthVertex : fourthVertexSet) {
                            HashSet newRect = new HashSet(4);
                            Collections.addAll(newRect, key, a, b, fourthVertex);
                            rectangles.add(new HashSet<>());
                        }
                    }
                }
            }
        }
        return changed;
    }

    /**
     * An implementation of the high-degree rule which removes vertices with more neighbours than the value of K.
     *
     * @param inst Target instance.
     * @return True if this method changed the instance. False otherwise.
     */
    private static boolean removeHighDeg(Instance inst) {
        boolean changed = false;
        for (Integer key : inst.graph.getVertices()) {
            if (inst.graph.degree(key) > inst.k) {    //is a "high vertex"
                inst.graph.deleteVertex(key);
                inst.k--;
                changed = true;
            }
        }
        return changed;
    }

    /**
     * @param g    The target graph.
     * @param from This vertex is removed and its edges are add to "to".
     * @param to   This vertex receives the edges from "from".
     */
    private static void mergeVertices(Graph g, int from, int to) {
        g.getNeighbors(from).forEach(x -> g.addEdge(to, x));
        g.deleteVertex(from);  //also deletes all edges containing
    }
}