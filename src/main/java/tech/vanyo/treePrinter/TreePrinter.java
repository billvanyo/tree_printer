package tech.vanyo.treePrinter;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class TreePrinter<T> {

    public static final Pattern ANSI_REGEX = Pattern.compile("\\e\\[[\\d;]*[^\\d;]");

    /** Segments for drawing a tree. See documentation of their values for more info. */
    public enum Segment {
        /**Diagonal placeholder*/ D_PLACEHOLDER,
        /**Diagonal left*/ D_LEFT,
        /**Diagonal right*/ D_RIGHT,

        /**Placeholder in square style*/ S_PLACEHOLDER,
        /**Vertical line*/ V,
        /**Horizontal line*/ H,
        /**Split to left and right children in square style */ SPLIT,
        /**Outgoing to right child*/ OUT_R,
        /**Outgoing to left child*/ OUT_L,
        /**Incoming into left child*/ IN_L,
        /**Incoming into right child*/ IN_R,
    }

    private Function<T, String> getLabel;
    private Function<T, T> getLeft;
    private Function<T, T> getRight;
    private Function<Segment, Character> segmentMapper = segment -> switch (segment) {
        case D_PLACEHOLDER -> '╳'; // this is unicode diagonal crossing!
        case D_LEFT -> '╱'; // this is unicode diagonal!
        case D_RIGHT -> '╲'; // this is unicode diagonal!
        case S_PLACEHOLDER, V -> '│';
        case H -> '─';
        case SPLIT -> '┴';
        case OUT_R -> '└';
        case OUT_L -> '┘';
        case IN_L -> '┌';
        case IN_R -> '┐';
    };

    private PrintStream outStream = System.out;

    private boolean squareBranches = false;
    private boolean lrAgnostic = false;
    private int labelGap = 2;
    private int colGap = 1;
    private int rowGap = 1;
    private boolean usePlaceholder = true;
    private boolean flush = true;

    /** Create new tree printer with suppliers for the label as well as left and right children. */
    public TreePrinter(Function<T, String> getLabel, Function<T, T> getLeft, Function<T, T> getRight) {
        this.getLabel = getLabel;
        this.getLeft = getLeft;
        this.getRight = getRight;
    }

    /** Set stream to which the tree will be printed. Default is {@link System#out}. */
    public TreePrinter<T> setPrintStream(PrintStream outStream) { this.outStream = outStream; return this; }
    /** Set square branches style for the tree. It is more compressed than the diagonal view. */
    public TreePrinter<T> setSquareBranches(boolean squareBranches) { this.squareBranches = squareBranches; return this; }
    /** Single children will be printed directly below instead of on the left/right. Works only with square branches. */
    public TreePrinter<T> setLrAgnostic(boolean lrAgnostic) { this.lrAgnostic = lrAgnostic; return this; }
    /** Set gap between tree labels on the same level. */
    public TreePrinter<T> setLabelGap(int labelSpace) { this.labelGap = labelSpace; return this; }
    /** Set gap in single row between trees when printing with {@link #printTrees(List, int)} */
    public TreePrinter<T> setColGap(int tColGap) { this.colGap = tColGap; return this; }
    /** Set gap between rows when printing rows with {@link #printTrees(List, int)} */
    public TreePrinter<T> setRowGap(int tRowGap) { this.rowGap = tRowGap; return this; }
    /** Configure if placeholder should be used for empty tree labels */
    public TreePrinter<T> setUsePlaceholder(boolean usePlaceholder) { this.usePlaceholder = usePlaceholder; return this; }
    /** Configures if output stream will be flushed after printing */
    public TreePrinter<T> setFlush(boolean flush) { this.flush = flush; return this; }
    /** Provide a custom set of characters for the tree to be drawn. See {@link Segment}*/
    public TreePrinter<T> setCharacters(Function<Segment, Character> segmentMapper) { this.segmentMapper = segmentMapper; return this; }

    /**
        Prints ascii representation of binary tree.<br>
        Parameter <code>labelGap</code> is minimum number of spaces between adjacent node labels.<br>
        Parameter <code>squareBranches</code>, when set to true, results in branches being printed with ASCII box
        drawing characters.
     */
    public void printTree(T root) {
        List<TreeLine> treeLines = buildTreeLines(root);
        printTreeLines(treeLines);
        if(flush) outStream.flush();
    }

    /**
        Prints ascii representations of multiple trees across page.
        @param lineWidth is maximum width of output
        @see #setColGap(int)
     */
    public void printTrees(List<T> trees, int lineWidth) {
        List<List<TreeLine>> allTreeLines = new ArrayList<>();
        int[] treeWidths = new int[trees.size()];
        int[] minLeftOffsets = new int[trees.size()];
        int[] maxRightOffsets = new int[trees.size()];
        for (int i = 0; i < trees.size(); i++) {
            T treeNode = trees.get(i);
            List<TreeLine> treeLines = buildTreeLines(treeNode);
            allTreeLines.add(treeLines);
            minLeftOffsets[i] = minLeftOffset(treeLines);
            maxRightOffsets[i] = maxRightOffset(treeLines);
            treeWidths[i] = maxRightOffsets[i] - minLeftOffsets[i] + 1;
        }

        int nextTreeIndex = 0;
        while (nextTreeIndex < trees.size()) {
            // print a row of trees starting at nextTreeIndex

            // first figure range of trees we can print for next row
            int sumOfWidths = treeWidths[nextTreeIndex];
            int endTreeIndex = nextTreeIndex + 1;
            while (endTreeIndex < trees.size() && sumOfWidths + colGap + treeWidths[endTreeIndex] < lineWidth) {
                sumOfWidths += (colGap + treeWidths[endTreeIndex]);
                endTreeIndex++;
            }
            endTreeIndex--;

            // find max number of lines for tallest tree
            int maxLines = allTreeLines.stream().mapToInt(List::size).max().orElse(0);

            // print trees line by line
            for (int i = 0; i < maxLines; i++) {
                for (int j = nextTreeIndex; j <= endTreeIndex; j++) {
                    List<TreeLine> treeLines = allTreeLines.get(j);
                    if (i >= treeLines.size()) {
                        System.out.print(spaces(treeWidths[j]));
                    } else {
                        int leftSpaces = -(minLeftOffsets[j] - treeLines.get(i).leftOffset);
                        int rightSpaces = maxRightOffsets[j] - treeLines.get(i).rightOffset;
                        System.out.print(spaces(leftSpaces) + treeLines.get(i).line + spaces(rightSpaces));
                    }
                    if (j < endTreeIndex) System.out.print(spaces(colGap));
                }
                System.out.println();
            }

            for (int i = 0; i < rowGap; i++) {
                System.out.println();
            }

            nextTreeIndex = endTreeIndex + 1;
        }
    }

    private void printTreeLines(List<TreeLine> treeLines) {
        if (treeLines.size() > 0) {
            int minLeftOffset = minLeftOffset(treeLines);
            int maxRightOffset = maxRightOffset(treeLines);
            for (TreeLine treeLine : treeLines) {
                int leftSpaces = -(minLeftOffset - treeLine.leftOffset);
                int rightSpaces = maxRightOffset - treeLine.rightOffset;
                outStream.println(spaces(leftSpaces) + treeLine.line + spaces(rightSpaces));
            }
        }
    }

    private List<TreeLine> buildTreeLines(T root) {
        if (root == null) return Collections.emptyList();
        else {
            String rootLabel = getLabel.apply(root);
            List<TreeLine> leftTreeLines = buildTreeLines(getLeft.apply(root));
            List<TreeLine> rightTreeLines = buildTreeLines(getRight.apply(root));

            int leftCount = leftTreeLines.size();
            int rightCount = rightTreeLines.size();
            int minCount = Math.min(leftCount, rightCount);
            int maxCount = Math.max(leftCount, rightCount);

            // The left and right subtree print representations have jagged edges, and we essentially we have to
            // figure out how close together we can bring the left and right roots so that the edges just meet on
            // some line.  Then we add hspace, and round up to next odd number.
            int maxRootSpacing = 0;
            for (int i = 0; i < minCount; i++) {
                int spacing = leftTreeLines.get(i).rightOffset - rightTreeLines.get(i).leftOffset;
                if (spacing > maxRootSpacing) maxRootSpacing = spacing;
            }
            int rootSpacing = maxRootSpacing + labelGap;
            if (rootSpacing % 2 == 0) rootSpacing++;
            // rootSpacing is now the number of spaces between the roots of the two subtrees

            List<TreeLine> allTreeLines = new ArrayList<>();

            // strip ANSI escape codes to get length of rendered string. Fixes wrong padding when labels use ANSI escapes for colored nodes.
            String renderedRootLabel = ANSI_REGEX.matcher(rootLabel).replaceAll("");
            if(renderedRootLabel.isBlank() && usePlaceholder) rootLabel = renderedRootLabel =
                squareBranches ? draw(Segment.V) : draw(Segment.D_PLACEHOLDER);

            // add the root and the two branches leading to the subtrees

            allTreeLines.add(new TreeLine(rootLabel, -(renderedRootLabel.length() - 1) / 2, renderedRootLabel.length() / 2));

            // also calculate offset adjustments for left and right subtrees
            int leftTreeAdjust = 0;
            int rightTreeAdjust = 0;

            if (leftTreeLines.isEmpty()) {
                if (!rightTreeLines.isEmpty()) {
                    // there's a right subtree only
                    if (squareBranches) {
                        if (lrAgnostic) {
                            allTreeLines.add(new TreeLine(draw(Segment.V), 0, 0));
                        } else {
                            allTreeLines.add(new TreeLine(draw(Segment.OUT_R) + draw(Segment.IN_R),0, 1));
                            rightTreeAdjust = 1;
                        }
                    } else {
                        allTreeLines.add(new TreeLine(draw(Segment.D_RIGHT), 1, 1));
                        rightTreeAdjust = 2;
                    }
                }
            } else {
                if (rightTreeLines.isEmpty()) {
                    // there's a left subtree only
                    if (squareBranches) {
                        if (lrAgnostic) {
                            allTreeLines.add(new TreeLine(draw(Segment.V), 0, 0));
                        } else {
                            allTreeLines.add(new TreeLine(draw(Segment.IN_L) + draw(Segment.OUT_L), -1, 0));
                            leftTreeAdjust = -1;
                        }
                    } else {
                        allTreeLines.add(new TreeLine(draw(Segment.D_LEFT), -1, -1));
                        leftTreeAdjust = -2;
                    }
                } else {
                    // there's a left and right subtree
                    if (squareBranches) {
                        int adjust = (rootSpacing / 2) + 1;
                        String horizontal = String.join("", Collections.nCopies(rootSpacing / 2,
                            draw(Segment.H)));
                        String branch =
                            draw(Segment.IN_L) + horizontal + draw(Segment.SPLIT) + horizontal + draw(Segment.IN_R);
                        allTreeLines.add(new TreeLine(branch, -adjust, adjust));
                        rightTreeAdjust = adjust;
                        leftTreeAdjust = -adjust;
                    } else {
                        if (rootSpacing == 1) {
                            allTreeLines.add(new TreeLine(draw(Segment.D_LEFT) + " " + draw(Segment.D_RIGHT), -1,
                                1));
                            rightTreeAdjust = 2;
                            leftTreeAdjust = -2;
                        } else {
                            for (int i = 1; i < rootSpacing; i += 2) {
                                String branches = draw(Segment.D_LEFT) + spaces(i) + draw(Segment.D_RIGHT);
                                allTreeLines.add(new TreeLine(branches, -((i + 1) / 2), (i + 1) / 2));
                            }
                            rightTreeAdjust = (rootSpacing / 2) + 1;
                            leftTreeAdjust = -((rootSpacing / 2) + 1);
                        }
                    }
                }
            }

            // now add joined lines of subtrees, with appropriate number of separating spaces, and adjusting offsets

            for (int i = 0; i < maxCount; i++) {
                TreeLine leftLine, rightLine;
                if (i >= leftTreeLines.size()) {
                    // nothing remaining on left subtree
                    rightLine = rightTreeLines.get(i);
                    rightLine.leftOffset += rightTreeAdjust;
                    rightLine.rightOffset += rightTreeAdjust;
                    allTreeLines.add(rightLine);
                } else if (i >= rightTreeLines.size()) {
                    // nothing remaining on right subtree
                    leftLine = leftTreeLines.get(i);
                    leftLine.leftOffset += leftTreeAdjust;
                    leftLine.rightOffset += leftTreeAdjust;
                    allTreeLines.add(leftLine);
                } else {
                    leftLine = leftTreeLines.get(i);
                    rightLine = rightTreeLines.get(i);
                    int adjustedRootSpacing = (rootSpacing == 1 ? (squareBranches ? 1 : 3) : rootSpacing);
                    TreeLine combined = new TreeLine(leftLine.line + spaces(adjustedRootSpacing - leftLine.rightOffset + rightLine.leftOffset) + rightLine.line,
                            leftLine.leftOffset + leftTreeAdjust, rightLine.rightOffset + rightTreeAdjust);
                    allTreeLines.add(combined);
                }
            }
            return allTreeLines;
        }
    }

    private static int minLeftOffset(List<TreeLine> treeLines) {
        return treeLines.stream().mapToInt(l -> l.leftOffset).min().orElse(0);
    }

    private static int maxRightOffset(List<TreeLine> treeLines) {
        return treeLines.stream().mapToInt(l -> l.rightOffset).max().orElse(0);
    }

    private static String spaces(int n) {
        return String.join("", Collections.nCopies(n, " "));
    }

    private String draw(Segment segment) {
        return String.valueOf(segmentMapper.apply(segment));
    }

    private static class TreeLine {
        String line;
        int leftOffset;
        int rightOffset;

        TreeLine(String line, int leftOffset, int rightOffset) {
            this.line = line;
            this.leftOffset = leftOffset;
            this.rightOffset = rightOffset;
        }
    }
}
