package DecisionTreeClasses;

import java.util.ArrayList;

public class Tuple {
  private ArrayList<Integer> values;
  private Integer classification;

  public Tuple(String[] attributes) {
    values = new ArrayList<>(attributes.length - 1);
    for (int i = 0; i < attributes.length - 1; i++) {
      values.add(i, Integer.parseInt(attributes[i]));
    }
    classification = Integer.parseInt(attributes[attributes.length - 1]);
  }

  public int getValue(int attribute) {
    return values.get(attribute);
  }

  public int getClassification() {
    return classification;
  }

  public int getSize() {
    return values.size();
  }
}