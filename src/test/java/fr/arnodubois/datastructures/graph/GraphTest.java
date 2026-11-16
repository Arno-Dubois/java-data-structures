package fr.arnodubois.datastructures.graph;

import com.google.gson.JsonParseException;
import fr.arnodubois.datastructures.tree.nary.NaryTreeNode;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    /**
     * <pre>
     *   A --1-- B
     *   |     / |
     *   4   2   5
     *   | /     |
     *   C --3-- D
     * </pre>
     * The minimum spanning tree is A-B (1), B-C (2), C-D (3): total 6.
     */
    private static Graph<String> squareGraph() {
        final Graph<String> graph = new Graph<>();
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 2);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 3);
        return graph;
    }

    @Test
    void edgesAreUndirected() {
        final Graph<String> graph = squareGraph();
        assertEquals(4, graph.vertexCount());
        assertEquals(5, graph.edgeCount());
        assertEquals(List.of(new Edge<>("B", "A", 1), new Edge<>("B", "C", 2), new Edge<>("B", "D", 5)),
                graph.getEdgesFrom("B"));
    }

    @Test
    void addVertexIgnoresDuplicates() {
        final Graph<String> graph = new Graph<>();
        assertTrue(graph.addVertex("A"));
        assertFalse(graph.addVertex("A"));
        assertEquals(1, graph.vertexCount());
    }

    @Test
    void primOnSmallGraph() {
        final MinimumSpanningTree<String> mst = squareGraph().getSpanningTreeByPrim("A");
        assertEquals(6, mst.totalWeight());
        assertEquals(List.of(new Edge<>("A", "B", 1), new Edge<>("B", "C", 2), new Edge<>("C", "D", 3)),
                mst.edges());
        assertEquals("""
                A
                |-B
                  |-C
                    |-D
                """, mst.root().toPrettyText());
    }

    @Test
    void primGivesTheSameWeightFromEveryStart() {
        final Graph<String> graph = squareGraph();
        for (final String start : graph.getVertices()) {
            final MinimumSpanningTree<String> mst = graph.getSpanningTreeByPrim(start);
            assertEquals(6, mst.totalWeight());
            assertEquals(start, mst.root().getValue());
        }
    }

    @Test
    void primOnSingleVertex() {
        final Graph<String> graph = new Graph<>();
        graph.addVertex("A");
        final MinimumSpanningTree<String> mst = graph.getSpanningTreeByPrim();
        assertEquals(0, mst.totalWeight());
        assertTrue(mst.root().isLeaf());
    }

    @Test
    void primFailsOnDisconnectedGraph() {
        final Graph<String> graph = squareGraph();
        graph.addEdge("X", "Y", 1);
        final IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> graph.getSpanningTreeByPrim("A"));
        assertTrue(e.getMessage().contains("not connected"));
    }

    @Test
    void primFailsOnEmptyGraphOrUnknownStart() {
        assertThrows(IllegalStateException.class, () -> new Graph<String>().getSpanningTreeByPrim());
        assertThrows(NoSuchElementException.class, () -> squareGraph().getSpanningTreeByPrim("Z"));
    }

    @Test
    void primOnEuropeRailNetwork() {
        final Graph<String> graph = GraphJsonLoader.fromResource("europe-rail-network.json");
        assertEquals(47, graph.vertexCount());
        assertEquals(90, graph.edgeCount());

        final MinimumSpanningTree<String> mst = graph.getSpanningTreeByPrim("Paris");
        assertEquals(graph.vertexCount() - 1, mst.edges().size());
        assertEquals(mst.root().getSize(), graph.vertexCount());
        assertEquals(Set.copyOf(graph.getVertices()), new HashSet<>(mst.root().toPrefixList()));
        // Checked independently with Kruskal's algorithm.
        assertEquals(109, mst.totalWeight());
    }

    @Test
    void spanningTreeMatchesItsEdges() {
        final MinimumSpanningTree<String> mst = GraphJsonLoader.fromResource("europe-rail-network.json")
                .getSpanningTreeByPrim("Paris");
        final Set<String> treeLinks = new HashSet<>();
        collectLinks(mst.root(), treeLinks);
        final Set<String> edgeLinks = new HashSet<>();
        for (final Edge<String> edge : mst.edges()) edgeLinks.add(edge.from() + ">" + edge.to());
        assertEquals(edgeLinks, treeLinks);
    }

    private static void collectLinks(final NaryTreeNode<String> node, final Set<String> links) {
        for (final NaryTreeNode<String> child : node.getChildren()) {
            links.add(node.getValue() + ">" + child.getValue());
            collectLinks(child, links);
        }
    }

    @Test
    void loaderRejectsInvalidJson() {
        assertThrows(JsonParseException.class,
                () -> GraphJsonLoader.fromJson(new StringReader("[{\"weight\": 1, \"nodes\": [\"A\"]}]")));
        assertThrows(JsonParseException.class, () -> GraphJsonLoader.fromJson(new StringReader("")));
        assertThrows(NoSuchElementException.class, () -> GraphJsonLoader.fromResource("missing.json"));
    }
}
