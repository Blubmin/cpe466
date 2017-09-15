package labs;

import DocumentClasses.DocumentCollection;
import DocumentClasses.TextVector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class Lab1 {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("Usage: Lab01 documentFile outputFile");
      System.exit(1);
    }
    String document_file = args[0];
    String out_file = args[1];

    DocumentCollection collection = new DocumentCollection(document_file);
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

    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(out_file)))) {
      os.writeObject(docs);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}