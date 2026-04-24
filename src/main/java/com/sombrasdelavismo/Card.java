package com.sombrasdelavismo;

public abstract class Card {
    protected String name;
    protected String type;
    protected int cost;

    public Card(String name, String type, int cost) {
        this.name = name;
        this.type = type;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public abstract void play();
}