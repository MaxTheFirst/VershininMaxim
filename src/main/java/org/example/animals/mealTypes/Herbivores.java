package org.example.animals.mealTypes;

import org.example.animals.meal.Grass;

public class Herbivores extends BaseType{
    public void printType() {
        super.printType("Травоядные");
    }

    public void eat(Grass meal) {
        super.eat(meal);
    }
}
