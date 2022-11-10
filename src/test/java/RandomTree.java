import org.junit.jupiter.api.Test;
import tech.vanyo.treePrinter.TreeNode;
import tech.vanyo.treePrinter.TreePrinter;

import java.util.Random;

public class RandomTree {

    private static final Random r = new Random(0); // well, that's not a test per-se but a visualization
    private static final TreeNode tree = randomTree(30);
    

    /*
        We declare a TreePrinter object, parameterized with the type of tree object it will be printing (in this
        case tech.vanyo.treePrinter.TreeNode), and call the TreePrinter constructor, providing lambda functions to get the tech.vanyo.treePrinter.TreeNode's
        label as a String, and to get the left and right and right subtrees.
     */

    private static TreePrinter<TreeNode> textualPrinter =
        new TreePrinter<>(n -> nameForNumber(n.getValue()), TreeNode::getLeft, TreeNode::getRight);

    private TreePrinter<TreeNode> numericPrinter =
        new TreePrinter<>(n -> "" + n.getValue(), TreeNode::getLeft, TreeNode::getRight);

    @Test
    public void textualPrint() {
        textualPrinter
            .setLabelGap(1) // set minimum horizontal spacing between node labels with setHspace
            .setSquareBranches(true) // use square branches
            .printTree(tree);
    }

    @Test
    public void numericPrint() {
        numericPrinter
            .setLabelGap(1)
            .setSquareBranches(true)
            .printTree(tree);
    }

    @Test
    public void lrAgnosticPrint() {
        // single left/right subtree as straight down branch (i.e. no indication of left or right)
        numericPrinter
            .setLrAgnostic(true)
            .setSquareBranches(true)
            .printTree(tree);
    }

    @Test
    public void diagonalPrint() {
        numericPrinter.printTree(tree);
    }

    @Test
    public void hspacePrint() {
        numericPrinter
            .setLabelGap(3)
            .setSquareBranches(true)
            .printTree(tree);
    }

    public static TreeNode randomTree(int n) {
        return randomTree(1, n);
    }

    private static TreeNode randomTree(int firstValue, int lastValue) {
        if (firstValue > lastValue) return null;
        else {
            int treeSize = lastValue - firstValue + 1;
            int leftCount = r.nextInt(treeSize);
            int rightCount = treeSize - leftCount - 1;
            TreeNode root = new TreeNode(firstValue + leftCount);
            root.setLeft(randomTree(firstValue, firstValue + leftCount - 1));
            root.setRight(randomTree(firstValue + leftCount + 1, lastValue));
            return root;
        }
    }

    private static String nameForNumber(int n) {
        final String[] underTwenty = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve",
            "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
        final String[] decades = {"twenty", "thirty", "fourty", "fifty", "sixty", "seventy", "eighty", "ninety"};

        if (n < 20) return underTwenty[n];
        else if (n > 99) return "" + n;  // not implemented
        else return decades[n / 10 - 2] + (n % 10 == 0 ? "" : (" " + underTwenty[n % 10]));
    }
}
