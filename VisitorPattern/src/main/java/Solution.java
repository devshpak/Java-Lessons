import com.sun.javafx.geom.Edge;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

import java.util.ArrayList;
import java.util.Scanner;

enum Color {
    RED, GREEN
}

abstract class Tree {

    private int value;
    private Color color;
    private int depth;

    public Tree(int value, Color color, int depth) {
        this.value = value;
        this.color = color;
        this.depth = depth;
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public int getDepth() {
        return depth;
    }

    public abstract void accept(TreeVis visitor);
}

class TreeNode extends Tree {

    private ArrayList<Tree> children = new ArrayList<>();

    public TreeNode(int value, Color color, int depth) {
        super(value, color, depth);
    }

    public void accept(TreeVis visitor) {
        visitor.visitNode(this);

        for (Tree child : children) {
            child.accept(visitor);
        }
    }

    public void addChild(Tree child) {
        children.add(child);
    }
}

class TreeLeaf extends Tree {

    public TreeLeaf(int value, Color color, int depth) {
        super(value, color, depth);
    }

    public void accept(TreeVis visitor) {
        visitor.visitLeaf(this);
    }
}

abstract class TreeVis
{
    public abstract int getResult();
    public abstract void visitNode(TreeNode node);
    public abstract void visitLeaf(TreeLeaf leaf);

}

class SumInLeavesVisitor extends TreeVis {
    private int result = 0;

    public int getResult() {
        //implement this
        return result;
    }

    public void visitNode(TreeNode node) {
        //implement this
    }

    public void visitLeaf(TreeLeaf leaf) {
        result += leaf.getValue();
    }
}

class ProductOfRedNodesVisitor extends TreeVis {
    private static final int MODULO = 1_000_000_000 + 7;
    private long result = 1;

    public int getResult() {
        //implement this
        return (int) result;
    }

    private void computeResult(Tree node) {
        if (node.getColor() == Color.RED)
            result = (result * node.getValue()) % MODULO;
    }
    public void visitNode(TreeNode node) {
        computeResult(node);
    }

    public void visitLeaf(TreeLeaf leaf) {
        computeResult(leaf);
        //implement this
    }
}

class FancyVisitor extends TreeVis {
    private int sumOfEvenNodes = 0;
    private int sumOfGreenLeaves = 0;
    public int getResult() {
        //implement this
        return Math.abs(sumOfEvenNodes - sumOfGreenLeaves);
    }

    public void visitNode(TreeNode node) {
        if (node.getDepth() % 2 == 0)
            sumOfEvenNodes += node.getValue();
    }

    public void visitLeaf(TreeLeaf leaf) {
        if (leaf.getColor() == Color.GREEN)
            sumOfGreenLeaves += leaf.getValue();
    }
}

public class Solution {
    private static short[] X;
    private static Color[] C;
    private static List<Integer>[] Edges;

    private static Tree getChilds(int nodeIndex, int depth, int parentIndex)
    {

        List<Integer> childs = Edges[nodeIndex];
        int index;
        while ((index = childs.indexOf(new Integer(parentIndex))) >= 0) {
            childs.remove(index);
        }
        if (childs.isEmpty()) {
            return new TreeLeaf(X[nodeIndex], C[nodeIndex], depth);
        }
        else {
            TreeNode node = new TreeNode(X[nodeIndex], C[nodeIndex], depth);
            for (Integer child : childs) {
                node.addChild(getChilds(child, depth + 1, nodeIndex));
            }
            return node;
        }
    }
    public static Tree solve() {
        //read the tree from STDIN and return its root as a return value of this function
        InputStream input = Solution.class.getResourceAsStream("doc.txt");
        Scanner sc = new Scanner(input);
//        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        X = new short[N];
        C = new Color[N];
        Edges = new List[N];
        for (int i = 0; i < N; ++i)
            Edges[i] = new ArrayList<Integer>();
        for (int i = 0; i < N; ++i)
            X[i] = sc.nextShort();
        for (int i = 0; i < N; ++i)
            C[i] = Color.values()[sc.nextInt()];
        /*
         (1) --> {2, 3}
         (2) --> {1}
         (3) --> {1, 4, 5}
        */
        for (int i = 0; i < N - 1; ++i) {
            int v1 = sc.nextInt() - 1;
            int v2 = sc.nextInt() - 1;
            Edges[v1].add(Integer.valueOf(v2));
            Edges[v2].add(Integer.valueOf(v1));
        }
        return getChilds(0,0, 0);
    }

    public static void main(String[] args) {

        Tree root = solve();
        SumInLeavesVisitor vis1 = new SumInLeavesVisitor();
        ProductOfRedNodesVisitor vis2 = new ProductOfRedNodesVisitor();
        FancyVisitor vis3 = new FancyVisitor();

        root.accept(vis1);
        root.accept(vis2);
        root.accept(vis3);

        int res1 = vis1.getResult();
        int res2 = vis2.getResult();
        int res3 = vis3.getResult();

        System.out.println(res1);
        System.out.println(res2);
        System.out.println(res3);
    }
}