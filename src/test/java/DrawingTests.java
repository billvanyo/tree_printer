import org.junit.jupiter.api.Test;
import tech.vanyo.treePrinter.TreeNode;
import tech.vanyo.treePrinter.TreePrinter;

import java.util.List;

public class DrawingTests {
    
    
    TreeNode tree = CompleteTree.completeLevelOrderTree(16);
    @Test
    public void ansiPrint() {
        var ansiPrinter = new TreePrinter<>(n -> ansi8bit(n.getValue()-1), TreeNode::getLeft, TreeNode::getRight);
        ansiPrinter.setSquareBranches(true).printTree(tree);
    }
    
    @Test
    public void customCharactersPrint() {
        var boxyPrinter = new TreePrinter<>(n -> "" + n.getValue(), TreeNode::getLeft, TreeNode::getRight)
            .setCharacters(segment -> switch(segment) {
                case D_PLACEHOLDER -> '╳';
                case D_LEFT -> '╱';
                case D_RIGHT -> '╲';
                case S_PLACEHOLDER, V -> '║';
                case H -> '═';
                case SPLIT -> '╩';
                case OUT_R -> '╚';
                case OUT_L -> '╝';
                case IN_L -> '╔';
                case IN_R -> '╗';
            });
        boxyPrinter.setSquareBranches(true).printTree(tree);
        boxyPrinter.setSquareBranches(false).printTree(tree);
    }

    @Test
    public void placeholderPrint() {
        var boxyPrinter = new TreePrinter<>(n -> (n.getValue() % 2 == 0 ? "" + n.getValue() : ""),
            TreeNode::getLeft,
            TreeNode::getRight);
        boxyPrinter.setSquareBranches(true).printTree(tree);
        boxyPrinter.setSquareBranches(false).printTree(tree);
    }


    @Test
    public void spacingTestTrees() {
        List<TreeNode> trees = EnumTrees.enumTrees(4);

        var printer = new TreePrinter<>(n -> "" + n.getValue(), TreeNode::getLeft, TreeNode::getRight);
        printer.setColGap(6).setRowGap(2);
        printer.setLabelGap(2);
        printer.printTrees(trees, 60);
    }

    private String ansi8bit(int number) { // maps 0-16 to 30-37, 40-47
        var value = 30 + (number  >= 8 ? 10 : 0) + number % 8;
        return "\u001b[" + value + "m" + value + "\u001b[0m";
    }

}
