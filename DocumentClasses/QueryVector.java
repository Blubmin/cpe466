package DocumentClasses;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryVector extends TextVector {
  private HashMap<String, Double> normalizedVector;

  public QueryVector() {
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
      Double idf = dc.getInverseDocumentFrequency(word);
      Double weight = 0.5;
      if (idf != 0)
        weight += 0.5 * e.getValue() / (double) getHighestRawFrequency() * idf;
      normalizedVector.put(word, weight);
    }
  }

  @Override
  public double getNormalizedFrequency(String word) {
    return normalizedVector.get(word);
  }
}
