package labs;

import DocumentClasses.DocumentCollection;
import DocumentClasses.OkapiDistance;
import DocumentClasses.TextVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lab3 {
  public static void main(String args[]) {
    if (args.length < 3) {
      System.err.println("Usage: Lab3 cosineTfIdf okapiTfIdf humanJudgement");
      System.exit(1);
    }

    String cosineFile = args[0];
    HashMap<Integer, ArrayList<Integer>> cosineResults = deserialize(cosineFile);
    List<Map.Entry<Integer, ArrayList<Integer>>> topCosineResults = getTopNResults(cosineResults, 20);

    String okapiFile = args[1];
    HashMap<Integer, ArrayList<Integer>> okapiResults = deserialize(okapiFile);
    List<Map.Entry<Integer, ArrayList<Integer>>> topOkapiResults = getTopNResults(okapiResults, 20);

    HashMap<Integer, HashSet<Integer>> judgement = readJudgement(args[2]);
    System.out.println("Cosine MAP: = " + averageMeanAveragePrecision(topCosineResults, judgement));
    System.out.println("Okapi MAP: = " + averageMeanAveragePrecision(topOkapiResults, judgement));
  }

  private static double averageMeanAveragePrecision(List<Map.Entry<Integer, ArrayList<Integer>>> results,
                                             HashMap<Integer, HashSet<Integer>> judgements) {
    return results.parallelStream().mapToDouble(e -> meanAveragePrecision(e, judgements.get(e
      .getKey()))).average().orElse(0);
  }

  private static double meanAveragePrecision(Map.Entry<Integer, ArrayList<Integer>> result,
                                      HashSet<Integer> judgement) {
    double sum = 0;
    ArrayList<Integer> list = result.getValue();
    int count = 0;
    for (int i = 0; i < list.size(); i++) {
      if (judgement.contains(list.get(i))) {
        count++;
        sum += count / (double) (i + 1);
      }
    }
    return sum / (double) judgement.size();
  }

  private static HashMap<Integer, ArrayList<Integer>> deserialize(String tfIdfFile) {
    HashMap<Integer, ArrayList<Integer>> results = null;
    try (ObjectInputStream is = new
      ObjectInputStream(new FileInputStream(new File(tfIdfFile)))) {
      results = (HashMap<Integer, ArrayList<Integer>>) is.readObject();
    } catch (Exception e) {
      System.err.println(e);
    }
    return results;
  }

  private static HashMap<Integer, HashSet<Integer>> readJudgement(String judgementFile) {
    HashMap<Integer, HashSet<Integer>> results = new HashMap<>();

    try (Stream<String> stream = Files.lines(Paths.get(judgementFile))) {
      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split(" ");
        Integer queryId = Integer.parseInt(args[0]);
        if (!results.containsKey(queryId))
          results.put(queryId, new HashSet<>());

        Integer document = Integer.parseInt(args[1]);
        Integer relevance = Integer.parseInt(args[2]);
        List<Integer> relevantValues = Arrays.asList(1, 2, 3);
        if (relevantValues.contains(relevance))
          results.get(queryId).add(document);
      }
    } catch (IOException e) {
      System.err.println(e);
      System.exit(1);
    }

    return results;
  }

  private static List<Map.Entry<Integer, ArrayList<Integer>>> getTopResults(HashMap<Integer,
    ArrayList<Integer>> tfIdf) {
    return getTopNResults(tfIdf, 20);
  }

  private static List<Map.Entry<Integer, ArrayList<Integer>>> getTopNResults(HashMap<Integer,
    ArrayList<Integer>> tfIdf, Integer n) {
    List<Map.Entry<Integer, ArrayList<Integer>>> list = tfIdf
      .entrySet()
      .parallelStream()
      .collect(Collectors.toList());
    Collections.sort(list, Comparator.comparingInt(Map.Entry::getKey));
    return list.subList(0, n);
  }
}
