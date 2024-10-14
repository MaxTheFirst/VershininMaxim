package sort.sorters;

import sort.BaseSorter;
import sort.ElementCountLimitedException;
import sort.SortType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergeSorter implements BaseSorter {
  private final static int MAX_COUNT = 10_000;
  private int maxCount;

  public MergeSorter(int maxCount) {
    this.maxCount = maxCount;
  }

  public MergeSorter() {
    maxCount = MAX_COUNT;
  }

  @Override
  public SortType type() {
    return SortType.MERGE;
  }

  @Override
  public List<Integer> sort(List<Integer> array) {
    if (array.size() > maxCount){
      throw new ElementCountLimitedException();
    }
    List<Integer> newArray = new ArrayList<Integer>(array);
    Collections.sort(newArray);
    return newArray;
  }
}
