package com.sombrasdelavismo.model;

public class SpellCard extends Card {
    private final SpellEffect effect;
    private final SpellColor color;
    private final int primaryValue;
    private final int secondaryValue;
    private final String relatedCardId;

    public SpellCard(
            String id,
            String name,
            int manaCost,
            SpellEffect effect,
            SpellColor color,
            int primaryValue,
            int secondaryValue,
            String relatedCardId,
            String description,
            String imagePath) {
        super(id, name, manaCost, description, imagePath);
        this.effect = effect;
        this.color = color;
        this.primaryValue = primaryValue;
        this.secondaryValue = secondaryValue;
        this.relatedCardId = relatedCardId;
    }

    public SpellEffect getEffect() {
        return effect;
    }

    public SpellColor getColor() {
        return color;
    }

    public int getPrimaryValue() {
        return primaryValue;
    }

    public int getSecondaryValue() {
        return secondaryValue;
    }

    public String getRelatedCardId() {
        return relatedCardId;
    }

    public boolean requiresFriendlyTarget() {
        return effect == SpellEffect.BUFF || effect == SpellEffect.SET_ATTACK_TO_TEN;
    }

    @Override
    public Card copy() {
        return new SpellCard(
                getId(),
                getName(),
                getManaCost(),
                effect,
                color,
                primaryValue,
                secondaryValue,
                relatedCardId,
                getDescription(),
                getImagePath());
    }
}
