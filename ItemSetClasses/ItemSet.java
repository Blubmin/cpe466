package ItemSetClasses;

import java.util.ArrayList;
import java.util.Collections;

public class ItemSet implements Comparable<ItemSet>{

  private ArrayList<Integer> items;

  public ItemSet() {
    items = new ArrayList<>();
  }

  public void addItem(Integer item) {
    items.add(item);
    Collections.sort(items);
  }

  public Integer itemAt(int idx) {
    return items.get(idx);
  }

  public boolean contains(ItemSet set) {
    if (set.getSize() > getSize()) return false;
    for (Integer i : set.items) {
     if (!contains(i)) return false;
    }
    return true;
  }

  public boolean contains(Integer item) {
    return items.contains(item);
  }

  public ItemSet union(ItemSet set) {
    ItemSet new_set = new ItemSet();
    items.stream().forEach(item -> new_set.addItem(item));
    set.items.stream().forEach(item -> {
      if (!new_set.contains(item)) new_set.addItem(item);
    });
    return new_set;
  }

  public boolean prefixMatch(ItemSet set) {
    for (int i = 0; i < set.getSize() - 1; i++) {
      if (set.itemAt(i) != itemAt(i)) return false;
    }
    return true;
  }

  public int getSize() {
    return items.size();
  }

  @Override
  public int compareTo(ItemSet o) {
    if (o.getSize() != getSize()) return getSize() - o.getSize();
    for (int i = 0; i < o.getSize(); i++) {
      if (o.itemAt(i) != itemAt(i)) return itemAt(i) - o.itemAt(i);
    }
    return 0;
  }

  @Override
  public String toString() {
    return items.toString();
  }
}
