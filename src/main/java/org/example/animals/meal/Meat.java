package org.example.animals.meal;

public class Meat implements BaseMeal{
    private static final String NAME = "мясо";

    @Override
    public String getMealName() {
        return NAME;
    }
}
