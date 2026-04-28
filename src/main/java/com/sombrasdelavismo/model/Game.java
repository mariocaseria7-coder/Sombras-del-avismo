package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private static final int INITIAL_HAND_SIZE = 4;

    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player waitingPlayer;
    private int turnNumber;
    private int actionCounter;
    private String phase;
    private String lastAction;
    private Player winner;
    private boolean gameFinished;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.waitingPlayer = player2;
        this.turnNumber = 0;
        this.actionCounter = 0;
        this.phase = "Preparacion";
        this.lastAction = "La partida esta lista para comenzar.";
        this.gameFinished = false;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getWaitingPlayer() {
        return waitingPlayer;
    }

    public void setWaitingPlayer(Player waitingPlayer) {
        this.waitingPlayer = waitingPlayer;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public int getActionCounter() {
        return actionCounter;
    }

    public void setActionCounter(int actionCounter) {
        this.actionCounter = actionCounter;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public void startGame() {
        player1.resetForNewGame();
        player2.resetForNewGame();
        player1.shuffleDeck();
        player2.shuffleDeck();

        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            if (!drawWithoutFatigueLoss(player1) || !drawWithoutFatigueLoss(player2)) {
                return;
            }
        }

        currentPlayer = player1;
        waitingPlayer = player2;
        turnNumber = 1;
        actionCounter = 0;
        executeStartPhase();
    }

    public void nextTurn() {
        if (gameFinished) {
            return;
        }
        Player previous = currentPlayer;
        currentPlayer = waitingPlayer;
        waitingPlayer = previous;
        turnNumber++;
        actionCounter = 0;
        executeStartPhase();
    }

    public void executeStartPhase() {
        if (gameFinished) {
            return;
        }

        phase = "Inicio";
        currentPlayer.increaseMana();
        currentPlayer.refillMana();

        Card drawnCard = currentPlayer.drawCard();
        if (drawnCard == null) {
            loseByFatigue(currentPlayer);
            return;
        }

        phase = "Mantenimiento";
        for (CreatureCard creature : currentPlayer.getCreatures()) {
            creature.enderezarParaTurnoPropio();
        }

        phase = "Accion";
        lastAction = currentPlayer.getName() + " inicia el turno " + turnNumber
                + ", roba " + drawnCard.getName() + " y prepara sus criaturas.";
    }

    public boolean canPlaySelectedCard(Card card) {
        return !gameFinished
                && card != null
                && currentPlayer.getHand().contains(card)
                && currentPlayer.canPlay(card);
    }

    public String playCard(Card card) {
        if (gameFinished) {
            return "La partida ya ha terminado.";
        }
        if (card == null) {
            return "Selecciona una carta valida.";
        }
        if (!currentPlayer.getHand().contains(card)) {
            return "Solo puedes jugar cartas de tu mano.";
        }
        if (!currentPlayer.canPlay(card)) {
            return "No tienes mana suficiente para jugar " + card.getName() + ".";
        }

        currentPlayer.spendMana(card.getCostMana());
        currentPlayer.removeFromHand(card);
        actionCounter++;

        if (card instanceof CreatureCard creatureCard) {
            creatureCard.prepararInvocacion();
            currentPlayer.addCreature(creatureCard);
            creatureCard.usar();
            lastAction = currentPlayer.getName() + " invoca a " + creatureCard.getName() + ".";
            return lastAction;
        }

        SpellCard spellCard = (SpellCard) card;
        resolveSpell(spellCard, null);
        currentPlayer.getDeck().sendToGraveyard(spellCard);
        spellCard.usar();
        return lastAction;
    }

    public boolean canAttackWith(CreatureCard attacker) {
        return !gameFinished
                && attacker != null
                && currentPlayer.getCreatures().contains(attacker)
                && attacker.isCanAttack()
                && !attacker.isTapped();
    }

    public boolean canAttackDirectly(CreatureCard attacker) {
        return canAttackWith(attacker);
    }

    public boolean canAttackTarget(CreatureCard attacker, CreatureCard defender) {
        return canAttackWith(attacker)
                && defender != null
                && waitingPlayer.getCreatures().contains(defender)
                && defender.isCanDefend();
    }

    public List<CreatureCard> getAvailableAttackers() {
        List<CreatureCard> attackers = new ArrayList<>();
        for (CreatureCard creature : currentPlayer.getCreatures()) {
            if (canAttackWith(creature)) {
                attackers.add(creature);
            }
        }
        return attackers;
    }

    public List<CreatureCard> getAvailableBlockers() {
        List<CreatureCard> blockers = new ArrayList<>();
        for (CreatureCard creature : waitingPlayer.getCreatures()) {
            if (creature.isCanDefend() && !creature.isTapped()) {
                blockers.add(creature);
            }
        }
        return blockers;
    }

    public String attackDirectly(CreatureCard attacker) {
        return resolveAttack(attacker, null);
    }

    public String attackCreature(CreatureCard attacker, CreatureCard defender) {
        return resolveAttack(attacker, defender);
    }

    public String resolveAttack(CreatureCard attacker, CreatureCard blocker) {
        if (gameFinished) {
            return "La partida ya ha terminado.";
        }
        if (!canAttackWith(attacker)) {
            return "La criatura seleccionada no puede atacar.";
        }

        phase = "Combate";
        actionCounter++;
        attacker.girarPorAtaque();

        if (blocker == null) {
            attacker.atacar(waitingPlayer);
            lastAction = currentPlayer.getName() + " ataca con " + attacker.getName()
                    + " directamente a " + waitingPlayer.getName() + " por " + attacker.getAttack() + " de daño.";
            updateWinnerByLife();
            return lastAction;
        }

        if (!waitingPlayer.getCreatures().contains(blocker) || !blocker.isCanDefend() || blocker.isTapped()) {
            return "La criatura defensora no puede bloquear.";
        }

        attacker.atacar(blocker);
        blocker.defender(attacker);
        moveDeadCreaturesToGraveyard();

        StringBuilder result = new StringBuilder();
        result.append(currentPlayer.getName())
                .append(" ataca con ")
                .append(attacker.getName())
                .append(" y ")
                .append(waitingPlayer.getName())
                .append(" bloquea con ")
                .append(blocker.getName())
                .append(".");

        if (!currentPlayer.getCreatures().contains(attacker) && !waitingPlayer.getCreatures().contains(blocker)) {
            result.append(" Ambas criaturas mueren.");
        } else if (!currentPlayer.getCreatures().contains(attacker)) {
            result.append(" ").append(attacker.getName()).append(" muere.");
        } else if (!waitingPlayer.getCreatures().contains(blocker)) {
            result.append(" ").append(blocker.getName()).append(" muere.");
        } else {
            result.append(" Ambas criaturas sobreviven.");
        }

        lastAction = result.toString();
        return lastAction;
    }

    public String castBuffSpell(SpellCard spellCard, CreatureCard target) {
        if (spellCard == null || target == null) {
            return "Debes seleccionar hechizo y objetivo.";
        }
        resolveSpell(spellCard, target);
        currentPlayer.getDeck().sendToGraveyard(spellCard);
        return lastAction;
    }

    private void resolveSpell(SpellCard spellCard, CreatureCard target) {
        switch (spellCard.getType()) {
            case DAMAGE -> waitingPlayer.receiveDamage(spellCard.getValue());
            case HEAL -> currentPlayer.heal(spellCard.getValue());
            case DRAW -> {
                for (int i = 0; i < spellCard.getDrawCount(); i++) {
                    Card draw = currentPlayer.drawCard();
                    if (draw == null) {
                        loseByFatigue(currentPlayer);
                        return;
                    }
                }
            }
            case BUFF -> {
                CreatureCard objective = target != null ? target : chooseDefaultBuffTarget();
                if (objective != null) {
                    objective.aplicarBuff(spellCard.getBuffAttack(), spellCard.getBuffLife());
                }
            }
        }

        updateWinnerByLife();
        if (gameFinished) {
            return;
        }

        lastAction = switch (spellCard.getType()) {
            case DAMAGE -> currentPlayer.getName() + " lanza " + spellCard.getName()
                    + " e inflige " + spellCard.getValue() + " de daño.";
            case HEAL -> currentPlayer.getName() + " lanza " + spellCard.getName()
                    + " y recupera " + spellCard.getValue() + " de vida.";
            case DRAW -> currentPlayer.getName() + " lanza " + spellCard.getName()
                    + " y roba " + spellCard.getDrawCount() + " cartas.";
            case BUFF -> currentPlayer.getName() + " lanza " + spellCard.getName()
                    + " para reforzar a una criatura aliada.";
        };
    }

    private CreatureCard chooseDefaultBuffTarget() {
        if (currentPlayer.getCreatures().isEmpty()) {
            return null;
        }
        return currentPlayer.getCreatures().get(0);
    }

    private boolean drawWithoutFatigueLoss(Player player) {
        Card drawnCard = player.drawCard();
        if (drawnCard == null) {
            loseByFatigue(player);
            return false;
        }
        return true;
    }

    private void moveDeadCreaturesToGraveyard() {
        List<CreatureCard> currentDead = new ArrayList<>();
        for (CreatureCard creature : currentPlayer.getCreatures()) {
            if (creature.estaMuerta()) {
                currentDead.add(creature);
            }
        }

        List<CreatureCard> waitingDead = new ArrayList<>();
        for (CreatureCard creature : waitingPlayer.getCreatures()) {
            if (creature.estaMuerta()) {
                waitingDead.add(creature);
            }
        }

        for (CreatureCard dead : currentDead) {
            currentPlayer.removeCreature(dead);
            currentPlayer.getDeck().sendToGraveyard(dead);
        }

        for (CreatureCard dead : waitingDead) {
            waitingPlayer.removeCreature(dead);
            waitingPlayer.getDeck().sendToGraveyard(dead);
        }
    }

    private void loseByFatigue(Player player) {
        gameFinished = true;
        winner = player == player1 ? player2 : player1;
        lastAction = "Fatiga magica: " + player.getName() + " intento robar con el mazo vacio.";
    }

    private void updateWinnerByLife() {
        if (player1.getLife() <= 0) {
            winner = player2;
            gameFinished = true;
            lastAction = player2.getName() + " gana porque " + player1.getName() + " ha caido a 0 vidas.";
        } else if (player2.getLife() <= 0) {
            winner = player1;
            gameFinished = true;
            lastAction = player1.getName() + " gana porque " + player2.getName() + " ha caido a 0 vidas.";
        }
    }
}
