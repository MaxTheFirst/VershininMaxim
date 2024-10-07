package org.example.animals.meal;

public class Beef extends Meat{
    private static final String NAME = "говядину";

    @Override
    public String getMealName() {
        return NAME;
    }
}
