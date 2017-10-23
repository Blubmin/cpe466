package DocumentClasses;

public class OkapiDistance implements DocumentDistance {
  @Override
  public double findDistance(TextVector query, TextVector document, DocumentCollection documents) {
    int n = documents.getSize();
    double k1 = 1.2;
    double b = .75;
    double k2 = 100;
    double avdl = documents.getAverageDocumentLength();

    return query.getRawVectorEntrySet().parallelStream().mapToDouble(q -> {
      int df = documents.getDocumentFrequency(q.getKey());
      if (df == 0) return 0;
      int fij = document.getRawFrequency(q.getKey());
      int fiq = query.getRawFrequency(q.getKey());
      int dl = document.getTotalWordCount();

      double first = Math.log((n - df + 0.5) / (df + 0.5));
      double second = ((k1 + 1) * fij) / (k1 * (1 - b + b * (dl / avdl)) + fij);
      double third = ((k2 + 1) * fiq) / (k2 + fiq);
      return first * second * third;
    }).sum();
  }
}
