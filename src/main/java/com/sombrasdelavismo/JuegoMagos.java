package com.sombrasdelavismo;

import com.sombrasdelavismo.model.Card;
import com.sombrasdelavismo.model.CardFactory;
import com.sombrasdelavismo.model.CreatureCard;
import com.sombrasdelavismo.model.Deck;
import com.sombrasdelavismo.model.Game;
import com.sombrasdelavismo.model.Player;
import com.sombrasdelavismo.model.SpellCard;
import com.sombrasdelavismo.model.SpellType;
import java.util.List;
import java.util.Scanner;

public class JuegoMagos {
    private final Scanner scanner;
    private Game game;
    private Tablero tablero;

    public JuegoMagos() {
        this.scanner = new Scanner(System.in);
    }

    public void iniciarJuego() {
        Player player1 = new Player(leerNombre("Nombre del Jugador 1", "Jugador 1"));
        Player player2 = new Player(leerNombre("Nombre del Jugador 2", "Jugador 2"));

        player1.setDeck(crearMazoJugador());
        player2.setDeck(crearMazoJugador());

        this.game = new Game(player1, player2);
        this.tablero = new Tablero(player1, player2);

        game.startGame();

        while (!game.isGameFinished()) {
            turno();
            if (!game.isGameFinished()) {
                game.nextTurn();
            }
        }

        tablero.mostrarTablero(game);
        System.out.println("Partida finalizada.");
    }

    public void turno() {
        boolean endTurn = false;

        while (!endTurn && !game.isGameFinished()) {
            mostrarTablero();
            mostrarMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> ejecutarInvocacion();
                case "2" -> ejecutarAtaque();
                case "3" -> mostrarTableroYEsperar();
                case "4" -> mostrarCementerios();
                case "5" -> {
                    game.setLastAction(game.getCurrentPlayer().getName() + " termina su turno.");
                    endTurn = true;
                }
                default -> pausa("Opcion no valida.");
            }
        }
    }

    public void mostrarTablero() {
        tablero.mostrarTablero(game);
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("1. Invocar criatura / lanzar hechizo");
        System.out.println("2. Atacar");
        System.out.println("3. Refrescar tablero");
        System.out.println("4. Ver cementerios");
        System.out.println("5. Pasar turno");
        System.out.print("Elige una accion: ");
    }

    private void ejecutarInvocacion() {
        List<Card> hand = game.getCurrentPlayer().getHand();
        if (hand.isEmpty()) {
            pausa("No tienes cartas en mano.");
            return;
        }

        System.out.print("Numero de carta a jugar: ");
        int index = leerIndice(hand.size());
        if (index < 0) {
            pausa("Seleccion cancelada.");
            return;
        }

        Card card = hand.get(index);
        if (!game.canPlaySelectedCard(card)) {
            pausa("No puedes jugar esa carta con tu mana actual.");
            return;
        }

        if (card instanceof SpellCard spellCard && spellCard.getType() == SpellType.BUFF) {
            if (game.getCurrentPlayer().getCreatures().isEmpty()) {
                pausa("No tienes criaturas aliadas para aplicar el buff.");
                return;
            }
            game.getCurrentPlayer().spendMana(spellCard.getCostMana());
            game.getCurrentPlayer().removeFromHand(spellCard);
            CreatureCard target = seleccionarCriaturaPropia("Selecciona la criatura que recibira el buff:");
            if (target == null) {
                game.getCurrentPlayer().getHand().add(spellCard);
                game.getCurrentPlayer().setCurrentMana(
                        game.getCurrentPlayer().getCurrentMana() + spellCard.getCostMana());
                pausa("Seleccion cancelada.");
                return;
            }
            String result = game.castBuffSpell(spellCard, target);
            game.setActionCounter(game.getActionCounter() + 1);
            pausa(result);
            return;
        }

        pausa(game.playCard(card));
    }

    private void ejecutarAtaque() {
        List<CreatureCard> attackers = game.getAvailableAttackers();
        if (attackers.isEmpty()) {
            pausa("No tienes criaturas listas para atacar.");
            return;
        }

        System.out.println("Selecciona atacante:");
        for (int i = 0; i < attackers.size(); i++) {
            CreatureCard attacker = attackers.get(i);
            System.out.println((i + 1) + ". " + attacker.getName() + " [" + attacker.getAttack() + "/" + attacker.getLife() + "]");
        }
        int attackerIndex = leerIndice(attackers.size());
        if (attackerIndex < 0) {
            pausa("Seleccion cancelada.");
            return;
        }

        CreatureCard attacker = attackers.get(attackerIndex);
        List<CreatureCard> blockers = game.getAvailableBlockers();
        if (blockers.isEmpty()) {
            pausa(game.attackDirectly(attacker));
            return;
        }

        System.out.println();
        System.out.println(game.getWaitingPlayer().getName() + ", ¿Con qué criatura quieres bloquear a "
                + attacker.getName() + "?");
        System.out.println("0. No bloquear");
        for (int i = 0; i < blockers.size(); i++) {
            CreatureCard blocker = blockers.get(i);
            System.out.println((i + 1) + ". " + blocker.getName() + " [" + blocker.getAttack() + "/" + blocker.getLife() + "]");
        }

        int blockerChoice = leerIndicePermitidoCero(blockers.size());
        if (blockerChoice == 0) {
            pausa(game.attackDirectly(attacker));
            return;
        }

        CreatureCard blocker = blockers.get(blockerChoice - 1);
        pausa(game.attackCreature(attacker, blocker));
    }

    private void mostrarTableroYEsperar() {
        mostrarTablero();
        System.out.println();
        System.out.print("Pulsa Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarCementerios() {
        System.out.println();
        System.out.println("Cementerio de " + game.getPlayer1().getName() + ": " + game.getPlayer1().getDeck().getGraveyard());
        System.out.println("Cementerio de " + game.getPlayer2().getName() + ": " + game.getPlayer2().getDeck().getGraveyard());
        System.out.print("Pulsa Enter para continuar...");
        scanner.nextLine();
    }

    private CreatureCard seleccionarCriaturaPropia(String message) {
        List<CreatureCard> creatures = game.getCurrentPlayer().getCreatures();
        System.out.println(message);
        for (int i = 0; i < creatures.size(); i++) {
            CreatureCard creature = creatures.get(i);
            System.out.println((i + 1) + ". " + creature.getName() + " [" + creature.getAttack() + "/" + creature.getLife() + "]");
        }
        int index = leerIndice(creatures.size());
        if (index < 0) {
            return null;
        }
        return creatures.get(index);
    }

    private Deck crearMazoJugador() {
        return CardFactory.createStandardDeck();
    }

    private String leerNombre(String label, String fallback) {
        System.out.print(label + " (" + fallback + " por defecto): ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? fallback : input;
    }

    private int leerIndice(int max) {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            if (value < 1 || value > max) {
                return -1;
            }
            return value - 1;
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private int leerIndicePermitidoCero(int max) {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            if (value < 0 || value > max) {
                return -1;
            }
            return value;
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void pausa(String message) {
        System.out.println();
        System.out.println(message);
        System.out.print("Pulsa Enter para continuar...");
        scanner.nextLine();
    }
}
