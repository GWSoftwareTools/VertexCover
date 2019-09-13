# Vertex cover solver

Reads in undirected graph with no multiple edges per two vertices.
Calculates the minimum number of vertices you need to cover every edges at least once with a vertex. (Vertex cover problem)

---

By now it contains many parts that don't speed up calculation on small inputs noticably. On very big instances though, they are worth it. For example the split into disjoint subGraphs actually slows the program down in most cases. But if you hit one very big graph that can be split into disjoint subGraphs, the speedup may be 50-fold or more. And as were mostly optimizing for the worst case anyway, we are willing to take that drawback.

The main changes that improved the runtime on all inputs were:
* Creating the method removeClique, which removes cliques of any size n when less than n vertices are connected outside of the clique.
* Applying as many rules before you try to solve for K, so you only have to do it once.
* Using the base-datastructures Hashmap and Hashset for two reasons: Not having to manage indices reduces complexity and risk
for programming erros AND hashing is very time-efficient.
---

An undo stack was also created, so that we don't have to make a copy of the graph every time we go one layer deeper into
the search tree. While this change was beneficial from what our tests say so far, the runtime reduction was only about 20%.
The rules described above on the other hand changed it by a factor of at least 10 to put it into persepective.
