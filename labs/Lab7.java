package labs;

import DecisionTreeClasses.DecisionTree;

public class Lab7 {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: Lab7 data");
      System.exit(1);
    }

    String data_file = args[0];
    DecisionTree tree = new DecisionTree(data_file);
    tree.computeTree();
    System.out.println(tree);
  }
}
