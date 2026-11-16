package fr.arnodubois.datastructures.tree.binary;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * A node of a {@link BinarySearchTree}. It caches its own height, so balance
 * factors are computed in constant time.
 */
final class BinaryNode<E extends Comparable<E>> {
    private static final String BRANCH_ABOVE = "╭─";
    private static final String BRANCH_BELOW = "╰─";
    private static final String VERTICAL = "│ ";
    private static final String BLANK = "  ";

    private final E value;
    private BinaryNode<E> left;
    private BinaryNode<E> right;
    private int height = 1;

    BinaryNode(final E value) {
        this.value = value;
    }

    static int heightOf(final BinaryNode<?> node) {
        return node == null ? 0 : node.height;
    }

    E getValue() {
        return this.value;
    }

    /** @return height(left) - height(right) */
    int getBalanceFactor() {
        return BinaryNode.heightOf(this.left) - BinaryNode.heightOf(this.right);
    }

    /**
     * Inserts a value below this node and updates the heights on the way back up.
     *
     * @return {@code false} if the value was already present
     */
    boolean add(final E newValue) {
        final int comparison = newValue.compareTo(this.value);
        final boolean added;
        if (comparison < 0) {
            if (this.left == null) {
                this.left = new BinaryNode<>(newValue);
                added = true;
            } else {
                added = this.left.add(newValue);
            }
        } else if (comparison > 0) {
            if (this.right == null) {
                this.right = new BinaryNode<>(newValue);
                added = true;
            } else {
                added = this.right.add(newValue);
            }
        } else {
            return false;
        }
        this.height = 1 + Math.max(BinaryNode.heightOf(this.left), BinaryNode.heightOf(this.right));
        return added;
    }

    boolean contains(final E searched) {
        BinaryNode<E> node = this;
        while (node != null) {
            final int comparison = searched.compareTo(node.value);
            if (comparison == 0) return true;
            node = comparison < 0 ? node.left : node.right;
        }
        return false;
    }

    boolean isAVL() {
        return Math.abs(this.getBalanceFactor()) <= 1
                && (this.left == null || this.left.isAVL())
                && (this.right == null || this.right.isAVL());
    }

    void prefix(final List<E> values) {
        values.add(this.value);
        if (this.left != null) this.left.prefix(values);
        if (this.right != null) this.right.prefix(values);
    }

    void infix(final List<E> values) {
        if (this.left != null) this.left.infix(values);
        values.add(this.value);
        if (this.right != null) this.right.infix(values);
    }

    void postfix(final List<E> values) {
        if (this.left != null) this.left.postfix(values);
        if (this.right != null) this.right.postfix(values);
        values.add(this.value);
    }

    void byWidth(final List<E> values) {
        final Queue<BinaryNode<E>> queue = new ArrayDeque<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            final BinaryNode<E> node = queue.poll();
            values.add(node.value);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
    }

    String toPrettyString() {
        if (this.left == null && this.right == null) return "[" + this.value + "]";
        return "[" + this.value + "("
                + (this.left == null ? "-" : this.left.toPrettyString()) + ", "
                + (this.right == null ? "-" : this.right.toPrettyString()) + ")]";
    }

    String toTreeString() {
        final StringBuilder builder = new StringBuilder();
        this.appendTree(builder, "", "", "", "");
        return builder.toString();
    }

    /**
     * Appends this subtree. {@code prefix} is printed before this node's value,
     * {@code indent} before its descendants, and {@code aboveIndent} /
     * {@code belowIndent} extend the indent for the left / right subtree so that a
     * vertical line joins each child to its parent.
     */
    private void appendTree(final StringBuilder builder, final String indent, final String prefix,
                            final String aboveIndent, final String belowIndent) {
        if (this.left != null) {
            this.left.appendTree(builder, indent + aboveIndent, BinaryNode.BRANCH_ABOVE,
                    BinaryNode.BLANK, BinaryNode.VERTICAL);
        }
        builder.append(indent).append(prefix)
                .append(this.value).append('(').append(this.getBalanceFactor()).append(")\n");
        if (this.right != null) {
            this.right.appendTree(builder, indent + belowIndent, BinaryNode.BRANCH_BELOW,
                    BinaryNode.VERTICAL, BinaryNode.BLANK);
        }
    }
}
