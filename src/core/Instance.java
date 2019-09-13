package core;

/**
 * We didn't use a nested class for "Instance" because we (miss-)used this class for other purposes too.
 * It is worth it to apply as many reduction rules before you solve for K, because otherwise you would have to do
 * it every time again for each try for a new K.
 * If you apply the rules beforehand, you need to track by how much "points" you simplified the graph and add this value
 * back to result once you found it. This can also be done with the "Instance" class.
 * To see how it is done, visit the "SearchTree" class.
 */
public class Instance {
    public Graph graph;
    public int k;

    public Instance(Graph graph, int k) {
        this.graph = graph;
        this.k = k;
    }
}