package fr.arnodubois.datastructures.graph;

import fr.arnodubois.datastructures.tree.nary.NaryTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * An undirected weighted graph stored as an adjacency list.
 *
 * @param <E> the type of the vertices; they must implement {@code equals} and {@code hashCode}
 */
public class Graph<E> {
    private final Map<E, List<Edge<E>>> adjacency = new LinkedHashMap<>();
    private final List<Edge<E>> edges = new ArrayList<>();

    /**
     * Adds a vertex. Does nothing if it is already in the graph.
     *
     * @param vertex the vertex to add
     * @return {@code true} if the vertex was added
     */
    public boolean addVertex(final E vertex) {
        Objects.requireNonNull(vertex, "vertex");
        return this.adjacency.putIfAbsent(vertex, new ArrayList<>()) == null;
    }

    /**
     * Adds an undirected edge, and both vertices if they are missing.
     *
     * @param a      one end of the edge
     * @param b      the other end of the edge
     * @param weight the cost of the edge
     * @return the new edge, oriented from {@code a} to {@code b}
     */
    public Edge<E> addEdge(final E a, final E b, final int weight) {
        final Edge<E> edge = new Edge<>(a, b, weight);
        this.addVertex(a);
        this.addVertex(b);
        this.adjacency.get(a).add(edge);
        this.adjacency.get(b).add(edge.reversed());
        this.edges.add(edge);
        return edge;
    }

    /** @return the vertices, in insertion order */
    public Set<E> getVertices() {
        return Collections.unmodifiableSet(this.adjacency.keySet());
    }

    /** @return every edge once, in insertion order */
    public List<Edge<E>> getEdges() {
        return Collections.unmodifiableList(this.edges);
    }

    /**
     * @param vertex a vertex of the graph
     * @return the edges leaving {@code vertex}, each oriented from it
     * @throws NoSuchElementException if the vertex is not in the graph
     */
    public List<Edge<E>> getEdgesFrom(final E vertex) {
        final List<Edge<E>> vertexEdges = this.adjacency.get(vertex);
        if (vertexEdges == null) throw new NoSuchElementException("Unknown vertex: " + vertex);
        return Collections.unmodifiableList(vertexEdges);
    }

    /** @return {@code true} if the vertex is in the graph */
    public boolean containsVertex(final E vertex) {
        return this.adjacency.containsKey(vertex);
    }

    /** @return the number of vertices */
    public int vertexCount() {
        return this.adjacency.size();
    }

    /** @return the number of edges */
    public int edgeCount() {
        return this.edges.size();
    }

    /**
     * Computes a minimum spanning tree with Prim's algorithm, starting from the
     * first vertex that was added.
     *
     * @return the minimum spanning tree
     * @throws IllegalStateException if the graph is empty or not connected
     * @see #getSpanningTreeByPrim(Object)
     */
    public MinimumSpanningTree<E> getSpanningTreeByPrim() {
        if (this.adjacency.isEmpty()) throw new IllegalStateException("The graph is empty");
        return this.getSpanningTreeByPrim(this.adjacency.keySet().iterator().next());
    }

    /**
     * Computes a minimum spanning tree with Prim's algorithm.
     * <p>
     * The tree grows from {@code start}. At each step, the cheapest edge going from
     * the tree to a vertex outside it is taken from a priority queue and added.
     * This runs in O(E log E) time.
     *
     * @param start the vertex that becomes the root of the tree
     * @return the minimum spanning tree, rooted at {@code start}
     * @throws NoSuchElementException if {@code start} is not in the graph
     * @throws IllegalStateException  if some vertices cannot be reached from {@code start}
     */
    public MinimumSpanningTree<E> getSpanningTreeByPrim(final E start) {
        if (!this.containsVertex(start)) throw new NoSuchElementException("Unknown vertex: " + start);

        final Map<E, NaryTreeNode<E>> treeNodes = new HashMap<>();
        final NaryTreeNode<E> root = new NaryTreeNode<>(start);
        treeNodes.put(start, root);

        final List<Edge<E>> chosenEdges = new ArrayList<>(this.vertexCount() - 1);
        final PriorityQueue<Edge<E>> frontier = new PriorityQueue<>(Comparator.comparingInt(Edge::weight));
        frontier.addAll(this.adjacency.get(start));

        while (!frontier.isEmpty() && treeNodes.size() < this.vertexCount()) {
            final Edge<E> edge = frontier.poll();
            if (treeNodes.containsKey(edge.to())) continue;

            final NaryTreeNode<E> child = new NaryTreeNode<>(edge.to());
            treeNodes.get(edge.from()).addChild(child);
            treeNodes.put(edge.to(), child);
            chosenEdges.add(edge);

            for (final Edge<E> next : this.adjacency.get(edge.to())) {
                if (!treeNodes.containsKey(next.to())) frontier.add(next);
            }
        }

        if (treeNodes.size() < this.vertexCount()) {
            throw new IllegalStateException("The graph is not connected: only " + treeNodes.size() + " of "
                    + this.vertexCount() + " vertices can be reached from " + start);
        }
        return new MinimumSpanningTree<>(root, chosenEdges);
    }
}
