package DocumentClasses;

import javax.xml.soap.Text;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class TextVector implements Serializable {

  private HashMap<String, Integer> rawVector;

  public TextVector() {
    rawVector = new HashMap<>();
  }

  public Set<Map.Entry<String, Integer>> getRawVectorEntrySet() {
    return rawVector.entrySet();
  }

  public void add(String word) {
    Integer count = contains(word) ? rawVector.get(word) : 0;
    rawVector.put(word, count + 1);
  }

  public boolean contains(String word) {
    return rawVector.containsKey(word);
  }

  public int getRawFrequency(String word) {
    return rawVector.getOrDefault(word, 0);
  }

  public int getTotalWordCount() {
    return rawVector.values().stream().reduce(0, Integer::sum);
  }

  public int getDistinctWordCount() {
    return rawVector.size();
  }

  public int getHighestRawFrequency() {
    return rawVector.values().stream().reduce(0, Integer::max);
  }

  public String getMostFrequentWord() {
    int max = getHighestRawFrequency();
    for (Map.Entry<String, Integer> entry : getRawVectorEntrySet()) {
      if (entry.getValue() == max) return entry.getKey();
    }
    return null;
  }

  public abstract Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet();

  public abstract void normalize(DocumentCollection dc);

  public abstract double getNormalizedFrequency(String word);

  public double getL2Norm() {
    return Math.sqrt(
      rawVector
        .keySet()
        .stream()
        .mapToDouble(k -> Math.pow(getNormalizedFrequency(k), 2))
        .sum()
    );
  }

  public ArrayList<Integer> findClosestDocuments(DocumentCollection documents,
                                                 DocumentDistance distanceAlg) {
    return findClosestDocuments(documents, distanceAlg, 20);
  }

  public ArrayList<Integer> findClosestDocuments(DocumentCollection documents, DocumentDistance
    distanceAlg, int num) {
    List<Map.Entry<Integer, TextVector>> entries = new ArrayList<>(documents.getEntrySet());

    Collections.sort(entries, (e1, e2) -> distanceAlg.findDistance(this, e1.getValue(), documents) < distanceAlg
      .findDistance(this, e2.getValue(), documents) ? -1 : 1);

    List<Integer> sub_list = entries.stream().map(e -> e.getKey()).collect(Collectors.toList());
//    return new ArrayList<>(sub_list.subList(0, num));
    return new ArrayList<>(sub_list);
  }

  public double dot(TextVector other) {
    return rawVector
      .keySet()
      .stream()
      .mapToInt(k -> getRawFrequency(k) * other.getRawFrequency(k))
      .sum();
  }
}
