package com.sombrasdelavismo.model;

public abstract class Card {
    private final String id;
    private final String name;
    private final int manaCost;
    private final CardRarity rarity;
    private final String description;
    private final String imagePath;

    protected Card(String id, String name, int manaCost, String description, String imagePath) {
        this(id, name, manaCost, CardRarity.SPELL, description, imagePath);
    }

    protected Card(
            String id,
            String name,
            int manaCost,
            CardRarity rarity,
            String description,
            String imagePath) {
        this.id = id;
        this.name = name;
        this.manaCost = manaCost;
        this.rarity = rarity == null ? CardRarity.SPELL : rarity;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getManaCost() {
        return manaCost;
    }

    public CardRarity getRarity() {
        return rarity;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isCreature() {
        return this instanceof CreatureCard;
    }

    public boolean isSpell() {
        return this instanceof SpellCard;
    }

    public abstract Card copy();
}
