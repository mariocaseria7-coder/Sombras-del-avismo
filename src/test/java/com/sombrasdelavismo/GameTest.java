package com.sombrasdelavismo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    @Test
    void creatureNeedsToWaitUntilNextOwnTurnBeforeAttacking() {
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");
        CreatureCard wolf = new CreatureCard("Wolf", 1, 3, 3, "Criatura", "cards/wolf.jpg");

        player1.addCardToDeck(wolf);
        for (int i = 0; i < 4; i++) {
            player1.addCardToDeck(new SpellCard("Relleno " + i, 1, "Nada", 0, 0, 0, "cards/fill.jpg"));
            player2.addCardToDeck(new SpellCard("Relleno rival " + i, 1, "Nada", 0, 0, 0, "cards/fill.jpg"));
        }

        Game game = new Game(player1, player2);
        game.startGame();

        assertTrue(game.canPlaySelectedCard(wolf));
        game.playCard(wolf);
        assertFalse(game.canAttackWith(wolf));

        game.nextTurn();
        game.nextTurn();

        assertTrue(game.canAttackWith(wolf));
    }

    @Test
    void spellConsumesManaAndDamagesRival() {
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");
        SpellCard bolt = new SpellCard("Bolt", 1, "Dano", 4, 0, 0, "cards/bolt.jpg");

        player1.addCardToDeck(bolt);
        for (int i = 0; i < 4; i++) {
            player1.addCardToDeck(new CreatureCard("Aliado " + i, 1, 1, 1, "Criatura", "cards/a.jpg"));
            player2.addCardToDeck(new CreatureCard("Rival " + i, 1, 1, 1, "Criatura", "cards/b.jpg"));
        }

        Game game = new Game(player1, player2);
        game.startGame();

        int lifeBefore = player2.getLife();
        int manaBefore = player1.getMana();
        String result = game.playCard(bolt);

        assertEquals(lifeBefore - 4, player2.getLife());
        assertEquals(manaBefore - 1, player1.getMana());
        assertTrue(result.contains("Bolt"));
    }

    @Test
    void creatureCombatCanDestroyBothCreatures() {
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");
        CreatureCard attacker = new CreatureCard("Tigre", 1, 3, 2, "Criatura", "cards/tiger.jpg");
        CreatureCard defender = new CreatureCard("Oso", 1, 2, 3, "Criatura", "cards/bear.jpg");

        player1.addCardToDeck(attacker);
        player2.addCardToDeck(defender);
        for (int i = 0; i < 4; i++) {
            player1.addCardToDeck(new SpellCard("Relleno " + i, 1, "Nada", 0, 0, 0, "cards/fill.jpg"));
            player2.addCardToDeck(new SpellCard("Relleno rival " + i, 1, "Nada", 0, 0, 0, "cards/fill.jpg"));
        }

        Game game = new Game(player1, player2);
        game.startGame();
        game.playCard(attacker);
        game.nextTurn();
        game.playCard(defender);
        game.nextTurn();

        assertTrue(game.canAttackTarget(attacker, defender));
        String result = game.attackCreature(attacker, defender);

        assertFalse(player1.getBattlefield().contains(attacker));
        assertFalse(player2.getBattlefield().contains(defender));
        assertTrue(result.contains("Ambas criaturas son destruidas"));
    }

    @Test
    void copiedCardsDoNotShareState() {
        CreatureCard original = new CreatureCard("Bestia", 2, 3, 2, "Criatura", "cards/beast.jpg");
        CreatureCard copy = (CreatureCard) original.copy();

        original.markPlayedThisTurn();
        original.startOwnerTurn();
        original.consumeAttack();

        assertNotSame(original, copy);
        assertFalse(copy.isReadyToAttack());
        assertTrue(copy.isJustPlayed());
    }
}
