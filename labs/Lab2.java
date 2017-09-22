package labs;

import DocumentClasses.DocumentCollection;

public class Lab2 {
  public static DocumentCollection documents;
  public static DocumentCollection queries;

  public static void main(String[] args) {
    if(args.length != 2) {
      System.err.println("Usage: Lab02 documentCollectionFile queryFile");
      System.exit(1);
    }

    String doc_file = args[0];
    documents = DocumentCollection.deserialize(doc_file);

    String query_file = args[1];
    queries = new DocumentCollection(query_file, "query");
  }
}
