package org.example;

import org.example.animals.Dolphin;
import org.example.animals.Hourse;
import org.example.animals.meal.Fish;
import org.example.animals.meal.Grass;

public class Main {
    public static void main(String[] args) {
        Hourse hourse = new Hourse();
        hourse.walk();

        Grass grass = new Grass();
        hourse.eat(grass);

        Dolphin dolphin = new Dolphin();
        dolphin.swim();

        Fish fish = new Fish();
        dolphin.eat(fish);
    }
}