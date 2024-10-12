package sort;

import org.junit.jupiter.api.Test;
import sort.sorters.BubbleSorter;
import sort.sorters.MergeSorter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortManagerTest {

  @Test
  void bubleSort() throws ElementCountLimitedException {
    List<Integer> inList = List.of(2, 3, 5, 10, 8, 7);
    List<Integer> outList = List.of(2, 3, 5, 7, 8, 10);
    SortManager manager = new SortManager(
        List.of(
            new BubbleSorter(),
            new MergeSorter()
        )
    );
    List<Integer> result = manager.sort(inList, SortType.BUBBLE);
    assertEquals(result, outList);
  }

  @Test
  void mergeSort() throws ElementCountLimitedException {
    List<Integer> inList = List.of(2, 3, 5, 10, 8, 7);
    List<Integer> outList = List.of(2, 3, 5, 7, 8, 10);
    SortManager manager = new SortManager(
        List.of(
            new BubbleSorter(),
            new MergeSorter()
        )
    );
    List<Integer> result = manager.sort(inList, SortType.MERGE);
    assertEquals(result, outList);
  }

  @Test
  void sortBubleExeption() {
    List<Integer> inList = List.of(2, 3, 5, 10, 8, 7);
    SortManager manager = new SortManager(
        List.of(
            new BubbleSorter(2),
            new MergeSorter(2)
        )
    );
    assertThrows(
        ElementCountLimitedException.class,
        () -> manager.sort(inList, SortType.BUBBLE)
    );
  }

  @Test
  void sortMergeExeption() {
    List<Integer> inList = List.of(2, 3, 5, 10, 8, 7);
    SortManager manager = new SortManager(
        List.of(
            new BubbleSorter(),
            new MergeSorter(3)
        )
    );
    assertThrows(
        ElementCountLimitedException.class,
        () -> manager.sort(inList, SortType.MERGE)
    );
  }
}