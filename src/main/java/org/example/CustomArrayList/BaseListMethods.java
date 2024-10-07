package org.example.CustomArrayList;

public interface BaseListMethods<T> {
  /**
   * Size of list
   * @return integer
   */
  int getLenght();

  /**
   * Add element to list
   * @param value
   */
  void add(T value);

  /**
   * Get list element by index
   * @param index
   * @return
   */
  T get(int index);

  /**
   * Remove list element by index
   * @param index
   */
  void remove(int index);
}
