package org.example.CustomArrayList;

/**
 * Implementation of a dynamic array
 * @param <T>
 */
public class CustomArrayList<T> implements BaseListMethods<T>{
  private static final int DEFAULT_CAPACITY = 10;
  private int item = -1;
  private Object[] array;

  /**
   * Initializes the array
   */
  public CustomArrayList() {
    array = new Object[DEFAULT_CAPACITY];
  }


  @Override
  public int getLenght() {
    return item + 1;
  }

  private void expendArray() {
    if (item == array.length) {
      Object[] newArray = new Object[array.length + DEFAULT_CAPACITY];
      System.arraycopy(array, 0, newArray, 0, array.length);
      array = newArray;
    }
  }

  @Override
  public void add(T value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    ++item;
    expendArray();
    array[item] = value;
  }

  @Override
  public T get(int index) {
    return (T) array[index];
  }

  private void narrow() {
    int newLenght = array.length - DEFAULT_CAPACITY;
    if (item < newLenght) {
      Object[] newArray = new Object[newLenght];
      System.arraycopy(array, 0, newArray, 0, newLenght);
      array = newArray;
    }
  }

  @Override
  public void remove(int index) {
    System.arraycopy(array, index + 1, array, index, item - index);
    --item;
    narrow();
  }
}
