package DocumentClasses;

import javax.xml.soap.Text;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class TextVector implements Serializable {

  private HashMap<String, Integer> rawVector;

  private Integer id;

  private HashMap<Integer, Integer> dots;

  private HashMap<Integer, Double> norm_dots;

  public TextVector() {
    this(-1);
  }

  public TextVector(int id) {
    rawVector = new HashMap<>();
    this.id = id;
    dots = new HashMap<>();
    norm_dots = new HashMap<>();
  }

  public Set<Map.Entry<String, Integer>> getRawVectorEntrySet() {
    return rawVector.entrySet();
  }

  public void add(String word) {
    rawVector.put(word, rawVector.getOrDefault(word, 0) + 1);
  }

  public boolean contains(String word) {
    return rawVector.containsKey(word);
  }

  public int getId() {
    return id;
  }

  public int getRawFrequency(String word) {
    return rawVector.getOrDefault(word, 0);
  }

  public int getTotalWordCount() {
    return rawVector.values().parallelStream().reduce(0, Integer::sum);
  }

  public int getDistinctWordCount() {
    return rawVector.size();
  }

  public int getHighestRawFrequency() {
    return rawVector.values().parallelStream().reduce(0, Integer::max);
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
        .parallelStream()
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

    Collections.sort(entries, (e1, e2) ->
      distanceAlg.findDistance(this, e1.getValue(), documents)
        < distanceAlg.findDistance(this, e2.getValue(), documents) ? -1 : 1
    );
    Collections.reverse(entries);
    List<Integer> sub_list = entries.parallelStream().map(e -> e.getKey()).collect(Collectors
      .toList());
    return new ArrayList<>(sub_list.subList(0, num));
  }

  public int dot(TextVector other) {
    if (!dots.containsKey(other.id))
      dots.put(other.id, rawVector
        .keySet()
        .parallelStream()
        .mapToInt(k -> getRawFrequency(k) * other.getRawFrequency(k))
        .sum()
      );
    return dots.get(other.id);
  }

  public double normalized_dot(TextVector other) {
    if (!norm_dots.containsKey(other.id))
      norm_dots.put(other.id, getNormalizedVectorEntrySet()
        .parallelStream()
        .mapToDouble(e -> e.getValue() * other.getNormalizedFrequency(e.getKey()))
        .sum()
      );
    return norm_dots.get(other.id);
  }

  @Override
  public String toString() {
    return "" + id;
  }
}
