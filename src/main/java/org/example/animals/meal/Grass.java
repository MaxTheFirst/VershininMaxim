package org.example.animals.meal;

public class Grass implements BaseMeal{
    private static final String NAME = "траву";

    @Override
    public String getMealName() {
        return NAME;
    }
}
