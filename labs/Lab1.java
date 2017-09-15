package labs;

import DocumentClasses.DocumentCollection;
import DocumentClasses.TextVector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class Lab1 {
  public static void main(String[] args) {
    DocumentCollection collection = new DocumentCollection("documents.txt.1400");
    Collection<TextVector> docs = collection.getDocuments();

    String word = "";
    Integer freq = 0;
    for (TextVector doc : docs) {
      if (doc.getHighestRawFrequency() > freq) {
        word = doc.getMostFrequentWord();
        freq = doc.getHighestRawFrequency();
      }
    }
    System.out.println("Word = " + word);
    System.out.println("Frequency = " + freq);

    Integer count = docs.stream().mapToInt(v -> v.getDistinctWordCount()).sum();
    System.out.println("Distinct Number of Words = " + count);

    Integer total = docs.stream().mapToInt(v -> v.getTotalWordCount()).sum();
    System.out.println("Total word count = " + total);

    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("" +
      "./files/docvector")))) {
      os.writeObject(docs);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}