package ItemSetClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public class Transactions {

  HashMap<Integer, ItemSet> transactions;
  HashSet<Integer> items;
  HashMap<Integer, ArrayList<ItemSet>> frequent_item_sets;

  public Transactions(String transactions_file) {
    transactions = new HashMap<>();
    items = new HashSet<>();
    frequent_item_sets = new HashMap<>();

    try (Stream<String> stream = Files.lines(Paths.get(transactions_file))) {
      for (String line : (Iterable<String>) stream::iterator) {
        String[] args = line.split(",");

        Integer id = -1;
        ItemSet transaction = new ItemSet();
        for (String item : args) {
          if (id < 0) {
            id = Integer.parseInt(item);
            continue;
          }
          Integer item_id = Integer.parseInt(item.trim());
          items.add(item_id);
          transaction.addItem(item_id);
        }
        transactions.put(id, transaction);
      }
    } catch (IOException exc) {
      System.err.println(exc);
      System.exit(1);
    }
  }

  public void findFrequentItemSets(Double support) {
    int k = 1;
    while(findFrequentItemSet(k, support)) { k++; }
  }

  private boolean findFrequentItemSet(int k, Double support) {
    if (k == 1) return initItemSet1(support);

    ArrayList<ItemSet> freq_sets = new ArrayList<>();
    ArrayList<ItemSet> prev_set = frequent_item_sets.get(k - 1);

    for (int i = 0, j = 1; i < prev_set.size() - 1; j++) {
      if (j >= prev_set.size()) {
        i++;
        j = i;
        continue;
      }
      ItemSet first = prev_set.get(i);
      ItemSet second = prev_set.get(j);
      if (!first.prefixMatch(second)) {
        i++;
        j = i;
        continue;
      }
      ItemSet union = first.union(second);
      if (isFrequentSet(union, support)) freq_sets.add(union);
    }
    Collections.sort(freq_sets);
    if (!freq_sets.isEmpty())
      frequent_item_sets.put(k, freq_sets);

    return freq_sets.size() > 0;
  }

  private boolean initItemSet1(Double support) {
    ArrayList<ItemSet> freq_sets = new ArrayList<>();
    items.parallelStream().forEach(item ->{
      ItemSet set = new ItemSet();
      set.addItem(item);
      if (isFrequentSet(set, support)) freq_sets.add(set);
    });
    Collections.sort(freq_sets);
    frequent_item_sets.put(1, freq_sets);

    return freq_sets.size() > 0;
  }

  private boolean isFrequentSet(ItemSet set, Double support) {
    int count = transactions.entrySet()
      .parallelStream()
      .mapToInt(transaction -> transaction.getValue().contains(set) ? 1 : 0)
      .sum();

    return count / (double) transactions.size() - support > -10e-6;
  }

  public HashMap<Integer, ArrayList<ItemSet>> getFrequentItemSets() {
    return frequent_item_sets;
  }
}
