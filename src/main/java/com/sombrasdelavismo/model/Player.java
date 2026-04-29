package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    public static final int STARTING_LIFE = 20;

    private final String name;
    private final List<Card> deckTemplate;
    private final List<Card> hand;
    private final List<CreatureCard> battlefield;
    private final Deck deck;
    private int life;
    private int currentMana;
    private int maxMana;
    private boolean revealOpponentHand;
    private boolean smokeUnlocked;
    private boolean hideNextPlay;
    private boolean alliesProtected;

    public Player(String name) {
        this.name = name;
        this.deckTemplate = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.battlefield = new ArrayList<>();
        this.deck = new Deck();
        resetCoreStats();
    }

    private void resetCoreStats() {
        this.life = STARTING_LIFE;
        this.currentMana = 0;
        this.maxMana = 0;
        this.revealOpponentHand = false;
        this.smokeUnlocked = false;
        this.hideNextPlay = false;
        this.alliesProtected = false;
    }

    public String getName() {
        return name;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = Math.max(0, life);
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public boolean isRevealOpponentHand() {
        return revealOpponentHand;
    }

    public void setRevealOpponentHand(boolean revealOpponentHand) {
        this.revealOpponentHand = revealOpponentHand;
    }

    public boolean isSmokeUnlocked() {
        return smokeUnlocked;
    }

    public void setSmokeUnlocked(boolean smokeUnlocked) {
        this.smokeUnlocked = smokeUnlocked;
    }

    public boolean isHideNextPlay() {
        return hideNextPlay;
    }

    public void setHideNextPlay(boolean hideNextPlay) {
        this.hideNextPlay = hideNextPlay;
    }

    public boolean isAlliesProtected() {
        return alliesProtected;
    }

    public void setAlliesProtected(boolean alliesProtected) {
        this.alliesProtected = alliesProtected;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public List<CreatureCard> getBattlefield() {
        return Collections.unmodifiableList(battlefield);
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeckTemplate(List<Card> cards) {
        deckTemplate.clear();
        for (Card card : cards) {
            deckTemplate.add(card.copy());
        }
    }

    public void resetForNewGame() {
        resetCoreStats();
        hand.clear();
        battlefield.clear();
        deck.loadFreshCards(deckTemplate);
    }

    public void shuffleDeck() {
        deck.shuffle();
    }

    public Card drawCard() {
        Card drawnCard = deck.draw();
        if (drawnCard != null) {
            hand.add(drawnCard);
        }
        return drawnCard;
    }

    public boolean canPlay(Card card) {
        return card != null && currentMana >= card.getManaCost();
    }

    public boolean spendMana(int amount) {
        if (amount > currentMana) {
            return false;
        }
        currentMana -= amount;
        return true;
    }

    public void increaseMaxMana(int amount, int manaCap) {
        maxMana = Math.min(manaCap, maxMana + amount);
    }

    public void refillMana() {
        currentMana = maxMana;
    }

    public void boostMana(int amount, int manaCap) {
        maxMana = Math.min(manaCap, maxMana + amount);
        currentMana = Math.min(maxMana, currentMana + amount);
    }

    public void receiveDamage(int damage) {
        life = Math.max(0, life - damage);
    }

    public void heal(int amount) {
        life += Math.max(0, amount);
    }

    public boolean removeFromHand(Card card) {
        return hand.remove(card);
    }

    public void addCardToHand(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    public void addToBattlefield(CreatureCard creature) {
        battlefield.add(creature);
    }

    public boolean removeFromBattlefield(CreatureCard creature) {
        return battlefield.remove(creature);
    }

    public boolean hasBathroomCrewOnBoard() {
        boolean hasAdrian = false;
        boolean hasFabio = false;
        boolean hasFernando = false;

        for (CreatureCard creature : battlefield) {
            if ("ADRIAN".equals(creature.getId())) {
                hasAdrian = true;
            } else if ("FABIO".equals(creature.getId())) {
                hasFabio = true;
            } else if ("FERNANDO".equals(creature.getId())) {
                hasFernando = true;
            }
        }

        return hasAdrian && hasFabio && hasFernando;
    }

    public String describeHand() {
        if (hand.isEmpty()) {
            return "La mano rival está vacía.";
        }

        return hand.stream()
                .map(card -> card.getName() + " (" + card.getManaCost() + ")")
                .collect(Collectors.joining(", "));
    }
}
