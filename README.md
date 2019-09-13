# Vertex cover solver

Reads in undirected graph with no multiple edges per two vertices.
Calculates the minimum number of vertices you need to cover every edges at least once with a vertex.

---

By now it contains many parts that don't speed up calculation on small inputs noticably. The main changes that
improved runtime were:
* Creating the method removeClique, which removes cliques of any size n when less than n vertices are connected outside of the clique.
* Applying as many rules before you try to solve for K, so you only have to do it once.

---

An undo stack was also created, so that we don't have to make a copy of the graph every time we go one layer deeper into
the search tree. While this change was beneficial from what our tests say so far, the runtime reduction was only about 20%.
The rules described above on the other hand changed it by a factor of at least 10 to put it into persepective.
