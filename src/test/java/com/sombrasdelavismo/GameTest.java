package com.sombrasdelavismo;

import com.sombrasdelavismo.model.ActionResult;
import com.sombrasdelavismo.model.Card;
import com.sombrasdelavismo.model.CardCatalog;
import com.sombrasdelavismo.model.CardRarity;
import com.sombrasdelavismo.model.CreatureCard;
import com.sombrasdelavismo.model.Game;
import com.sombrasdelavismo.model.Player;
import com.sombrasdelavismo.model.SpellCard;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    @Test
    void creatureNeedsToWaitUntilNextOwnTurnBeforeAttacking() {
        Player player1 = createPlayer("Alice");
        Player player2 = createPlayer("Bob");
        Game game = new Game(player1, player2);
        game.startGame();

        CreatureCard antonio = CardCatalog.createCreature("ANTONIO");
        player1.addCardToHand(antonio);

        assertTrue(game.canPlaySelectedCard(antonio));
        game.playCard(antonio);
        assertFalse(game.canAttackWith(antonio));

        game.nextTurn();
        game.nextTurn();

        assertTrue(game.canAttackWith(antonio));
    }

    @Test
    void enChinoSummonsLinToBattlefield() {
        Player player1 = createPlayer("Alice");
        Player player2 = createPlayer("Bob");
        Game game = new Game(player1, player2);
        game.startGame();

        SpellCard enChino = CardCatalog.createSpell("EN_CHINO");
        player1.addCardToHand(enChino);

        ActionResult result = game.playCard(enChino);

        assertTrue(result.successful());
        assertTrue(player1.getBattlefield().stream().anyMatch(creature -> "LIN".equals(creature.getId())));
        assertTrue(result.publicMessage().contains("Lin"));
    }

    @Test
    void banoUnlocksHumoWhenBathroomCrewIsOnBoard() {
        Player player1 = createPlayer("Alice");
        Player player2 = createPlayer("Bob");
        Game game = new Game(player1, player2);
        game.startGame();

        player1.addToBattlefield(CardCatalog.createCreature("ADRIAN"));
        player1.addToBattlefield(CardCatalog.createCreature("FABIO"));
        player1.addToBattlefield(CardCatalog.createCreature("FERNANDO"));

        SpellCard bano = CardCatalog.createSpell("BANO");
        SpellCard humo = CardCatalog.createSpell("HUMO");
        player1.addCardToHand(bano);
        player1.addCardToHand(humo);

        assertTrue(game.canPlaySelectedCard(bano));
        assertFalse(game.canPlaySelectedCard(humo));

        game.playCard(bano);

        assertTrue(player1.isSmokeUnlocked());
        assertTrue(game.canPlaySelectedCard(humo));
    }

    @Test
    void humoMakesNextCreatureHiddenFromOpponent() {
        Player player1 = createPlayer("Alice");
        Player player2 = createPlayer("Bob");
        Game game = new Game(player1, player2);
        game.startGame();

        player1.setSmokeUnlocked(true);
        SpellCard humo = CardCatalog.createSpell("HUMO");
        CreatureCard ines = CardCatalog.createCreature("INES");
        player1.addCardToHand(humo);
        player1.addCardToHand(ines);

        ActionResult smokeResult = game.playCard(humo);
        ActionResult creatureResult = game.playCard(ines);

        assertTrue(smokeResult.successful());
        assertTrue(creatureResult.successful());
        assertTrue(ines.isHiddenFromOpponent());
        assertTrue(creatureResult.publicMessage().contains("criatura oculta"));
    }

    @Test
    void hackerStealsOneCardFromOpponentHand() {
        Player player1 = createPlayer("Alice");
        Player player2 = createPlayer("Bob");
        Game game = new Game(player1, player2);
        game.startGame();

        SpellCard hacker = CardCatalog.createSpell("HACKER");
        player1.addCardToHand(hacker);

        int handBeforePlayer2 = player2.getHand().size();
        ActionResult result = game.playCard(hacker);

        assertTrue(result.successful());
        assertEquals(handBeforePlayer2 - 1, player2.getHand().size());
        assertTrue(result.privateMessage().contains("Carta robada:"));
    }

    @Test
    void copiedCardsDoNotShareBattleState() {
        CreatureCard original = CardCatalog.createCreature("MARCO");
        CreatureCard copy = (CreatureCard) original.copy();

        original.markSummoned();
        original.startOwnerTurn();
        original.exhaust();
        original.receiveDamage(3);

        assertNotSame(original, copy);
        assertFalse(copy.isExhausted());
        assertTrue(copy.hasSummoningSickness());
        assertEquals(copy.getBaseHealth(), copy.getHealth());
    }

    @Test
    void creatureRaritiesMatchCatalog() {
        assertEquals(CardRarity.MYTHIC, CardCatalog.createCreature("MARCO").getRarity());
        assertEquals(CardRarity.EPIC, CardCatalog.createCreature("JUANMA").getRarity());
        assertEquals(CardRarity.EPIC, CardCatalog.createCreature("DIEGO").getRarity());
        assertEquals(CardRarity.EPIC, CardCatalog.createCreature("MAMEN").getRarity());

        assertEquals(CardRarity.RARE, CardCatalog.createCreature("LUIS").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("ALEJANDRO").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("MARIO_R").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("MARIO_A").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("FRAN").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("PEDRO").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("JAVI").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("LIN").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("QUEMO").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("JAVI_B").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("FRAN_A").getRarity());
        assertEquals(CardRarity.RARE, CardCatalog.createCreature("CARMEN").getRarity());

        assertEquals(CardRarity.COMMON, CardCatalog.createCreature("URBANO").getRarity());
        assertEquals(CardRarity.COMMON, CardCatalog.createCreature("INES").getRarity());
        assertEquals(CardRarity.COMMON, CardCatalog.createCreature("ANTONIO").getRarity());
        assertEquals(CardRarity.COMMON, CardCatalog.createCreature("ADRIAN").getRarity());
        assertEquals(CardRarity.COMMON, CardCatalog.createCreature("FABIO").getRarity());
        assertEquals(CardRarity.COMMON, CardCatalog.createCreature("FERNANDO").getRarity());
    }

    @Test
    void defaultDeckUsesBalancedRarityCopies() {
        List<Card> deck = CardCatalog.createDefaultDeck();
        Map<CardRarity, Integer> rarityCounts = countRarities(deck);

        assertEquals(55, deck.size());
        assertEquals(1, rarityCounts.get(CardRarity.MYTHIC));
        assertEquals(4, rarityCounts.get(CardRarity.EPIC));
        assertEquals(12, rarityCounts.get(CardRarity.RARE));
        assertEquals(20, rarityCounts.get(CardRarity.COMMON));
        assertEquals(18, rarityCounts.get(CardRarity.SPELL));
    }

    private Map<CardRarity, Integer> countRarities(List<Card> cards) {
        Map<CardRarity, Integer> rarityCounts = new EnumMap<>(CardRarity.class);
        for (Card card : cards) {
            rarityCounts.merge(card.getRarity(), 1, Integer::sum);
        }
        return rarityCounts;
    }

    private Player createPlayer(String name) {
        Player player = new Player(name);
        player.setDeckTemplate(createFillerDeck());
        return player;
    }

    private List<Card> createFillerDeck() {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            deck.add(CardCatalog.createCreature("ANTONIO"));
            deck.add(CardCatalog.createCreature("INES"));
        }
        return deck;
    }
}
