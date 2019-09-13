# Vertex cover solver

Reads in an undirected graph with no multiple edges per two vertices.
Calculates the minimum number of vertices you need to cover every edges at least once with a vertex. (Vertex cover problem)

---
## Datastructure
For representing the graph we decided to use a **[HashMap](https://docs.oracle.com/javase/10/docs/api/java/util/HashMap.html "JavaDoc")** that maps from integer to a **[HashSet](https://docs.oracle.com/javase/10/docs/api/java/util/HashSet.html "JavaDoc")** of integers. This means: The key is the ID of a vertex and the value (the set) are all of the neighbours. 
As an example for the following graph:  
<img src="https://raw.githubusercontent.com/GWSoftwareTools/VertexCover/master/pictures/graph.png" width="40%" alt="simple graph">
* 1 -> {2,3}
* 2 -> {3,1}
* 3 -> {2,4,1}
* 4 -> {3}

---

This method uses way less space than any solution with a matrix, and still has reasonably low runtime (the runtime is mostly
dependent on the logic of the searchtree anyway).
Also, we don't have to manage any indexes of a list if we use a set.

---

## Reduction Rules

The reduction rules are all applied exhaustively, meaning the are repeated as often as possible until they don't change anything anymore. For this reason their implementations all return a boolean which indicates wether a change has been made to the graph/instance. The rules all are applied until one run occurs where nothing has happened, then it stops. We always have to apply all rules, because one rule may create an opportunity for another rule to be applied. As we can't really anticipate these side-effects (yet?), so we always have to apply all of them.

* ### removeCliques: 

A **clique** is a set of vertices which are ALL connected to each other vertex in the clique. For example a single point,
two connected vertices or a triangle are (simple) cases of a clique. If we find a clique of **size n and only `n-1` vertices
are connected to a vertex outside of the clique**, we can remove the clique and reduce the parent instance by `n-1`.
This is a generalization of the "singleton" and "degree-one" rule => It also works on arbitrarily big cliques.  
Example:  
<img src="https://raw.githubusercontent.com/GWSoftwareTools/VertexCover/master/pictures/removeCliques.png" width="50%" alt="removeCliques">  
The vertices `1`, `2` and `3` have edges between each other and only `2` and `3` have even more neighbours. All vertices in this triangle where deleted and `k` decreased by `3`.


* ### removeP3:

If a vertex "key" is ONLY connected to 2 neighbours "nb1" and "nb2", who are themselves **not neighbours of each other**, we can
remove "key", and merge both neighbours together, which means deleting one of them and moving the connections of the
deleted one onto the remaining one. This is done in the method "mergeVertices". It doesn't really affect the runtime
in which direction the merge operation is done.  
Example:  
<img src="https://raw.githubusercontent.com/GWSoftwareTools/VertexCover/master/pictures/removeP3.png" width="70%" alt="removeP3">  
The vertices `2` and `3` aren't neighbours so one of them (in this example `3`) was deleted and their neighbours merged.


* ### removeBigNeighbour:

If there exist two adjacent vertices v1 and v2 and the set of neighbours of v1 is a **subset** of the neighbours of
v2, we can remove v2 and reduce k by 1.
You can visualize this rule this way: As v1 and v2 are connected, at least one of the has to be included in the
vertex cover to cover the edge between them. Because v2 also covers every edge v1 covers, maybe even more, it
is in every case worth it to take it over v1. If v1 and v2 have the same set of neighbours, this rule can
be applied in both direction with no difference.  
Example:  
<img src="https://raw.githubusercontent.com/GWSoftwareTools/VertexCover/master/pictures/removeBigNeighbour.png" width="100%" alt="removeBigNeighbour">  
Because vertex `2` has all the neighbours vertex `1` has and even some more, vertex `2` was deleted.  


* ### removeHighDeg:

An implementation of the high-degree rule which removes vertices with more neighbours than the value of `k`.

---

## Overview 
We split the project into two packages *core* and *vertex cover*, for the graph functionality that is indipendent from the problem and for that parts that aren't.

By now it contains many parts that don't speed up calculation on small inputs noticably. On very **big instances** though, they are worth it. For example the split into disjoint subGraphs actually slows the program down in most cases. But if you hit one very big graph that can be split into disjoint subGraphs, the speedup may be 50-fold or more. And as we're mostly optimizing for the worst case anyway, we're willing to take that drawback.

### The main changes that improved the runtime on all inputs were:
* Creating the method removeClique, which removes cliques of any size `n` when less than `n` vertices are connected outside of the clique.
* Applying as many rules before you try to solve for `k`, so you only have to do it once (doesn't work for the high-degree rule, as it depends on the value `k` of an instance, but all other rules are applicable).
* Using the datastructures Hashmap and Hashset.

---

## Undo-Stack
An [UndoStack](./src/vertexCover/advanced/UndoStack.java "UndoStack") was also created, so that we don't have to make a copy of the graph every time we go one layer deeper into
the search tree. While this change was beneficial from what our tests say so far, the runtime reduction was only about 20%.
The rules described above on the other hand changed it by a factor of at least 10 to put it into persepective.
