package DocumentClasses;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class DocumentCollection implements Serializable {

  private static List<String> noiseWordArray = Arrays.asList("a", "about", "above", "all",
    "along", "also", "although", "am", "an", "and", "any", "are", "aren't", "as", "at", "be",
    "because", "been", "but", "by", "can", "cannot", "could", "couldn't", "did", "didn't", "do",
    "does", "doesn't", "e.g.", "either", "etc", "etc.", "even", "ever", "enough", "for", "from",
    "further", "get", "gets", "got", "had", "have", "hardly", "has", "hasn't", "having", "he",
    "hence", "her", "here", "hereby", "herein", "hereof", "hereon", "hereto", "herewith", "him",
    "his", "how", "however", "i", "i.e.", "if", "in", "into", "it", "it's", "its", "me", "more",
    "most", "mr", "my", "near", "nor", "now", "no", "not", "or", "on", "of", "onto", "other",
    "our", "out", "over", "really", "said", "same", "she", "should", "shouldn't", "since", "so",
    "some", "such", "than", "that", "the", "their", "them", "then", "there", "thereby",
    "therefore", "therefrom", "therein", "thereof", "thereon", "thereto", "therewith", "these",
    "they", "this", "those", "through", "thus", "to", "too", "under", "until", "unto", "upon",
    "us", "very", "was", "wasn't", "we", "were", "what", "when", "where", "whereby", "wherein",
    "whether", "which", "while", "who", "whom", "whose", "why", "with", "without", "would",
    "you", "your", "yours", "yes");

  private HashMap<Integer, TextVector> documents;

  private double averageLength;
  private boolean computeAverageLength;

  private HashMap<String, Integer> docFreq;
  private HashMap<String, Double> invDocFreq;

  private int totalWordCount;
  private boolean computeTotalWordCount;
  private int distinctWordCount;
  private boolean computeDistinctWordCount;

  public DocumentCollection(String filename, String type) {
    documents = new HashMap<>();

    averageLength = 0;
    totalWordCount = 0;
    distinctWordCount = 0;
    setRecompute();

    try (Stream<String> stream = Files.lines(Paths.get(filename))) {
      Integer id = 0;
      TextVector textVector = null;
      boolean readText = false;

      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split(" ");
        if (args.length == 0) return;

        if (args[0].equals(".I")) {
          if (textVector != null) documents.put(id, textVector);
          id = type.equals("document") ? Integer.parseInt(args[1]) : id + 1;
          textVector = type.equals("document") ? new DocumentVector(id) : new QueryVector(id);
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
      System.err.println(e);
    }
  }

  public static DocumentCollection deserialize(String filename) {
    DocumentCollection collection = null;
    try (ObjectInputStream is = new
      ObjectInputStream(new FileInputStream(new File(filename)))) {
      collection = (DocumentCollection) is.readObject();
    } catch (Exception e) {
      System.err.println(e);
    }
    return collection;
  }

  private void setRecompute() {
    computeAverageLength = true;
    docFreq = new HashMap<>();
    invDocFreq = new HashMap<>();
    computeTotalWordCount = true;
    computeDistinctWordCount = true;
  }

  public TextVector getDocumentById(int id) {
    return documents.get(id);
  }

  public double getAverageDocumentLength() {
    if (computeAverageLength) {
      averageLength = getDocuments()
        .parallelStream().mapToInt((TextVector v) -> v.getTotalWordCount()).average().orElse(0);
      computeAverageLength = false;
    }
    return averageLength;
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
    if (!docFreq.containsKey(word))
      docFreq.put(word, getDocuments().parallelStream().mapToInt((TextVector v) -> v.contains
        (word) ? 1 : 0)
        .sum());
    return docFreq.get(word);
  }

  public double getInverseDocumentFrequency(String word) {
    if (!invDocFreq.containsKey(word)) {
      double freq = getDocumentFrequency(word);
      if (freq == 0) return 0;
      invDocFreq.put(word, Math.log(getSize() / freq) / Math.log(2));
    }
    return invDocFreq.get(word);
  }

  private boolean isNoiseWord(String word) {
    return noiseWordArray.contains(word);
  }

  public int getTotalDistinctWordCount() {
    if (computeDistinctWordCount) {
      distinctWordCount = getDocuments()
        .parallelStream()
        .mapToInt(v -> v.getDistinctWordCount())
        .sum();
      computeDistinctWordCount = false;
    }
    return distinctWordCount;
  }

  public int getTotalWordCount() {
    if (computeTotalWordCount) {
      totalWordCount = getDocuments().parallelStream().mapToInt(v -> v.getTotalWordCount()).sum();
      computeTotalWordCount = false;
    }
    return totalWordCount;
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
    return getDocuments().parallelStream().mapToInt(v -> v.getHighestRawFrequency()).max().orElse
      (0);
  }

  public int getHighestWordFrequency(String word) {
    return getDocuments().parallelStream().mapToInt(v -> v.getRawFrequency(word)).max().orElse(0);
  }

  public void normalize(DocumentCollection dc) {
    for (TextVector v : documents.values()) v.normalize(dc);
  }
}
