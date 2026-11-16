package fr.arnodubois.datastructures.tree.binary;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeTest {
    private static BinarySearchTree<Integer> sampleTree() {
        //        5
        //      /   \
        //     3     7
        //    / \   / \
        //   2   4 6   8
        final BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.addAll(5, 3, 7, 2, 4, 6, 8);
        return tree;
    }

    @Test
    void emptyTree() {
        final BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertEquals(0, tree.getHeight());
        assertTrue(tree.isAVL());
        assertEquals(List.of(), tree.toInfixList());
        assertEquals("", tree.toTreeString());
        assertEquals("[]", tree.toString());
    }

    @Test
    void traversals() {
        final BinarySearchTree<Integer> tree = sampleTree();
        assertEquals(List.of(2, 3, 4, 5, 6, 7, 8), tree.toInfixList());
        assertEquals(List.of(5, 3, 2, 4, 7, 6, 8), tree.toPrefixList());
        assertEquals(List.of(2, 4, 3, 6, 8, 7, 5), tree.toPostfixList());
        assertEquals(List.of(5, 3, 7, 2, 4, 6, 8), tree.toByWidthList());
    }

    @Test
    void duplicatesAreIgnored() {
        final BinarySearchTree<Integer> tree = sampleTree();
        assertFalse(tree.add(4));
        assertEquals(7, tree.size());
        assertEquals(List.of(2, 3, 4, 5, 6, 7, 8), tree.toInfixList());
    }

    @Test
    void nullIsRejected() {
        assertThrows(NullPointerException.class, () -> new BinarySearchTree<Integer>().add(null));
    }

    @Test
    void contains() {
        final BinarySearchTree<Integer> tree = sampleTree();
        for (int i = 2; i <= 8; i++) assertTrue(tree.contains(i), "should contain " + i);
        assertFalse(tree.contains(1));
        assertFalse(tree.contains(9));
        assertFalse(tree.contains(null));
    }

    @Test
    void worksWithComparatorsThatReturnAnyInteger() {
        // String.compareTo returns the difference between characters, not just -1/0/1.
        final BinarySearchTree<String> tree = new BinarySearchTree<>();
        tree.addAll("m", "a", "z", "b", "y");
        assertEquals(List.of("a", "b", "m", "y", "z"), tree.toInfixList());
        assertEquals(5, tree.size());
    }

    @Test
    void heightAndAVL() {
        final BinarySearchTree<Integer> tree = sampleTree();
        assertEquals(3, tree.getHeight());
        assertTrue(tree.isAVL());

        tree.add(9);
        tree.add(10); // 8 -> 9 -> 10 is now a chain on the right
        assertEquals(5, tree.getHeight());
        assertFalse(tree.isAVL());
    }

    @Test
    void degenerateTreeIsNotAVL() {
        final BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.addAll(1, 2, 3);
        assertEquals(3, tree.getHeight());
        assertFalse(tree.isAVL());
    }

    @Test
    void toTreeString() {
        final BinarySearchTree<Integer> tree = sampleTree();
        tree.add(9);
        assertEquals("""
                  ╭─2(0)
                ╭─3(0)
                │ ╰─4(0)
                5(-1)
                │ ╭─6(0)
                ╰─7(-1)
                  ╰─8(-1)
                    ╰─9(0)
                """, tree.toTreeString());
    }

    @Test
    void testToString() {
        final BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.addAll(5, 3, 7, 8);
        assertEquals("[5([3], [7(-, [8])])]", tree.toString());
    }
}
