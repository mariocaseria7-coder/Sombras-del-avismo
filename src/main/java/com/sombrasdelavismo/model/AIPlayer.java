package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AIPlayer {
    private final Player player;

    public AIPlayer(Player player) {
        this.player = player;
    }

    public List<String> playTurn(Game game) {
        List<String> actions = new ArrayList<>();
        if (game.getCurrentPlayer() != player || game.isGameFinished()) {
            return actions;
        }

        boolean playedSomething = true;
        while (playedSomething) {
            playedSomething = false;
            Card selected = findBestPlayableCard();
            if (selected != null) {
                actions.add(game.playCard(selected));
                playedSomething = true;
            }
        }

        List<CreatureCard> attackers = new ArrayList<>(game.getAvailableAttackers());
        for (CreatureCard attacker : attackers) {
            if (game.isGameFinished()) {
                break;
            }
            List<CreatureCard> blockers = game.getAvailableBlockers();
            if (blockers.isEmpty()) {
                actions.add(game.attackDirectly(attacker));
            } else {
                actions.add(game.attackCreature(attacker, blockers.get(0)));
            }
        }

        return actions;
    }

    private Card findBestPlayableCard() {
        return player.getHand().stream()
                .filter(player::canPlay)
                .max(Comparator.comparingInt(card -> {
                    if (card instanceof CreatureCard creatureCard) {
                        return creatureCard.getAttack() + creatureCard.getLife();
                    }
                    SpellCard spellCard = (SpellCard) card;
                    return switch (spellCard.getType()) {
                        case DAMAGE -> 100 + spellCard.getValue();
                        case HEAL -> 80 + spellCard.getValue();
                        case DRAW -> 70 + spellCard.getDrawCount();
                        case BUFF -> 60 + spellCard.getBuffAttack() + spellCard.getBuffLife();
                    };
                }))
                .orElse(null);
    }
}
