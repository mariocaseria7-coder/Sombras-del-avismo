package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> drawPile;
    private final List<Card> graveyard;

    public Deck() {
        this.drawPile = new ArrayList<>();
        this.graveyard = new ArrayList<>();
    }

    public void loadFreshCards(List<Card> cards) {
        drawPile.clear();
        graveyard.clear();
        for (Card card : cards) {
            drawPile.add(card.copy());
        }
    }

    public void shuffle() {
        Collections.shuffle(drawPile);
    }

    public Card draw() {
        if (drawPile.isEmpty()) {
            return null;
        }
        return drawPile.remove(drawPile.size() - 1);
    }

    public void sendToGraveyard(Card card) {
        if (card != null) {
            graveyard.add(card);
        }
    }

    public int size() {
        return drawPile.size();
    }

    public List<Card> getGraveyard() {
        return Collections.unmodifiableList(graveyard);
    }
}
