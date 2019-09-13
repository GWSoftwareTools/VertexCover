package core;

import vertexCover.advanced.UndoStack;

import java.io.*;
import java.util.*;

public class Graph {
    private HashMap<Integer, HashSet<Integer>> edges;
    private UndoStack undoStack;

    public Graph() {
        edges = new HashMap<>();
        undoStack = new UndoStack();
    }

    public Graph(File file) throws IOException {
        this();
        if (!file.exists()) {
            throw new FileNotFoundException("The file doesn't exist!");
        } else if (file.isDirectory()) {
            throw new FileNotFoundException("The file is a directory!");
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        Scanner i;
        while ((line = br.readLine()) != null) {
            //only necessary if the file format contains additional annotations
            if (line.equals("") || line.startsWith("c") || line.startsWith("p")) {
                continue;
            }
            i = new Scanner(line);
                addEdge(i.nextInt(), i.nextInt());
            i.close();
        }
        br.close();
    }

    /**
     * Does nothing if the vertex already exists
     *
     * @param v ID of the new added vertex
     */
    public void addVertex(Integer v) {
        addVertex(v, false);
    }

    private void addVertex(Integer v, boolean restore) {
        if (!contains(v)) {
            edges.put(v, new HashSet<>());
            if (!restore) {
                undoStack.push(new UndoStack.UndoItem() {
                    @Override
                    public void undo() {
                        edges.remove(v);
                    }
                });
            }
        }
    }

    /**
     * Deletes this vertex in the graph. Also makes sure it isn`t listed as a neighbour for the other vertices
     *
     * @param v ID of the deleted vertex
     */
    public void deleteVertex(Integer v) {
        if (contains(v)) {
            for (Integer nb : edges.get(v)) {
                undoStack.push(new UndoStack.UndoItem() {
                    @Override
                    public void undo() {
                        addEdge(v, nb, true);
                    }
                });
                edges.get(nb).remove(v);
            }
            edges.remove(v);    //delete the vertex itself
        }
    }

    /**
     * Generates the needed vertices if they don`t already exist.
     * Does nothing if the edge already exists.
     *
     * @param v First ID of incident vertex
     * @param w Second ID of incident vertex
     */
    public void addEdge(Integer v, Integer w) {
        addEdge(v, w, false);
    }

    private void addEdge(Integer v, Integer w, boolean restore) {
        if (!v.equals(w)) {
            addVertex(v, restore);
            addVertex(w, restore);
            if ((edges.get(v).add(w) | edges.get(w).add(v)) && !restore) {
                undoStack.push(new UndoStack.UndoItem() {
                    @Override
                    public void undo() {
                        deleteEdge(v, w, true);
                    }
                });
            }
        }
    }

    /**
     * Deletes the edge specified by the 2 Vertex-IDs.
     * deleteEdge(1,2) is identical as for deleteEdge(2,1) for example.
     * DELETES a vertex if he has 0 edges after this function call
     */
    public void deleteEdge(Integer v, Integer w) {
        deleteEdge(v, w, false);
    }

    private void deleteEdge(Integer v, Integer w, boolean restore) {
        if (contains(v) && contains(w)) {
            if ((edges.get(v).remove(w) | edges.get(w).remove(v)) && !restore) {
                undoStack.push(new UndoStack.UndoItem() {
                    @Override
                    public void undo() {
                        addEdge(v, w, true);
                    }
                });
            }

            if (edges.get(v).isEmpty()) {
                edges.remove(v);
            }
            if (edges.get(w).isEmpty()) {
                edges.remove(w);
            }
        }
    }

    public boolean contains(Integer v) {
        return edges.containsKey(v);
    }

    public int degree(Integer v) {
        if (contains(v)) {
            return edges.get(v).size();
        }
        return 0;
    }

    public boolean adjacent(Integer v, Integer w) {
        return contains(v) && edges.get(v).contains(w);
    }

    public Set<Integer> getNeighbors(Integer v) {
        if (edges.containsKey(v)) {
            return new HashSet<>(edges.get(v));
        }
        return new HashSet<>();
    }

    /**
     * Returns the amount of vertices in this {@link Graph}.
     * @return the amount of vertices in this {@link Graph}
     */
    public int size() {
        return edges.size();
    }

    /**
     * Returns whether there are vertices in this {@link Graph}.
     * @return true, if there are no vertices in this {@link Graph}; false otherwise
     */
    public boolean isEmpty() {
        return edges.isEmpty();
    }

    /**
     * Returns the amount of edges in this {@link Graph}.
     * @return the amount of edges in this {@link Graph}
     */
    public int getEdgeCount() {
        int edgeCount = 0;
        for (HashSet<Integer> set : edges.values()) {
            edgeCount += set.size();
        }
        edgeCount /= 2;
        return edgeCount;
    }

    /**
     * Returns whether there are edges in this {@link Graph}.
     * @return true, if there are edges in this {@link Graph}; false otherwise
     */
    public boolean hasEdges() {
        for (HashSet<Integer> set : edges.values()) {
            if (!set.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return A set of the ID's of all the vertices (even if they are disconnected)
     */
    public Set<Integer> getVertices() {
        return new HashSet<>(edges.keySet());
    }

    /**
     * @return True if every vertex has a path to every other vertex. False otherwise.
     */
    public boolean isConnected() {
        if (isEmpty()) {
            return true;
        }
        int startPos = getVertices().iterator().next();
        return connectedVertices(startPos).size() == getVertices().size();
    }

    /**
     * Returns a graph containing all vertices that have a path to "startPos".
     * The edges of these vertices stay unchanged and are included in the graph.
     */
    public Graph getConnectedGraph(int startPos) {
        Graph connGraph = new Graph();
        connectedVertices(startPos).forEach(x -> connGraph.edges.put(x, new HashSet<>(getNeighbors(x))));
        return connGraph;
    }

    /**
     * ONLY contains the ID's of the vertices, but no edges
     *
     * @param startPos the ID of the vertex where the algorithm starts its search
     * @return A set of all ID's that have a path to "startPos"
     */
    public Set<Integer> connectedVertices(int startPos) {
        if (!contains(startPos)) {
            return new HashSet<>();     //makes no sense if the vertex doesn't even exist
        }

        HashSet<Integer> current = new HashSet<>();
        current.add(startPos);
        HashSet<Integer> visited = new HashSet<>();

        while (current.size() > 0) {
            int cur = current.iterator().next();
            for (int nb : getNeighbors(cur)) {
                if (!visited.contains(nb))
                    current.add(nb);
            }
            current.remove(cur);
            visited.add(cur);
        }
        return visited;
    }

    /**
     * @return A set of all disjoint subGraphs.
     * If for example the graph has a "border" that separates it into 2 groups, a set of 2 Graphs is returned.
     * A singleton would therefore also be a disjoint subGraph.
     * If the graph is connected, it returns a set containing only one element, the graph itself.
     */
    public Set<Graph> getDisjointGraphs() {
        Set<Graph> disjointGraphs = new HashSet<>();
        Set<Integer> verticesLeft = new HashSet<>(getVertices());

        while (!verticesLeft.isEmpty()) {
            int startPos = verticesLeft.iterator().next();
            Graph disjointGraph = getConnectedGraph(startPos);
            disjointGraphs.add(disjointGraph);
            verticesLeft.removeAll(disjointGraph.getVertices());
        }
        return disjointGraphs;
    }

    /**@return an Integer representing the current state of this {@link Graph}
     */
    public int getSafePoint() {
        return undoStack.size();
    }

    /**
     * Restores a state of this {@link Graph} saved in an Integer.
     * @param safePoint is the ID of the state this {@link Graph} will be set to
     */
    public void restore(int safePoint) {
        while (undoStack.size() > safePoint) {
            undoStack.pop().undo();
        }
    }
}