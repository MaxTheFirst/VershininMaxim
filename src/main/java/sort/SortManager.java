package sort;

import java.util.List;

public class SortManager {
  private final List<BaseSorter> sorters;

  public SortManager(List<BaseSorter> sorters) {
    this.sorters = sorters;
  }

  public List<Integer> sort(List<Integer> array, SortType type) throws ElementCountLimitedException {
    for (BaseSorter sorter : sorters) {
      if (sorter.type().equals(type)) {
        try {
         return sorter.sort(array);
        } catch (ElementCountLimitedException e) {
          System.out.println("Couldn`t execute sort. Trying next one.");
        }
      }
    }
    throw new ElementCountLimitedException("No implementation was suitable due to the large number of elements.");
  }
}
