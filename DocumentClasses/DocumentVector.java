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
    return null;
  }

  @Override
  public void normalize(DocumentCollection dc) {

  }

  @Override
  public double getNormalizedFrequency(String word) {
    return 0;
  }
}
