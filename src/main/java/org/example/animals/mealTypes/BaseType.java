package org.example.animals.mealTypes;

import org.example.animals.meal.BaseMeal;

public abstract class BaseType {
    public String animalTitle;

    public void printType(String typeName){
        System.out.println(typeName);
    }

    public void eat(BaseMeal meal){
        System.out.println(animalTitle + " ест " + meal.getMealName());
    }
}
