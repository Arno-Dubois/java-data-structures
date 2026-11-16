package fr.arnodubois.datastructures;

import fr.arnodubois.datastructures.graph.Graph;
import fr.arnodubois.datastructures.graph.GraphJsonLoader;
import fr.arnodubois.datastructures.graph.MinimumSpanningTree;
import fr.arnodubois.datastructures.tree.binary.BinarySearchTree;

/**
 * Small demo of the library. Run it with {@code ./mvnw -q compile exec:java}.
 */
public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        binarySearchTreeDemo();
        System.out.println();
        minimumSpanningTreeDemo();
    }

    private static void binarySearchTreeDemo() {
        System.out.println("=== Binary search tree ===");
        final BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.addAll(5, 3, 2, 4, 7, 6, 8, 9, 10);

        System.out.println("Tree (value(balance factor)):");
        System.out.print(tree.toTreeString());
        System.out.println("In order:     " + tree.toInfixList());
        System.out.println("Pre order:    " + tree.toPrefixList());
        System.out.println("Post order:   " + tree.toPostfixList());
        System.out.println("Level order:  " + tree.toByWidthList());
        System.out.println("Height:       " + tree.getHeight());
        System.out.println("AVL balanced: " + tree.isAVL());
    }

    private static void minimumSpanningTreeDemo() {
        System.out.println("=== Minimum spanning tree of a European rail network (Prim) ===");
        final Graph<String> graph = GraphJsonLoader.fromResource("europe-rail-network.json");
        System.out.println(graph.vertexCount() + " cities, " + graph.edgeCount() + " connections");

        final MinimumSpanningTree<String> spanningTree = graph.getSpanningTreeByPrim("Paris");
        System.out.println("Total weight: " + spanningTree.totalWeight());
        System.out.print(spanningTree.root().toPrettyText());
    }
}
