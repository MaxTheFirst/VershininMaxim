package org.example.animals;

import org.example.animals.livingEnvironmen.Terrestrial;
import org.example.animals.mealTypes.Herbivores;

public class Camel extends Herbivores implements Terrestrial {
    public Camel(){
        animalTitle = "Верблюд";
    }

    @Override
    public void walk() {
        System.out.println(animalTitle + " ходит");
    }
}
