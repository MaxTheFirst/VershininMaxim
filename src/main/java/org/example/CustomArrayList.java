package org.example;


public class CustomArrayList<T> {
  private static final int DEFAULT_CAPACITY = 10;
  private int item = -1;
  private Object[] array;

  public CustomArrayList() {
    array = new Object[DEFAULT_CAPACITY];
  }

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

  public void add(T value) {
    if (value == null) {
      throw new NullPointerException();
    }
    ++item;
    expendArray();
    array[item] = value;
  }

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

  public void remove(int index) {
    System.arraycopy(array, index + 1, array, index, item - index);
    --item;
    narrow();
  }
}
