package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.List;

public final class CardFactory {
    private CardFactory() {
    }

    public static List<Card> createInitialCardDatabase() {
        List<Card> cards = new ArrayList<>();

        cards.add(new CreatureCard(
                "Lobo Feral",
                2,
                3,
                2,
                "Criatura agresiva de bajo coste.",
                "images/carta_rodri.jpg"));
        cards.add(new CreatureCard(
                "Gólem de Piedra",
                4,
                4,
                5,
                "Defensor resistente del abismo.",
                "images/carta_gonzalo.jpg"));
        cards.add(new CreatureCard(
                "Dragón Azul",
                7,
                7,
                6,
                "Amenaza final que domina los cielos.",
                "images/carta_alonso.jpg"));

        cards.add(new SpellCard(
                "Bola de Fuego",
                4,
                SpellType.DAMAGE,
                6,
                "Inflige 6 de daño directo.",
                "images/hechizo_fueguito.jpg"));
        cards.add(new SpellCard(
                "Curación",
                2,
                SpellType.HEAL,
                4,
                "Restaura 4 puntos de vida.",
                "images/hechizo_humo.jpg"));
        cards.add(new SpellCard(
                "Inspiración",
                3,
                SpellType.DRAW,
                2,
                "Roba 2 cartas.",
                "images/hechizo_apuestas.jpg"));
        cards.add(new SpellCard(
                "Bendición del Vacío",
                3,
                SpellType.BUFF,
                0,
                2,
                2,
                "Otorga +2/+2 a una criatura aliada.",
                "images/hechizo_kungfu.jpg"));

        return cards;
    }

    public static Deck createStandardDeck() {
        Deck deck = new Deck();
        deck.loadCopies(createInitialCardDatabase(), 3);
        deck.shuffle();
        return deck;
    }
}
