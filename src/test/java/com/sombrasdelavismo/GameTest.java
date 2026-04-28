package com.sombrasdelavismo;

import com.sombrasdelavismo.model.CreatureCard;
import com.sombrasdelavismo.model.Game;
import com.sombrasdelavismo.model.Player;
import com.sombrasdelavismo.model.SpellCard;
import com.sombrasdelavismo.model.SpellType;
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

        for (int i = 0; i < 7; i++) {
            player1.addCardToDeck(new SpellCard("Relleno " + i, 1, SpellType.HEAL, 0, "Nada", "cards/fill.jpg"));
            player2.addCardToDeck(new SpellCard("Relleno rival " + i, 1, SpellType.HEAL, 0, "Nada", "cards/fill.jpg"));
        }

        Game game = new Game(player1, player2);
        game.startGame();
        player1.getHand().add(wolf);

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
        SpellCard bolt = new SpellCard("Bolt", 1, SpellType.DAMAGE, 4, "Dano", "cards/bolt.jpg");

        for (int i = 0; i < 7; i++) {
            player1.addCardToDeck(new CreatureCard("Aliado " + i, 1, 1, 1, "Criatura", "cards/a.jpg"));
            player2.addCardToDeck(new CreatureCard("Rival " + i, 1, 1, 1, "Criatura", "cards/b.jpg"));
        }

        Game game = new Game(player1, player2);
        game.startGame();
        player1.getHand().add(bolt);

        int lifeBefore = player2.getLife();
        int manaBefore = player1.getCurrentMana();
        String result = game.playCard(bolt);

        assertEquals(lifeBefore - 4, player2.getLife());
        assertEquals(manaBefore - 1, player1.getCurrentMana());
        assertTrue(result.contains("Bolt"));
    }

    @Test
    void blockedCombatDoesNotPassExcessDamageToPlayer() {
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");
        CreatureCard attacker = new CreatureCard("Tigre", 1, 5, 3, "Criatura", "cards/tiger.jpg");
        CreatureCard blocker = new CreatureCard("Oso", 1, 2, 1, "Criatura", "cards/bear.jpg");

        for (int i = 0; i < 7; i++) {
            player1.addCardToDeck(new SpellCard("Relleno " + i, 1, SpellType.HEAL, 0, "Nada", "cards/fill.jpg"));
            player2.addCardToDeck(new SpellCard("Relleno rival " + i, 1, SpellType.HEAL, 0, "Nada", "cards/fill.jpg"));
        }

        Game game = new Game(player1, player2);
        game.startGame();
        player1.getHand().add(attacker);
        game.playCard(attacker);
        game.nextTurn();
        player2.getHand().add(blocker);
        game.playCard(blocker);
        game.nextTurn();

        int rivalLifeBefore = player2.getLife();
        String result = game.attackCreature(attacker, blocker);

        assertFalse(player2.getCreatures().contains(blocker));
        assertEquals(rivalLifeBefore, player2.getLife());
        assertTrue(result.contains("bloquea"));
    }

    @Test
    void copiedCardsDoNotShareState() {
        CreatureCard original = new CreatureCard("Bestia", 2, 3, 2, "Criatura", "cards/beast.jpg");
        CreatureCard copy = (CreatureCard) original.copy();

        original.prepararInvocacion();
        original.enderezarParaTurnoPropio();
        original.girarPorAtaque();

        assertNotSame(original, copy);
        assertFalse(copy.isCanAttack());
        assertTrue(copy.isSummoningSickness());
    }
}
