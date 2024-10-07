package org.example.animals;

import org.example.animals.livingEnvironmen.Flying;
import org.example.animals.mealTypes.Predators;

public class Eagle extends Predators implements Flying {
    private final static String TITLE = "Орел";
    private final static String MOVE_TYPE = " летает";

    public Eagle(){
        animalTitle = TITLE;
    }

    @Override
    public void fly() {
        System.out.println(animalTitle + MOVE_TYPE);
    }
}
