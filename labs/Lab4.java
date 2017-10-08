package labs;

import GraphClasses.Graph;

public class Lab4 {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: Lab4 graphFile");
      System.exit(1);
    }

    String graphFile = args[0];
    Graph graph = new Graph(graphFile);

    graph.pageRank();
    System.out.println(graph.topPages());
  }
}
