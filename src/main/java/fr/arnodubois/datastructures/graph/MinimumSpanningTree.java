package fr.arnodubois.datastructures.graph;

import fr.arnodubois.datastructures.tree.nary.NaryTreeNode;

import java.util.List;

/**
 * The result of a minimum spanning tree computation.
 *
 * @param root  the tree, rooted at the start vertex
 * @param edges the edges of the tree, in the order they were chosen
 * @param <E>   the type of the vertices
 */
public record MinimumSpanningTree<E>(NaryTreeNode<E> root, List<Edge<E>> edges) {
    public MinimumSpanningTree {
        edges = List.copyOf(edges);
    }

    /** @return the sum of the weights of the edges */
    public int totalWeight() {
        return this.edges.stream().mapToInt(Edge::weight).sum();
    }
}
