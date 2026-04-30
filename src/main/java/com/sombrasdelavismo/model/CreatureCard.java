package com.sombrasdelavismo.model;

public class CreatureCard extends Card {
    private final int baseAttack;
    private final int baseHealth;
    private final boolean slippery;
    private final boolean bathroomCrew;
    private int attack;
    private int health;
    private boolean summoningSickness;
    private boolean exhausted;
    private boolean hiddenFromOpponent;

    public CreatureCard(
            String id,
            String name,
            int manaCost,
            int attack,
            int health,
            String description,
            String imagePath) {
        this(id, name, manaCost, attack, health, description, imagePath, CardRarity.COMMON);
    }

    public CreatureCard(
            String id,
            String name,
            int manaCost,
            int attack,
            int health,
            String description,
            String imagePath,
            CardRarity rarity) {
        this(id, name, manaCost, attack, health, description, imagePath, rarity, false, false);
    }

    public CreatureCard(
            String id,
            String name,
            int manaCost,
            int attack,
            int health,
            String description,
            String imagePath,
            boolean slippery,
            boolean bathroomCrew) {
        this(id, name, manaCost, attack, health, description, imagePath, CardRarity.COMMON, slippery, bathroomCrew);
    }

    public CreatureCard(
            String id,
            String name,
            int manaCost,
            int attack,
            int health,
            String description,
            String imagePath,
            CardRarity rarity,
            boolean slippery,
            boolean bathroomCrew) {
        super(id, name, manaCost, rarity, description, imagePath);
        this.baseAttack = attack;
        this.baseHealth = health;
        this.slippery = slippery;
        this.bathroomCrew = bathroomCrew;
        resetBattleState();
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public boolean isSlippery() {
        return slippery;
    }

    public boolean isBathroomCrew() {
        return bathroomCrew;
    }

    public boolean hasSummoningSickness() {
        return summoningSickness;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public boolean isHiddenFromOpponent() {
        return hiddenFromOpponent;
    }

    public void setHiddenFromOpponent(boolean hiddenFromOpponent) {
        this.hiddenFromOpponent = hiddenFromOpponent;
    }

    public void resetBattleState() {
        this.attack = baseAttack;
        this.health = baseHealth;
        this.summoningSickness = true;
        this.exhausted = false;
        this.hiddenFromOpponent = false;
    }

    public void markSummoned() {
        this.summoningSickness = true;
        this.exhausted = false;
    }

    public void startOwnerTurn() {
        this.summoningSickness = false;
        this.exhausted = false;
    }

    public boolean canAttack() {
        return health > 0 && !summoningSickness && !exhausted;
    }

    public boolean canDefend() {
        return health > 0 && !exhausted;
    }

    public void exhaust() {
        this.exhausted = true;
    }

    public void reveal() {
        this.hiddenFromOpponent = false;
    }

    public int receiveDamage(int rawDamage) {
        if (rawDamage < 0) {
            return 0;
        }
        int appliedDamage = slippery ? Math.max(0, rawDamage - 2) : rawDamage;
        health = Math.max(0, health - appliedDamage);
        return appliedDamage;
    }

    public void addStats(int attackBonus, int healthBonus) {
        this.attack = Math.max(0, attack + attackBonus);
        this.health = Math.max(1, health + healthBonus);
    }

    public void setAttackToAtLeast(int targetAttack) {
        if (attack < targetAttack) {
            attack = targetAttack;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public Card copy() {
        return new CreatureCard(
                getId(),
                getName(),
                getManaCost(),
                baseAttack,
                baseHealth,
                getDescription(),
                getImagePath(),
                getRarity(),
                slippery,
                bathroomCrew);
    }
}
