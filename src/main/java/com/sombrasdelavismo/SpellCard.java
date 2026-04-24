package com.sombrasdelavismo;

public class SpellCard extends Card {
    private String effect;

    public SpellCard(String name, int cost, String effect) {
        super(name, "Spell", cost);
        this.effect = effect;
    }

    public String getEffect() {
        return effect;
    }

    @Override
    public void play() {
        System.out.println("Casting spell: " + name + " - " + effect);
    }
}