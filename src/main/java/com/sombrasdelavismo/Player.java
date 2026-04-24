package com.sombrasdelavismo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private final String name;
    private final List<Card> deck;
    private final List<Card> hand;
    private final List<CreatureCard> battlefield;
    private int life;
    private int mana;
    private int maxMana;

    public Player(String name) {
        this.name = name;
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.battlefield = new ArrayList<>();
        this.life = 20;
        this.mana = 0;
        this.maxMana = 0;
    }

    public void addCardToDeck(Card card) {
        deck.add(card);
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public Card drawCard() {
        if (!deck.isEmpty()) {
            Card card = deck.remove(0);
            hand.add(card);
            return card;
        }
        return null;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void takeDamage(int damage) {
        life = Math.max(0, life - damage);
    }

    public void heal(int amount) {
        life += amount;
    }

    public String getName() {
        return name;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public List<CreatureCard> getBattlefield() {
        return battlefield;
    }

    public void addToBattlefield(CreatureCard creatureCard) {
        battlefield.add(creatureCard);
    }

    public boolean removeFromHand(Card card) {
        return hand.remove(card);
    }

    public void clearBattlefield() {
        battlefield.clear();
    }

    public void resetForNewGame() {
        hand.clear();
        battlefield.clear();
        life = 20;
        mana = 0;
        maxMana = 0;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void startTurn() {
        maxMana = Math.min(10, maxMana + 1);
        mana = maxMana;
        drawCard();
        for (CreatureCard creature : battlefield) {
            creature.startOwnerTurn();
        }
    }

    public boolean canPlay(Card card) {
        return card != null && mana >= card.getCost();
    }

    public boolean spendMana(int amount) {
        if (mana < amount) {
            return false;
        }
        mana -= amount;
        return true;
    }
}
