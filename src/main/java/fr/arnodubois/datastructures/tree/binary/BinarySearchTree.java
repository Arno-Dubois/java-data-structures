package fr.arnodubois.datastructures.tree.binary;

import java.util.ArrayList;
import java.util.List;

/**
 * An unbalanced binary search tree.
 * <p>
 * Values are kept in sorted order and duplicates are ignored. The tree does not
 * rebalance itself, but it tracks every node's height so it can report balance
 * factors and check whether it satisfies the AVL balance property.
 *
 * @param <E> the type of the values, which must be comparable
 */
public class BinarySearchTree<E extends Comparable<E>> {
    private BinaryNode<E> root;
    private int size;

    /**
     * Adds each value to the tree, in order.
     *
     * @param values the values to add
     */
    @SafeVarargs
    public final void addAll(final E... values) {
        for (final E value : values) {
            this.add(value);
        }
    }

    /**
     * Adds a value to the tree.
     *
     * @param value the value to add, not {@code null}
     * @return {@code true} if the value was added, {@code false} if it was already present
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public boolean add(final E value) {
        if (value == null) throw new NullPointerException("BinarySearchTree does not accept null values");
        if (this.root == null) {
            this.root = new BinaryNode<>(value);
            this.size = 1;
            return true;
        }
        final boolean added = this.root.add(value);
        if (added) this.size++;
        return added;
    }

    /**
     * Returns whether the tree contains a value, in O(height) time.
     *
     * @param value the value to look for
     * @return {@code true} if the value is in the tree
     */
    public boolean contains(final E value) {
        return value != null && this.root != null && this.root.contains(value);
    }

    /** @return the number of values in the tree */
    public int size() {
        return this.size;
    }

    /** @return {@code true} if the tree has no values */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /** @return the number of levels in the tree (0 when empty, 1 for a single node) */
    public int getHeight() {
        return BinaryNode.heightOf(this.root);
    }

    /**
     * Checks the AVL balance property: for every node, the heights of the left and
     * right subtrees differ by at most one.
     *
     * @return {@code true} if every node has a balance factor in [-1, 1]
     */
    public boolean isAVL() {
        return this.root == null || this.root.isAVL();
    }

    /** @return the values in prefix (pre-order) order: node, left, right */
    public List<E> toPrefixList() {
        final List<E> values = new ArrayList<>(this.size);
        if (this.root != null) this.root.prefix(values);
        return values;
    }

    /** @return the values in infix (in-order) order, which is sorted ascending */
    public List<E> toInfixList() {
        final List<E> values = new ArrayList<>(this.size);
        if (this.root != null) this.root.infix(values);
        return values;
    }

    /** @return the values in postfix (post-order) order: left, right, node */
    public List<E> toPostfixList() {
        final List<E> values = new ArrayList<>(this.size);
        if (this.root != null) this.root.postfix(values);
        return values;
    }

    /** @return the values level by level (breadth-first), left to right */
    public List<E> toByWidthList() {
        final List<E> values = new ArrayList<>(this.size);
        if (this.root != null) this.root.byWidth(values);
        return values;
    }

    /**
     * Draws the tree sideways: the root is on the left, the smaller values are
     * above it and the bigger values are below. Each value is followed by its
     * balance factor in parentheses.
     *
     * <pre>
     *   ╭─2(0)
     * ╭─3(0)
     * │ ╰─4(0)
     * 5(0)
     * ╰─7(-1)
     *   ╰─8(0)
     * </pre>
     *
     * @return the drawing, or an empty string if the tree is empty
     */
    public String toTreeString() {
        if (this.root == null) return "";
        return this.root.toTreeString();
    }

    /**
     * @return a compact one-line representation, e.g. {@code [5([3([2], [4])], [7(-, [8])])]}
     */
    @Override
    public String toString() {
        return this.root == null ? "[]" : this.root.toPrettyString();
    }
}
