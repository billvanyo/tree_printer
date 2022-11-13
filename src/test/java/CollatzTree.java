import org.junit.jupiter.api.Test;
import tech.vanyo.treePrinter.TreeNode;
import tech.vanyo.treePrinter.TreePrinter;

public class CollatzTree {

    // prints tree diagram for tree representation of "reverse" Collatz sequences
    @Test
    public void testCollatzSequence() {
        TreeNode root;

        root = collatzTree(1, 1, 15);
        // Collatz Conjecture: for every positive integer X, there is some N such that X appears in collatzTree(N) 

        TreePrinter<TreeNode> printer = new TreePrinter<>(n -> ""+n.getValue(), n -> n.getLeft(), n -> n.getRight());

        printer.setLabelGap(1);
        printer.setSquareBranches(true);
        printer.setLrAgnostic(true);
        printer.printTree(root);
    }


    private static TreeNode collatzTree(int start, int curLength, int maxLength) {
        TreeNode root = new TreeNode(start);
        if (curLength < maxLength) {
            // Forward Collatz sequence has that an even number N is followed by N/2, which is either an even or odd number.
            // So in reverse, either an even or odd number (any number) can be preceded by 2N.
            root.setLeft(collatzTree(start*2, curLength+1, maxLength));
            // Forward Collatz sequence has that an odd number N (i.e. a number of form 2X+1) is followed 
            // by 3N+1 (i.e. 3(2X+1)+1, or 6X+4).
            // So in reverse, a number N of the form 6X+4 can be preceded by (N-1)/3 (N-1 is divisible by 3)
            // But if N is 4, we don't want it preceded (in reverse) by 1, since in the forward direction, 
            // the sequence stops at 1 (or, in the reverse direction, 1 is where we started).
            if (start%6==4 && start>4) root.setRight(collatzTree((start-1)/3, curLength+1, maxLength));
        }
        return root;
    }
}
