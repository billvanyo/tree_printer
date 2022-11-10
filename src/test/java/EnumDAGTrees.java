import org.junit.jupiter.api.Test;
import tech.vanyo.treePrinter.TreeNode;
import tech.vanyo.treePrinter.TreePrinter;

import java.util.ArrayList;
import java.util.List;

public class EnumDAGTrees {

    // This tests treePrinter by enumerating trees of a given size.
    // Note that these trees are actually Directed Acyclic Graphs (DAGs), in that two identical subtrees are
    // represented by a single representation.
    // treePrinter doesn't know the difference; it traverses the structure as a tree.
    // Also note that treePrinter doesn't detect cycles (don't give it graphs with cycles)

    @Test
    public void enumDAGTrees() {

        List<TreeNode> trees = enumTrees(7);

        /*
            We declare a TreePrinter object, parameterized with the type of tree object it will be printing (in this
            case tech.vanyo.treePrinter.TreeNode), and call the TreePrinter constructor, providing lambda functions to get the tech.vanyo.treePrinter.TreeNode's
            label as a String, and to get the left and right and right subtrees.
         */
        TreePrinter<TreeNode> printer = new TreePrinter<>(n -> ""+n.getValue(),
                TreeNode::getLeft,
                TreeNode::getRight);

        // this prints trees in rows across the page
        printer.setSquareBranches(true);
        printer.printTrees(trees,120);

    }

    @SuppressWarnings("unchecked")
    public static List<TreeNode> enumTrees(int n) {
        List<TreeNode>[] subProblems = new ArrayList[n + 1];

        subProblems[0] = new ArrayList<>();
        subProblems[0].add(null);

        for (int totalNodes = 1; totalNodes <= n; totalNodes++) {
            subProblems[totalNodes] = new ArrayList<>();

            for (int rightCount = 0; rightCount < totalNodes; rightCount++) {
                int leftCount = totalNodes - rightCount - 1;
                List<TreeNode> leftTrees = subProblems[leftCount];
                List<TreeNode> rightTrees = subProblems[rightCount];
                for (TreeNode leftNode : leftTrees) {
                    for (TreeNode rightNode : rightTrees) {
                        TreeNode newRoot = new TreeNode(0, leftNode, rightNode);
                        subProblems[totalNodes].add(newRoot);
                    }
                }
            }
        }
        return subProblems[n];
    }
}
