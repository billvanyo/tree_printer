import org.junit.jupiter.api.Test;
import tech.vanyo.treePrinter.TreeNode;
import tech.vanyo.treePrinter.TreePrinter;

public class CompleteTree {

    static TreePrinter<TreeNode> printer = new TreePrinter<>(n -> ("" + n.getValue()), TreeNode::getLeft, TreeNode::getRight)
            .setLabelGap(2)
            .setSquareBranches(true);

    @Test
    public void levelOrder() {
        TreeNode tree = completeLevelOrderTree(90);
        printer.printTree(tree);
    }

    @Test
    public void inOrder() {
        TreeNode tree = completeInOrderTree(1, 90);
        printer.printTree(tree);
    }

    public static TreeNode completeInOrderTree(int first, int last) {
        if (first > last) return null;
        if (first == last) return new TreeNode(first);
        // size = total number of nodes in tree
        int size = last - first + 1;
        // number of nodes on next to last level (a power of 2)
        int nextToLastLevelCount = maxPowerOf2Under(size / 2);
        // number of nodes on last level (which may be less than power of 2)
        int lastLevelCount = size - (nextToLastLevelCount * 2) + 1;
        // number of nodes in left subtree
        int leftSize = nextToLastLevelCount - 1 + Math.min(lastLevelCount, nextToLastLevelCount);
        int rootVal = first + leftSize;
        return new TreeNode(rootVal, completeInOrderTree(first, rootVal - 1), completeInOrderTree(rootVal + 1, last));
    }

    public static TreeNode completeLevelOrderTree(int size) {
        TreeNode[] nodes = new TreeNode[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new TreeNode(i + 1);
        }
        int i = 0;
        while (i * 2 + 1 < size) {
            nodes[i].setLeft(nodes[i * 2 + 1]);
            if (i * 2 + 2 < size) nodes[i].setRight(nodes[i * 2 + 2]);
            i++;
        }
        return nodes[0];
    }

    public static int maxPowerOf2Under(int limit) {
        int lzs = Integer.numberOfLeadingZeros(limit);
        return 1 << (32 - lzs - 1);
    }
}
