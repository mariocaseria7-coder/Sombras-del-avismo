package com.sombrasdelavismo;

public class CreatureCard extends Card {
    private int power;
    private int toughness;

    public CreatureCard(String name, int cost, int power, int toughness) {
        super(name, "Creature", cost);
        this.power = power;
        this.toughness = toughness;
    }

    public int getPower() {
        return power;
    }

    public int getToughness() {
        return toughness;
    }

    @Override
    public void play() {
        System.out.println("Playing creature: " + name);
    }
}