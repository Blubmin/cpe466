package DocumentClasses;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextVector implements Serializable {

    private HashMap<String, Integer> rawVector;

    public TextVector() {
        rawVector = new HashMap<>();
    }

    public Set<Map.Entry<String, Integer>> getRawVectorEntrySet() {
        return rawVector.entrySet();
    }

    public void add(String word) {
        Integer count = contains(word) ? rawVector.get(word) : 0;
        rawVector.put(word, count + 1);
    }

    public boolean contains(String word) {
        return rawVector.containsKey(word);
    }

    public int getRawFrequency(String word) {
        return rawVector.getOrDefault(word, 0);
    }

    public int getTotalWordCount() {
        return rawVector.values().stream().reduce(0, Integer::sum);
    }

    public int getDistinctWordCount() {
        return  rawVector.size();
    }

    public int getHighestRawFrequency() {
        return rawVector.values().stream().reduce(0, Integer::max);
    }

    public String getMostFrequentWord() {
        int max = getHighestRawFrequency();
        for (Map.Entry<String, Integer> entry : getRawVectorEntrySet()) {
            if (entry.getValue() == max) return entry.getKey();
        }
        return null;
    }
}
