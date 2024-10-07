package org.example.animals.meal;

public class Fish extends Meat {
    private static final String NAME = "рыбу";

    @Override
    public String getMealName() {
        return NAME;
    }
}
