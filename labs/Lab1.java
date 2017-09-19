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

    DocumentCollection docs = new DocumentCollection(document_file);

    System.out.println("Word = " + docs.getMostFrequentWord());
    System.out.println("Frequency = " + docs.getHighestRawFrequency());
    System.out.println("Distinct Number of Words = " + docs.getTotalDistinctWordCount());
    System.out.println("Total word count = " + docs.getTotalWordCount());

    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(out_file)))) {
      os.writeObject(docs);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}