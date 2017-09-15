package labs;

import DocumentClasses.DocumentCollection;
import DocumentClasses.TextVector;

import java.util.Collection;

public class Lab1 {
  public static void main(String[] args) {
    DocumentCollection collection = new DocumentCollection("documents" +
      ".txt.1400");
    Collection<TextVector> docs = collection.getDocuments();

    Integer freq = docs.stream().mapToInt(v -> v.getHighestRawFrequency())
      .max().orElse(0);
    System.out.println("Frequency = " + freq);

    Integer count = docs.stream().mapToInt(v -> v.getDistinctWordCount()).sum();
    System.out.println("Distinct Number of Words = " + count);

    Integer total = docs.stream().mapToInt(v -> v.getTotalWordCount()).sum();
    System.out.println("Total word count = " + total);
  }
}