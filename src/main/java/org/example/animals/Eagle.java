package org.example.animals;

import org.example.animals.livingEnvironmen.Flying;
import org.example.animals.mealTypes.Predators;

public class Eagle extends Predators implements Flying {
    public Eagle(){
        animalTitle = "Орел";
    }

    @Override
    public void fly() {
        System.out.println(animalTitle + " летает");
    }
}
