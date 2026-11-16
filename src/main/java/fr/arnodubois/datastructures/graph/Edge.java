package fr.arnodubois.datastructures.graph;

import java.util.Objects;

/**
 * A weighted edge between two vertices. In an undirected {@link Graph}, each
 * edge is stored once per direction, so {@code from} is the vertex the edge
 * was reached from.
 *
 * @param from   the start vertex
 * @param to     the end vertex
 * @param weight the cost of the edge
 * @param <E>    the type of the vertices
 */
public record Edge<E>(E from, E to, int weight) {
    public Edge {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
    }

    /** @return the same edge in the opposite direction */
    public Edge<E> reversed() {
        return new Edge<>(this.to, this.from, this.weight);
    }

    @Override
    public String toString() {
        return this.from + " -(" + this.weight + ")- " + this.to;
    }
}
