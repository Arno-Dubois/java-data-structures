# Java Data Structures

[![CI](https://github.com/Arno-Dubois/java-data-structures/actions/workflows/ci.yml/badge.svg)](https://github.com/Arno-Dubois/java-data-structures/actions/workflows/ci.yml)

Generic trees and graphs written from scratch in Java 21, with unit tests:

| Structure | Main features |
|---|---|
| [`BinarySearchTree<E>`](src/main/java/fr/arnodubois/datastructures/tree/binary/BinarySearchTree.java) | insertion, search, the four traversals, height, balance factors, AVL check, sideways tree drawing |
| [`NaryTreeNode<E>`](src/main/java/fr/arnodubois/datastructures/tree/nary/NaryTreeNode.java) | any number of children, traversals, size/height/leaf count, JSON export, pretty printing |
| [`Graph<E>`](src/main/java/fr/arnodubois/datastructures/graph/Graph.java) | undirected weighted graph (adjacency list), **minimum spanning tree with Prim's algorithm**, JSON loading |

The pieces fit together: Prim's algorithm returns its spanning tree as an `NaryTreeNode`, rooted at the start vertex.

## Quick start

You only need a JDK 21 or newer. The Maven wrapper downloads Maven for you.

```bash
./mvnw verify                 # build and run the tests
./mvnw -q compile exec:java   # run the demo
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Examples

### Binary search tree

```java
BinarySearchTree<Integer> tree = new BinarySearchTree<>();
tree.addAll(5, 3, 2, 4, 7, 6, 8, 9, 10);

tree.toInfixList();    // [2, 3, 4, 5, 6, 7, 8, 9, 10]
tree.toByWidthList();  // [5, 3, 7, 2, 4, 6, 8, 9, 10]
tree.getHeight();      // 5
tree.isAVL();          // false
System.out.print(tree.toTreeString());
```

`toTreeString()` draws the tree sideways, with each node's balance factor in parentheses:

```
  ╭─2(0)
╭─3(0)
│ ╰─4(0)
5(-2)
│ ╭─6(0)
╰─7(-2)
  ╰─8(-2)
    ╰─9(-1)
      ╰─10(0)
```

### Minimum spanning tree of a rail network

[`europe-rail-network.json`](src/main/resources/europe-rail-network.json) describes 47 European cities and 90 rail connections, based on a board game map:

```json
[
  {"weight": 4, "nodes": ["Edinburgh", "London"]},
  {"weight": 2, "nodes": ["Amsterdam", "London"]},
  ...
]
```

```java
Graph<String> graph = GraphJsonLoader.fromResource("europe-rail-network.json");
MinimumSpanningTree<String> mst = graph.getSpanningTreeByPrim("Paris");

mst.totalWeight();                          // 109
System.out.print(mst.root().toPrettyText());
```

```
Paris
|-Dieppe
  |-Brest
|-Bruxelles
  |-Amsterdam
    |-London
      |-Edinburgh
  |-Frankfurt
    |-Munchen
...
```

## How Prim's algorithm is implemented

1. Start with a tree that holds only the start vertex, and put its edges in a priority queue sorted by weight.
2. Take the cheapest edge from the queue. If it leads to a vertex already in the tree, skip it.
3. Otherwise, add that vertex to the tree as a child of the vertex it was reached from, and queue its edges.
4. Repeat until every vertex is in the tree.

This runs in **O(E log E)** time. If the queue empties before every vertex is reached, the graph is not connected and the method throws an `IllegalStateException`. The tests check the result against a weight computed separately with Kruskal's algorithm.

## Project structure

```
src/main/java/fr/arnodubois/datastructures/
├── Main.java                     demo
├── tree/binary/                  BinarySearchTree, BinaryNode
├── tree/nary/                    NaryTreeNode
└── graph/                        Graph, Edge, MinimumSpanningTree, GraphJsonLoader
src/test/java/...                 JUnit 5 tests for each package
```

## Background

I wrote these as separate assignments for my algorithms and object-oriented programming courses in 2025 (`arboricol`, `NaryTreeNode` and `graphicol`), then merged them into one project and cleaned them up.

`NaryTreeNode` and its tests come from course material.
