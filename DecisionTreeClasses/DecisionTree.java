package DecisionTreeClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DecisionTree {

  Matrix mat;

  public DecisionTree(String data_file) {
    mat= new Matrix();

    try (Stream<String> stream = Files.lines(Paths.get(data_file))) {
      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split("(\\.\\d)*,");
        mat.addRow(args);
      }
    } catch (IOException exc) {
      System.err.println(exc);
      System.exit(1);
    }
  }

  public void computeTree() {
    System.out.println("Total entropy: " + mat.getEntropy());
    for (int i = 0; i < mat.numAttributes(); i++) {
      System.out.println("Entropy of " + i + ": " + mat.getEntropy(i));
    }
  }
}
