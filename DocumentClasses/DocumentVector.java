package DocumentClasses;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DocumentVector extends TextVector {
  private HashMap<String, Double> normalizedVector;

  public DocumentVector() {
    normalizedVector = new HashMap<>();
  }

  @Override
  public Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet() {
    return normalizedVector.entrySet();
  }

  @Override
  public void normalize(DocumentCollection dc) {
    for (Map.Entry<String, Integer> e : getRawVectorEntrySet()) {
      String word = e.getKey();
      Double weight = (e.getValue() / (double) getHighestRawFrequency())
        * dc.getInverseDocumentFrequency(word);
      normalizedVector.put(word, weight);
    }
  }

  @Override
  public double getNormalizedFrequency(String word) {
    return normalizedVector.getOrDefault(word, 0.0);
  }
}
