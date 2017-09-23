package labs;

import DocumentClasses.CosineDistance;
import DocumentClasses.DocumentCollection;
import DocumentClasses.TextVector;

import javax.xml.soap.Text;
import java.util.Map;

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

    documents.normalize(documents);
    queries.normalize(documents);

    CosineDistance cos = new CosineDistance();
    for (Map.Entry<Integer, TextVector> entry : queries.getEntrySet()) {
      System.out.print(entry.getKey() + ": ");
      System.out.println(entry.getValue().findClosestDocuments(documents, cos));
    }
  }
}
