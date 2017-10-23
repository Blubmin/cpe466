package GraphClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
  private HashSet<Integer> nodes;
  private HashMap<Integer, ArrayList<Integer>> adjacencyList; // Incoming edges
  private HashMap<Integer, Integer> outgoingEdgeCount;

  private HashMap<Integer, Double> pageRankOld = new HashMap<>();
  private HashMap<Integer, Double> pageRankNew = new HashMap<>();

  public Graph(String filename) {
    nodes = new HashSet<>();
    adjacencyList = new HashMap<>();
    outgoingEdgeCount = new HashMap<>();

    pageRankOld = new HashMap<>();
    pageRankNew = new HashMap<>();

    try (Stream<String> stream = Files.lines(Paths.get(filename))) {
      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split(",");
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[2]);

        nodes.add(a);
        nodes.add(b);

        if (!adjacencyList.containsKey(a))
          adjacencyList.put(a, new ArrayList<>());
        if (!adjacencyList.containsKey(b))
          adjacencyList.put(b, new ArrayList<>());
        adjacencyList.get(b).add(a);

        if (!outgoingEdgeCount.containsKey(a))
          outgoingEdgeCount.put(a, 0);
        outgoingEdgeCount.put(a, outgoingEdgeCount.get(a) + 1);
        if (!outgoingEdgeCount.containsKey(b))
          outgoingEdgeCount.put(b, 0);
      }
    } catch (IOException e) {
      System.err.println(e);
    }

    for (Integer node : nodes)
      pageRankNew.put(node, 1.0);
    normalize(pageRankNew);
  }

  private double getMagnitude(HashMap<Integer, Double> vector) {
    return Math.sqrt(vector.values().parallelStream().mapToDouble(d -> d * d).sum());
  }

  private void normalize(HashMap<Integer, Double> vector) {
    double sum = vector.values().parallelStream().mapToDouble(d -> d).sum();
    for (Integer node : nodes)
      vector.put(node, vector.get(node) / sum);
  }

  public int getNumPages() {
    return nodes.size();
  }

  public void pageRank() {
    double distance = Double.MAX_VALUE;
    double d = .9;
    while (distance >= .001) {
      pageRankOld = (HashMap<Integer, Double>) pageRankNew.clone();

      double firstTerm = (1 - d) / (double) getNumPages();
      pageRankNew.entrySet().parallelStream().forEach(node ->
        node.setValue(firstTerm + d * adjacencyList.get(node.getKey())
            .parallelStream()
            .mapToDouble(incoming -> pageRankOld.get(incoming) / outgoingEdgeCount.get(incoming))
            .sum()
          )
      );
      normalize(pageRankNew);
      distance = findDistance(pageRankOld, pageRankNew);
    }
  }

  public double findDistance(HashMap<Integer, Double> pageRankOld,
                             HashMap<Integer, Double> pageRankNew) {
    return pageRankOld
      .entrySet()
      .parallelStream()
      .mapToDouble(e -> Math.abs(e.getValue() - pageRankNew.get(e.getKey())))
      .sum();
  }

  public List<Integer> topPages() {
    return topNPages(20);
  }

  public List<Integer> topNPages(Integer n) {
    List<Integer> results = pageRankNew
      .entrySet()
      .parallelStream()
      .sorted((e1, e2) -> e1.getValue() < e2.getValue() ? 1 : -1)
      .map(e -> e.getKey())
      .collect(Collectors.toList());
    return results.subList(0, n);
  }
}
