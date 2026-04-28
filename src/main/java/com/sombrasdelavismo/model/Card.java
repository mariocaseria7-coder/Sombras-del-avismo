package com.sombrasdelavismo.model;

public abstract class Card {
    private String name;
    private int costMana;
    private String description;
    private String imagePath;

    public Card(String name, int costMana, String description, String imagePath) {
        this.name = name;
        this.costMana = costMana;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCostMana() {
        return costMana;
    }

    public void setCostMana(int costMana) {
        this.costMana = costMana;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public abstract void usar();

    public abstract Card copy();

    @Override
    public String toString() {
        return name + " (Mana " + costMana + ")";
    }
}
