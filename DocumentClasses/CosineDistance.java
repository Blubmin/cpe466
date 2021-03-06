package DocumentClasses;

public class CosineDistance implements DocumentDistance {
  @Override
  public double findDistance(TextVector query, TextVector document, DocumentCollection documents) {
    if (query.getDistinctWordCount() == 0 || document.getDistinctWordCount() == 0) return 0;
    return query.normalized_dot(document) / (query.getL2Norm() * document.getL2Norm());
  }
}
