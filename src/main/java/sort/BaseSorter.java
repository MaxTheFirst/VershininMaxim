package sort;

import java.util.List;

public interface BaseSorter {
  SortType type();
  List<Integer> sort(List<Integer> array);
}
