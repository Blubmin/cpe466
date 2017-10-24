package DecisionTreeClasses;

import java.util.*;

public class Matrix {

  private ArrayList<Tuple> rows;
  private HashMap<Integer, HashMap<Integer, Integer>> attributeCounts;
  private HashMap<Integer, Integer> classificationCounts;

  public Matrix() {
    rows = new ArrayList<>();
    attributeCounts = new HashMap<>();
    classificationCounts = new HashMap<>();
  }

  public void addRow(String[] attributes) {
    addRow(new Tuple(attributes));
  }

  public void addRow(Tuple tup) {
    for (int i = 0; i < tup.getSize(); i++) {
      if (!attributeCounts.containsKey(i))
        attributeCounts.put(i, new HashMap<>());
      int attribute_value = tup.getValue(i);
      HashMap<Integer, Integer> counts = attributeCounts.get(i);
      if (!counts.containsKey(attribute_value))
        counts.put(attribute_value, 0);
      counts.put(attribute_value, counts.get(attribute_value) + 1);
    }
    if (!classificationCounts.containsKey(tup.getClassification()))
      classificationCounts.put(tup.getClassification(), 0);
    classificationCounts.put(tup.getClassification(), classificationCounts.get(tup
      .getClassification()) + 1);
    rows.add(tup);
  }

  public int numClassifications() {
    return classificationCounts.size();
  }

  public int numAttributes() {
    return rows.get(0).getSize();
  }

  public int numRows() {
    return rows.size();
  }

  public int getTopClassification() {
    return classificationCounts.entrySet()
      .parallelStream()
      .max(Comparator.comparingInt(Map.Entry::getValue))
      .get()
      .getKey();
  }

  public double getEntropy() {
    return classificationCounts.entrySet().parallelStream().mapToDouble(e -> {
      double fraction = e.getValue() / (double) numRows();
      return -fraction * log2(fraction);
    })
    .sum();
  }

  public double getEntropy(int attribute) {
    return attributeCounts.get(attribute)
      .entrySet()
      .parallelStream()
      .mapToDouble(e -> {
        double fraction = e.getValue() / (double) numRows();
        return fraction * getRows(attribute, e.getKey()).getEntropy();
      })
      .sum();
  }

  public Set<Integer> getValues(int attribute) {
    return attributeCounts.get(attribute).keySet();
  }

  public double getGain(int attribute) {
    return getEntropy() - getEntropy(attribute);
  }

  public double getGainRatio(int attribute) {
    double denom = -attributeCounts.get(attribute)
      .entrySet()
      .parallelStream()
      .mapToDouble(e -> {
        double fraction = e.getValue() / (double) numRows();
        return fraction * log2(fraction);
      })
      .sum();
    return Math.abs(denom) < 10e-6 ? 0 : getGain(attribute) / denom;
  }

  private double log2(double val) {
    return Math.log(val) / Math.log(2);
  }

  public Matrix getRows(int attribute, int value) {
    Matrix mat = new Matrix();
    rows.stream().forEach(tup -> {
      if (tup.getValue(attribute) == value) mat.addRow(tup);
    });
    return mat;
  }
}
