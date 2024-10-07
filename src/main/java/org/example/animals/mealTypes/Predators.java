package org.example.animals.mealTypes;

import org.example.animals.meal.Meat;

public abstract class Predators extends BaseType {
    public void printType() {
        super.printType("Хищники");
    }

    public void eat(Meat meal) {
        super.eat(meal);
    }
}
