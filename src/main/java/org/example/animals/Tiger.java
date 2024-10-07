package org.example.animals;


import org.example.animals.meal.Beef;
import org.example.animals.mealTypes.Predators;
import org.example.animals.livingEnvironmen.Terrestrial;

public class Tiger extends Predators implements Terrestrial{
    private final static String TITLE = "Тигр";
    private final static String MOVE_TYPE = " ходит";

    public Tiger(){
        animalTitle = TITLE;
    }

    @Override
    public void walk() {
        System.out.println(animalTitle + MOVE_TYPE);
    }

    public void eat(Beef meal) {
        super.eat(meal);
    }
}
