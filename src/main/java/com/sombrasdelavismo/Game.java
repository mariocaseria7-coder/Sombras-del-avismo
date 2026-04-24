package com.sombrasdelavismo;

public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    public void startGame() {
        // Draw initial hands
        for (int i = 0; i < 7; i++) {
            player1.drawCard();
            player2.drawCard();
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        currentPlayer.drawCard();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}