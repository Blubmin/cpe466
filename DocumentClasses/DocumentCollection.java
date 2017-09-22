package DocumentClasses;

import javax.xml.soap.Text;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class DocumentCollection implements Serializable {

  private static String noiseWordArray[] = {"a", "about", "above", "all", "along", "also",
    "although", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been",
    "but", "by", "can", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't",
    "e.g.", "either", "etc", "etc.", "even", "ever", "enough", "for", "from", "further", "get",
    "gets", "got", "had", "have", "hardly", "has", "hasn't", "having", "he", "hence", "her",
    "here", "hereby", "herein", "hereof", "hereon", "hereto", "herewith", "him", "his", "how",
    "however", "i", "i.e.", "if", "in", "into", "it", "it's", "its", "me", "more", "most", "mr",
    "my", "near", "nor", "now", "no", "not", "or", "on", "of", "onto", "other", "our", "out",
    "over", "really", "said", "same", "she", "should", "shouldn't", "since", "so", "some",
    "such", "than", "that", "the", "their", "them", "then", "there", "thereby", "therefore",
    "therefrom", "therein", "thereof", "thereon", "thereto", "therewith", "these", "they",
    "this", "those", "through", "thus", "to", "too", "under", "until", "unto", "upon", "us",
    "very", "was", "wasn't", "we", "were", "what", "when", "where", "whereby", "wherein",
    "whether", "which", "while", "who", "whom", "whose", "why", "with", "without", "would",
    "you", "your", "yours", "yes"};

  private HashMap<Integer, TextVector> documents;

  public DocumentCollection(String filename, String type) {
    documents = new HashMap<>();

    try (Stream<String> stream = Files.lines(Paths.get(filename))) {
      Integer id = 0;
      TextVector textVector = null;
      boolean readText = false;

      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split(" ");
        if (args.length == 0) return;

        if (args[0].equals(".I")) {
          if (textVector != null) documents.put(id, textVector);
          id = Integer.parseInt(args[1]);
          textVector = type.equals("document") ? new DocumentVector() : new QueryVector();
          readText = false;
          continue;
        }

        if (args[0].equals(".W")) {
          readText = true;
          continue;
        }

        if (readText) {
          String[] tokens = line.split("[^a-zA-Z]+");
          for (String word : tokens) {
            String lower = word.toLowerCase();
            if (word.length() > 1 && !isNoiseWord(lower)) textVector.add(lower);
          }
        }
      }
      documents.put(id, textVector);
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  public static void serialize(DocumentCollection collection, String filename) {
    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(filename)))) {
      os.writeObject(collection);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static DocumentCollection deserialize(String filename) {
    DocumentCollection collection = null;
    try (ObjectInputStream is = new
      ObjectInputStream(new FileInputStream(new File(filename))))
    {
      collection = (DocumentCollection) is.readObject();
    } catch(Exception e){
      System.out.println(e);
    }
    return collection;
  }

  public TextVector getDocumentById(int id) {
    return documents.get(id);
  }

  public double getAverageDocumentLength() {
    return getDocuments()
      .stream().mapToInt((TextVector v) -> v.getTotalWordCount()).average().orElse(0);
  }

  public int getSize() {
    return documents.size();
  }

  public Collection<TextVector> getDocuments() {
    return new ArrayList<>(documents.values());
  }

  public Set<Map.Entry<Integer, TextVector>> getEntrySet() {
    return documents.entrySet();
  }

  public int getDocumentFrequency(String word) {
    return getDocuments().stream().mapToInt((TextVector v) -> v.contains(word) ? 1 : 0).sum();
  }

  private boolean isNoiseWord(String word) {
    return Arrays.asList(noiseWordArray).contains(word);
  }

  public int getTotalDistinctWordCount() {
    return getDocuments().stream().mapToInt(v -> v.getDistinctWordCount()).sum();
  }

  public int getTotalWordCount() {
    return getDocuments().stream().mapToInt(v -> v.getTotalWordCount()).sum();
  }

  public String getMostFrequentWord() {
    String word = "";
    Integer freq = 0;
    for (TextVector doc : getDocuments()) {
      if (doc.getHighestRawFrequency() > freq) {
        word = doc.getMostFrequentWord();
        freq = doc.getHighestRawFrequency();
      }
    }
    return word;
  }

  public int getHighestRawFrequency() {
    return getDocuments().stream().mapToInt(v -> v.getHighestRawFrequency()).max().orElse(0);
  }

  public void normalize(DocumentCollection dc) {

  }
}
