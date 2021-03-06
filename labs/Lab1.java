package labs;

import DocumentClasses.DocumentCollection;
import DocumentClasses.TextVector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class Lab1 {
  public static void main(String[] args) {
    if (args.length < 3) {
      System.err.println("Usage: Lab01 documentFile outputFile type");
      System.exit(1);
    }
    String document_file = args[0];
    String out_file = args[1];
    String type = args[2];

    DocumentCollection docs = new DocumentCollection(document_file, type);

    System.out.println("Word = " + docs.getMostFrequentWord());
    System.out.println("Frequency = " + docs.getHighestRawFrequency());
    System.out.println("Distinct Number of Words = " + docs.getTotalDistinctWordCount());
    System.out.println("Total word count = " + docs.getTotalWordCount());

    DocumentCollection.serialize(docs, out_file);
  }
}