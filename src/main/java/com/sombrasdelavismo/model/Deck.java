package com.sombrasdelavismo.model;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private Stack<Card> cards;
    private Stack<Card> graveyard;

    public Deck() {
        this.cards = new Stack<>();
        this.graveyard = new Stack<>();
    }

    public Stack<Card> getCards() {
        return cards;
    }

    public void setCards(Stack<Card> cards) {
        this.cards = cards;
    }

    public Stack<Card> getGraveyard() {
        return graveyard;
    }

    public void setGraveyard(Stack<Card> graveyard) {
        this.graveyard = graveyard;
    }

    public void addCard(Card card) {
        cards.push(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.pop();
    }

    public void sendToGraveyard(Card card) {
        if (card != null) {
            graveyard.push(card);
        }
    }

    public int size() {
        return cards.size();
    }

    public int graveyardSize() {
        return graveyard.size();
    }

    public void loadCopies(List<Card> baseCards, int copiesPerCard) {
        for (int i = 0; i < copiesPerCard; i++) {
            for (Card card : baseCards) {
                addCard(card.copy());
            }
        }
    }
}
