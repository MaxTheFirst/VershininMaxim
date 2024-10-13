import sort.ElementCountLimitedException;
import sort.SortManager;
import sort.SortType;
import sort.sorters.BubbleSorter;
import sort.sorters.MergeSorter;

import java.util.List;

public class Main {
  public static void main(String[] args) throws ElementCountLimitedException {
    List<Integer> inList = List.of(2, 3, 5, 10, 8, 7);
    SortManager manager = new SortManager(
        List.of(
            new BubbleSorter(),
            new MergeSorter()
        )
    );
    List<Integer> result = manager.sort(inList, SortType.MERGE);
    System.out.println(result);
  }
}