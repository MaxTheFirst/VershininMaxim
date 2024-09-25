package org.example.animals;


import org.example.animals.meal.Beef;
import org.example.animals.mealTypes.Predators;
import org.example.animals.livingEnvironmen.Terrestrial;

public class Tiger extends Predators implements Terrestrial{
    public Tiger(){
        animalTitle = "Тигр";
    }

    @Override
    public void walk() {
        System.out.println(animalTitle + " ходит");
    }

    public void eat(Beef meal) {
        super.eat(meal);
    }
}
