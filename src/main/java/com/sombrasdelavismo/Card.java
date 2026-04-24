package com.sombrasdelavismo;

public abstract class Card {
    protected final String name;
    protected final String type;
    protected final int cost;
    protected final String description;
    protected final String imagePath;

    public Card(String name, String type, int cost, String description, String imagePath) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.description = description;
        this.imagePath = imagePath;
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

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public abstract Card copy();

    public abstract void play();
}
