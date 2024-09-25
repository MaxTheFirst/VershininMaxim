package org.example.animals;

import org.example.animals.mealTypes.Herbivores;
import org.example.animals.livingEnvironmen.Terrestrial;

public class Hourse extends Herbivores implements Terrestrial {
    public Hourse() {
        animalTitle = "Лощадь";
    }

    @Override
    public void walk() {
        System.out.println(animalTitle + " ходит");
    }
}
