package org.example.animals;

import org.example.animals.mealTypes.Herbivores;
import org.example.animals.livingEnvironmen.Terrestrial;

public class Hourse extends Herbivores implements Terrestrial {
    private final static String TITLE = "Лошадь";
    private final static String MOVE_TYPE = " ходит";

    public Hourse(){
        animalTitle = TITLE;
    }

    @Override
    public void walk() {
        System.out.println(animalTitle + MOVE_TYPE);
    }
}
