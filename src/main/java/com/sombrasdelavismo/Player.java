package com.sombrasdelavismo;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> deck;
    private List<Card> hand;
    private int life;

    public Player(String name) {
        this.name = name;
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.life = 20; // Standard life
    }

    public void addCardToDeck(Card card) {
        deck.add(card);
    }

    public void drawCard() {
        if (!deck.isEmpty()) {
            hand.add(deck.remove(0));
        }
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

    public String getName() {
        return name;
    }
}