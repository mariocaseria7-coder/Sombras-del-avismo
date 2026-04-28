package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private static final int INITIAL_LIFE = 20;
    private static final int MAX_MANA_CAP = 10;

    private String name;
    private int life;
    private int currentMana;
    private int maxMana;
    private List<Card> hand;
    private List<CreatureCard> creatures;
    private Deck deck;
    private boolean lostByFatigue;

    public Player(String name) {
        this.name = name;
        this.life = INITIAL_LIFE;
        this.currentMana = 0;
        this.maxMana = 0;
        this.hand = new ArrayList<>();
        this.creatures = new ArrayList<>();
        this.deck = new Deck();
        this.lostByFatigue = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(0, currentMana);
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.min(MAX_MANA_CAP, Math.max(0, maxMana));
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public List<CreatureCard> getCreatures() {
        return creatures;
    }

    public void setCreatures(List<CreatureCard> creatures) {
        this.creatures = creatures;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public boolean isLostByFatigue() {
        return lostByFatigue;
    }

    public void setLostByFatigue(boolean lostByFatigue) {
        this.lostByFatigue = lostByFatigue;
    }

    public Card drawCard() {
        Card card = deck.draw();
        if (card == null) {
            lostByFatigue = true;
            return null;
        }
        hand.add(card);
        return card;
    }

    public void addCardToDeck(Card card) {
        deck.addCard(card);
    }

    public void shuffleDeck() {
        deck.shuffle();
    }

    public boolean removeFromHand(Card card) {
        return hand.remove(card);
    }

    public void addCreature(CreatureCard creature) {
        creatures.add(creature);
    }

    public boolean removeCreature(CreatureCard creature) {
        return creatures.remove(creature);
    }

    public boolean canPlay(Card card) {
        return card != null && currentMana >= card.getCostMana();
    }

    public boolean spendMana(int amount) {
        if (currentMana < amount) {
            return false;
        }
        currentMana -= amount;
        return true;
    }

    public void increaseMana() {
        if (maxMana < MAX_MANA_CAP) {
            maxMana++;
        }
    }

    public void refillMana() {
        currentMana = maxMana;
    }

    public void receiveDamage(int amount) {
        life = Math.max(0, life - amount);
    }

    public void heal(int amount) {
        life += Math.max(0, amount);
    }

    public void resetForNewGame() {
        life = INITIAL_LIFE;
        currentMana = 0;
        maxMana = 0;
        hand.clear();
        creatures.clear();
        lostByFatigue = false;
        deck.getGraveyard().clear();
    }
}
