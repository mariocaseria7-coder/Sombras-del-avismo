package com.sombrasdelavismo;

import com.sombrasdelavismo.model.Card;
import com.sombrasdelavismo.model.CreatureCard;
import com.sombrasdelavismo.model.Game;
import com.sombrasdelavismo.model.Player;

public class Tablero {
    private static final String RESET = "\u001B[0m";
    private static final String PLAYER_ONE = "\u001B[36m";
    private static final String PLAYER_TWO = "\u001B[35m";
    private static final String ALERT = "\u001B[33m";
    private static final String DANGER = "\u001B[31m";
    private static final String TITLE = "\u001B[97m";

    private final Player player1;
    private final Player player2;

    public Tablero(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void mostrarTablero(Game game) {
        clear();
        System.out.println(TITLE + "==================== SOMBRAS DEL ABISMO ====================" + RESET);
        System.out.println(ALERT + "Turno: " + game.getTurnNumber()
                + " | Fase: " + game.getPhase()
                + " | Jugador activo: " + colorJugador(game, game.getCurrentPlayer())
                + game.getCurrentPlayer().getName() + RESET
                + ALERT + " | Acciones: " + game.getActionCounter() + RESET);
        System.out.println(ALERT + "Ultima accion: " + game.getLastAction() + RESET);
        System.out.println();

        imprimirJugador(game, player2, true);
        System.out.println("------------------------------------------------------------");
        imprimirJugador(game, player1, false);

        if (game.isGameFinished()) {
            String reason = game.getLastAction().toLowerCase().contains("fatiga")
                    ? "Victoria por fatiga."
                    : "Victoria por vidas.";
            System.out.println();
            System.out.println(DANGER + "ALERTA: " + game.getWinner().getName() + " gana la partida. " + reason + RESET);
        }
    }

    private void imprimirJugador(Game game, Player player, boolean superior) {
        String color = colorJugador(game, player);
        String indicador = superior ? "[RIVAL]" : "[TU LADO]";
        System.out.println(color + indicador + " " + player.getName() + RESET
                + "  Vida: " + barraVida(player.getLife())
                + "  Mana: " + barraMana(player.getCurrentMana(), player.getMaxMana())
                + "  Mazo: " + player.getDeck().size()
                + "  Cementerio: " + player.getDeck().graveyardSize());
        System.out.println("Criaturas:");
        if (player.getCreatures().isEmpty()) {
            System.out.println("  - Ninguna");
        } else {
            for (int i = 0; i < player.getCreatures().size(); i++) {
                CreatureCard creature = player.getCreatures().get(i);
                System.out.println("  " + (i + 1) + ". " + formatearCriatura(creature));
            }
        }

        if (!superior) {
            System.out.println("Mano:");
            if (player.getHand().isEmpty()) {
                System.out.println("  - Sin cartas");
            } else {
                for (int i = 0; i < player.getHand().size(); i++) {
                    Card card = player.getHand().get(i);
                    System.out.println("  " + (i + 1) + ". " + formatearCarta(card));
                }
            }
        } else {
            System.out.println("Mano rival: " + player.getHand().size() + " cartas");
        }
    }

    private String colorJugador(Game game, Player player) {
        return player == game.getPlayer1() ? PLAYER_ONE : PLAYER_TWO;
    }

    private String formatearCarta(Card card) {
        return card.toString() + " -> " + card.getDescription();
    }

    private String formatearCriatura(CreatureCard creature) {
        String estado;
        if (creature.isTapped()) {
            estado = "GIRADA";
        } else if (creature.isSummoningSickness()) {
            estado = "MAREO";
        } else if (creature.isCanAttack()) {
            estado = "LISTA";
        } else {
            estado = "EN ESPERA";
        }
        return creature.getName() + " [" + creature.getAttack() + "/" + creature.getLife() + "] " + estado;
    }

    private String barraMana(int manaActual, int manaMaximo) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            builder.append(i < manaActual ? "|" : ".");
        }
        builder.append("] ").append(manaActual).append("/").append(manaMaximo);
        return builder.toString();
    }

    private String barraVida(int life) {
        int visible = Math.max(0, Math.min(life, 20));
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < 20; i++) {
            builder.append(i < visible ? "#" : ".");
        }
        builder.append("] ").append(life);
        return builder.toString();
    }

    private void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
