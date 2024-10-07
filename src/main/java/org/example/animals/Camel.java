package org.example.animals;

import org.example.animals.livingEnvironmen.Terrestrial;
import org.example.animals.mealTypes.Herbivores;

public class Camel extends Herbivores implements Terrestrial {
    private final static String TITLE = "Верблюд";
    private final static String MOVE_TYPE = " ходит";

    public Camel(){
        animalTitle = TITLE;
    }

    @Override
    public void walk() {
        System.out.println(animalTitle + MOVE_TYPE);
    }
}
