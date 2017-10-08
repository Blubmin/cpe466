package GraphClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public class Graph {
  HashSet<Integer> nodes;
  HashMap<Integer, ArrayList<Integer>> adjacencyList; // Incoming edges
  HashMap<Integer, Integer> outgoingEdgeCount;

  public Graph(String filename) {
    nodes = new HashSet<>();
    adjacencyList = new HashMap<>();
    outgoingEdgeCount = new HashMap<>();

    try (Stream<String> stream = Files.lines(Paths.get(filename))) {
      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split(",");
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[2]);

        nodes.add(a);
        nodes.add(b);

        if (!adjacencyList.containsKey(b))
          adjacencyList.put(b, new ArrayList<>());
        adjacencyList.get(b).add(a);

        if (!outgoingEdgeCount.containsKey(a))
          outgoingEdgeCount.put(a, 0);
        outgoingEdgeCount.put(a, outgoingEdgeCount.get(a) + 1);
      }
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
