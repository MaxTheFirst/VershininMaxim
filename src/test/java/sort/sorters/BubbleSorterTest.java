package sort.sorters;

import org.junit.jupiter.api.Test;
import sort.ElementCountLimitedException;
import sort.SortType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSorterTest {

  @Test
  void sort() throws ElementCountLimitedException {
    List<Integer> inList = List.of(3, 1, 2, 5);
    List<Integer> outList = List.of(1, 2, 3, 5);
    BubbleSorter sorter = new BubbleSorter();
    List<Integer> result = sorter.sort(inList);
    assertEquals(result, outList);
  }

  @Test
  void sortException() {
    List<Integer> inList = List.of(3, 1, 2, 5);
    BubbleSorter sorter = new BubbleSorter(3);
    assertThrows(
        ElementCountLimitedException.class,
        () -> sorter.sort(inList)
    );
  }

  @Test
  void type() {
    BubbleSorter sorter = new BubbleSorter(3);
    assertEquals(sorter.type(), SortType.BUBBLE);
  }
}