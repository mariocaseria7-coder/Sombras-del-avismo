package com.sombrasdelavismo;

public class Game {
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private int turnNumber;
    private String lastAction;
    private Player winner;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.turnNumber = 1;
        this.lastAction = "La partida va a comenzar.";
    }

    public void startGame() {
        player1.resetForNewGame();
        player2.resetForNewGame();
        player1.shuffleDeck();
        player2.shuffleDeck();

        for (int i = 0; i < 4; i++) {
            player1.drawCard();
            player2.drawCard();
        }
        currentPlayer.startTurn();
        lastAction = currentPlayer.getName() + " empieza la partida y roba una carta.";
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextTurn() {
        if (winner != null) {
            return;
        }
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        turnNumber++;
        currentPlayer.startTurn();
        lastAction = "Empieza el turno de " + currentPlayer.getName()
                + ". Roba carta y recupera su mana.";
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getWaitingPlayer() {
        return currentPlayer == player1 ? player2 : player1;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public String getLastAction() {
        return lastAction;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean canPlaySelectedCard(Card card) {
        return winner == null
                && card != null
                && currentPlayer.getHand().contains(card)
                && currentPlayer.canPlay(card);
    }

    public boolean canAttackWith(CreatureCard creatureCard) {
        return winner == null
                && creatureCard != null
                && currentPlayer.getBattlefield().contains(creatureCard)
                && creatureCard.isReadyToAttack();
    }

    public String playCard(Card card) {
        if (winner != null) {
            return "La partida ya ha terminado.";
        }
        if (card == null) {
            return "Selecciona una carta antes de jugar.";
        }
        if (!currentPlayer.getHand().contains(card)) {
            return "Solo puedes jugar cartas de tu mano.";
        }
        if (!currentPlayer.canPlay(card)) {
            return "No tienes mana suficiente para jugar " + card.getName() + ".";
        }
        if (!currentPlayer.spendMana(card.getCost())) {
            return "No se pudo gastar el mana necesario.";
        }

        currentPlayer.removeFromHand(card);
        if (card instanceof CreatureCard creatureCard) {
            creatureCard.markPlayedThisTurn();
            currentPlayer.addToBattlefield(creatureCard);
            card.play();
            lastAction = currentPlayer.getName() + " invoca a " + card.getName() + ".";
            return lastAction;
        }

        if (card instanceof SpellCard spellCard) {
            resolveSpell(spellCard);
            card.play();
            return lastAction;
        }

        return "La carta no tiene un comportamiento definido.";
    }

    public String attackWith(CreatureCard creatureCard) {
        if (winner != null) {
            return "La partida ya ha terminado.";
        }
        if (creatureCard == null) {
            return "Selecciona una criatura de tu mesa para atacar.";
        }
        if (!currentPlayer.getBattlefield().contains(creatureCard)) {
            return "Solo puedes atacar con criaturas de tu mesa.";
        }
        if (!creatureCard.isReadyToAttack()) {
            return creatureCard.getName() + " aun no puede atacar este turno.";
        }

        Player rival = getWaitingPlayer();
        rival.takeDamage(creatureCard.getPower());
        creatureCard.consumeAttack();
        updateWinner();
        if (winner != null) {
            return lastAction;
        }
        lastAction = currentPlayer.getName() + " ataca con " + creatureCard.getName()
                + " y hace " + creatureCard.getPower() + " de dano a " + rival.getName() + ".";
        return lastAction;
    }

    private void resolveSpell(SpellCard spellCard) {
        Player rival = getWaitingPlayer();
        int damage = spellCard.getDamage();
        int healing = spellCard.getHealing();
        int cardsToDraw = spellCard.getCardsToDraw();

        if (damage > 0) {
            rival.takeDamage(damage);
        }
        if (healing > 0) {
            currentPlayer.heal(healing);
        }
        for (int i = 0; i < cardsToDraw; i++) {
            currentPlayer.drawCard();
        }

        updateWinner();

        StringBuilder action = new StringBuilder(currentPlayer.getName())
                .append(" lanza ").append(spellCard.getName()).append(".");
        if (damage > 0) {
            action.append(" Hace ").append(damage).append(" de dano.");
        }
        if (healing > 0) {
            action.append(" Cura ").append(healing).append(" vidas.");
        }
        if (cardsToDraw > 0) {
            action.append(" Roba ").append(cardsToDraw).append(" carta");
            if (cardsToDraw > 1) {
                action.append("s");
            }
            action.append(".");
        }
        lastAction = action.toString();
    }

    private void updateWinner() {
        if (player1.getLife() <= 0) {
            winner = player2;
            lastAction = player2.getName() + " gana la partida.";
        } else if (player2.getLife() <= 0) {
            winner = player1;
            lastAction = player1.getName() + " gana la partida.";
        }
    }
}
