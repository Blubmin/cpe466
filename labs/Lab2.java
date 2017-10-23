package labs;

import DocumentClasses.*;

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
    if (args.length != 4) {
      System.err.println("Usage: Lab02 documentCollectionFile queryFile distanceAlg outputFile");
      System.exit(1);
    }

    String doc_file = args[0];
    documents = DocumentCollection.deserialize(doc_file);

    String query_file = args[1];
    queries = DocumentCollection.deserialize(query_file);

    String distanceAlgType = args[2];
    DocumentDistance distanceAlg = distanceAlgType.equals("okapi") ? new OkapiDistance() : new
      CosineDistance();

    if (distanceAlgType.equals("cosine")) {
      documents.normalize(documents);
      queries.normalize(documents);
    }

    HashMap<Integer, ArrayList<Integer>> results = new HashMap<>();
    for (Map.Entry<Integer, TextVector> query : queries.getEntrySet()) {
      results.put(query.getKey(), query.getValue().findClosestDocuments(documents, distanceAlg));
      System.out.println("" + query.getKey() + ": " + results.get(query.getKey()));
    }

    String out_file = args[3];
    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(out_file)))) {
      os.writeObject(results);
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
