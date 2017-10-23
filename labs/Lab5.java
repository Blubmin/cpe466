package labs;

import ItemSetClasses.ItemSet;
import ItemSetClasses.Transactions;

import java.util.ArrayList;
import java.util.Map;

public class Lab5 {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: Lab5 shoppingData");
      System.exit(1);
    }

    String trans_file = args[0];
    Transactions transactions = new Transactions(trans_file);
    transactions.findFrequentItemSets(.01);

    System.out.println(transactions.getFrequentItemSets());
  }
}
