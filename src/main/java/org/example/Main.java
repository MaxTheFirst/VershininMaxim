package org.example;

import org.example.CustomArrayList.CustomArrayList;

public class Main {
  public static void main(String[] args) {
    CustomArrayList<Integer> array = new CustomArrayList<>();

    for (int i = 0; i < 200; i++) {
      array.add(i);
    }

    System.out.println(array.get(9));

    for (int i = 0; i < 200; i++) {
      array.remove(0);
    }
  }
}