package sort.sorters;

import sort.BaseSorter;
import sort.ElementCountLimitedException;
import sort.SortType;

import java.util.ArrayList;
import java.util.List;

public class BubbleSorter implements BaseSorter {
  private final static int MAX_COUNT = 10_000;
  private int maxCount;

  public BubbleSorter(int maxCount) {
    this.maxCount = maxCount;
  }

  public BubbleSorter() {
    maxCount = MAX_COUNT;
  }

  @Override
  public SortType type() {
    return SortType.BUBBLE;
  }

  @Override
  public List<Integer> sort(List<Integer> array) throws ElementCountLimitedException {
    if (array.size() > maxCount){
      throw new ElementCountLimitedException();
    }
    List<Integer> newArray = new ArrayList<Integer>(array);
    for (int i = 0; i < newArray.size(); i++) {
      for (int j = i; j < newArray.size(); j++) {
        if (newArray.get(i) > newArray.get(j)) {
          Integer element = newArray.get(i);
          newArray.set(i, newArray.get(j));
          newArray.set(j, element);
        }
      }
    }
    return newArray;
  }
}
