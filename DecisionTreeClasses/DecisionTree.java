package DecisionTreeClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

public class DecisionTree {

  Matrix mat;
  Node tree;

  public DecisionTree(String data_file) {
    mat= new Matrix();

    try (Stream<String> stream = Files.lines(Paths.get(data_file))) {
      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split("(\\.\\d)*,");
        mat.addRow(args);
      }
    } catch (IOException exc) {
      System.err.println(exc);
      System.exit(1);
    }
  }

  public void computeTree() {
    Map.Entry<Integer, Double> top = pickAttribute(mat);
    tree = new Node(top.getKey(), 0);
    computeTree(tree, mat, top.getValue());
  }

  private void computeTree(Node tree, Matrix mat, double prevIGR) {
    int attribute = tree.getAttribute();
    for(Integer value : mat.getValues(attribute)) {
      Matrix matrix = mat.getRows(attribute, value);
      if (matrix.numClassifications() == 1) {
        tree.addChild(value, new LeafNode(matrix.getTopClassification(), tree.getLevel() + 1));
        continue;
      }
      Map.Entry<Integer, Double> top = pickAttribute(matrix);
      double delta_igr = prevIGR - top.getValue();
      if (delta_igr < .01) {
        tree.addChild(value, new LeafNode(matrix.getTopClassification(), tree.getLevel() + 1));
        continue;
      }
      Node node = new Node(top.getKey(), tree.getLevel() + 1);
      tree.addChild(value, node);
      computeTree(node, matrix, top.getValue());
    }
  }

  private Map.Entry<Integer, Double> pickAttribute(Matrix matrix) {
    HashMap<Integer, Double> gainRatios = new HashMap<>();
    for (int i = 0; i < matrix.numAttributes(); i++) {
      gainRatios.put(i, matrix.getGainRatio(i));
    }
    return gainRatios.entrySet()
      .parallelStream()
      .max(Comparator.comparingDouble(Map.Entry::getValue))
      .get();
  }

  public String toString() {
    return tree.toString();
  }

  private class Node {

    protected int attribute;
    protected int level;
    private HashMap<Integer, Node> children;

    public Node(int attribute, int level) {
      this.attribute = attribute;
      this.level = level;
      children = new HashMap<>();
    }

    public void addChild(int value, Node child) {
      children.put(value, child);
    }

    public HashSet<Integer> getClassifications() {
      HashSet<Integer> classifications = new HashSet<>();
      for (Node node : children.values()) {
        if (node instanceof LeafNode) {
          classifications.add(node.getAttribute());
        }
        else {
          classifications.addAll(node.getClassifications());
        }
      }
      return classifications;
    }

    public int numClassifications() {
      return getClassifications().size();
    }

    public int getAttribute() { return attribute; }

    public int getLevel() { return level; }

    public String toString() {
      String res = "";
      if (numClassifications() == 1) {
        int classification = (int) getClassifications().toArray()[0];
        return new LeafNode(classification, level).toString();
      }
      for (Map.Entry<Integer, Node> entry : children.entrySet()) {
        for (int i = 0; i < level; i++) res += "|\t";
        res += "attribute " + (attribute + 1) + " ";
        res += "value " + entry.getKey() + ":\n";
        res += entry.getValue().toString();
      }
      return res;
    }
  }

  private class LeafNode extends Node {

    public LeafNode(int value, int level) {
      super(value, level);
    }

    @Override
    public String toString() {
      String res = "";
      for (int i = 0; i < level; i++) res += "|\t";
      res += "pick " + attribute;
      return res + "\n";
    }
  }
}
