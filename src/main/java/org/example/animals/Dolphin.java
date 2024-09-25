package org.example.animals;

import org.example.animals.livingEnvironmen.Waterfowl;
import org.example.animals.meal.Fish;
import org.example.animals.mealTypes.Predators;

public class Dolphin extends Predators implements Waterfowl {
    public Dolphin(){
        animalTitle = "Дельфин";
    }

    public void eat(Fish meal) {
        super.eat(meal);
    }

    @Override
    public void swim() {
        System.out.println(animalTitle + " плавает");
    }
}
