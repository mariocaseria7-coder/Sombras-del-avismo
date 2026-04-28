package com.sombrasdelavismo.model;

public class SpellCard extends Card {
    private SpellType type;
    private int value;
    private int drawCount;
    private int buffAttack;
    private int buffLife;

    public SpellCard(String name, int costMana, SpellType type, int value, String description, String imagePath) {
        this(name, costMana, type, value, 0, 0, description, imagePath);
    }

    public SpellCard(
            String name,
            int costMana,
            SpellType type,
            int value,
            int buffAttack,
            int buffLife,
            String description,
            String imagePath) {
        super(name, costMana, description, imagePath);
        this.type = type;
        this.value = value;
        this.drawCount = type == SpellType.DRAW ? value : 0;
        this.buffAttack = buffAttack;
        this.buffLife = buffLife;
    }

    public SpellType getType() {
        return type;
    }

    public void setType(SpellType type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public int getBuffAttack() {
        return buffAttack;
    }

    public void setBuffAttack(int buffAttack) {
        this.buffAttack = buffAttack;
    }

    public int getBuffLife() {
        return buffLife;
    }

    public void setBuffLife(int buffLife) {
        this.buffLife = buffLife;
    }

    @Override
    public void usar() {
        // La resolucion real se gestiona desde Game.
    }

    @Override
    public Card copy() {
        return new SpellCard(
                getName(),
                getCostMana(),
                type,
                value,
                buffAttack,
                buffLife,
                getDescription(),
                getImagePath());
    }

    @Override
    public String toString() {
        return getName() + " [" + type + " " + value + "] Mana " + getCostMana();
    }
}
