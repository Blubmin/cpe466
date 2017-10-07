package labs;

import DocumentClasses.CosineDistance;
import DocumentClasses.DocumentCollection;
import DocumentClasses.DocumentDistance;
import DocumentClasses.TextVector;

import javax.xml.soap.Text;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lab2 {
  public static DocumentCollection documents;
  public static DocumentCollection queries;

  public static void main(String[] args) {
    if (args.length != 3) {
      System.err.println("Usage: Lab02 documentCollectionFile queryFile outputFile");
      System.exit(1);
    }

    String doc_file = args[0];
    documents = DocumentCollection.deserialize(doc_file);

    String query_file = args[1];
    queries = DocumentCollection.deserialize(query_file);

    documents.normalize(documents);
    queries.normalize(documents);

    CosineDistance cos = new CosineDistance();
    HashMap<Integer, ArrayList<Integer>> results = new HashMap<>();
    int counter = 1;
    for (Map.Entry<Integer, TextVector> entry : queries.getEntrySet()) {
      results.put(counter, entry.getValue().findClosestDocuments(documents, cos));
      System.out.println("" + counter + ": " + results.get(counter));
      counter++;
    }

    String out_file = args[2];
    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(out_file)))) {
      os.writeObject(results);
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
