package com.sombrasdelavismo.model;

public enum SpellColor {
    RED(3),
    BLUE(2),
    GRAY(0);

    private final int attackBonus;

    SpellColor(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}
