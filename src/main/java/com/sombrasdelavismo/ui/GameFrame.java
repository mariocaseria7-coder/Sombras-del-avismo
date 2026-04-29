package com.sombrasdelavismo.ui;

import com.sombrasdelavismo.model.ActionResult;
import com.sombrasdelavismo.model.Card;
import com.sombrasdelavismo.model.CardCatalog;
import com.sombrasdelavismo.model.CreatureCard;
import com.sombrasdelavismo.model.Game;
import com.sombrasdelavismo.model.Player;
import com.sombrasdelavismo.model.SpellCard;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class GameFrame extends JFrame {
    private static final Color BACKGROUND = new Color(16, 18, 27);
    private static final Color PANEL = new Color(26, 30, 44);
    private static final Color PANEL_ALT = new Color(20, 24, 36);
    private static final Color TEXT = new Color(240, 243, 248);
    private static final Color RED = new Color(182, 59, 59);
    private static final Color BLUE = new Color(52, 114, 214);
    private static final Color NEUTRAL = new Color(104, 118, 140);
    private static final CreatureCard BLOCKER_CANCELLED =
            new CreatureCard("BLOCKER_CANCELLED", "BLOCKER_CANCELLED", 0, 0, 0, "", null);
    private static final CreatureCard NO_BLOCK =
            new CreatureCard("NO_BLOCK", "NO_BLOCK", 0, 0, 0, "", null);

    private final JLabel turnLabel;
    private final JLabel phaseLabel;
    private final JLabel actionLabel;
    private final JLabel topPlayerLabel;
    private final JLabel bottomPlayerLabel;
    private final JPanel opponentHandPanel;
    private final JPanel opponentBoardPanel;
    private final JPanel playerHandPanel;
    private final JPanel playerBoardPanel;
    private final JTextArea logArea;
    private final JButton playButton;
    private final JButton attackPlayerButton;
    private final JButton attackCreatureButton;
    private final JButton endTurnButton;
    private final JButton graveyardButton;
    private final JButton newGameButton;

    private Game game;
    private Card selectedHandCard;
    private CreatureCard selectedAttacker;
    private CreatureCard selectedEnemyCreature;

    public GameFrame() {
        setTitle("Sombras del Abismo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1500, 940));

        turnLabel = createHeaderLabel(20);
        phaseLabel = createHeaderLabel(14);
        actionLabel = createHeaderLabel(13);
        topPlayerLabel = createInfoLabel();
        bottomPlayerLabel = createInfoLabel();

        opponentHandPanel = createCardStrip();
        opponentBoardPanel = createCardStrip();
        playerHandPanel = createCardStrip();
        playerBoardPanel = createCardStrip();
        logArea = createLogArea();

        playButton = createActionButton("Jugar carta");
        attackPlayerButton = createActionButton("Atacar rival");
        attackCreatureButton = createActionButton("Atacar criatura");
        endTurnButton = createActionButton("Terminar turno");
        graveyardButton = createActionButton("Ver cementerios");
        newGameButton = createActionButton("Nueva partida");

        setContentPane(buildContent());
        wireActions();
        startNewGame();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildContent() {
        JPanel root = new JPanel();
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        root.add(buildStatusPanel());
        root.add(Box.createVerticalStrut(12));
        root.add(buildPlayerSection("Jugador rival", topPlayerLabel, opponentHandPanel, opponentBoardPanel, false));
        root.add(Box.createVerticalStrut(12));
        root.add(buildCenterSection());
        root.add(Box.createVerticalStrut(12));
        root.add(buildPlayerSection("Tu lado", bottomPlayerLabel, playerHandPanel, playerBoardPanel, true));

        return root;
    }

    private JPanel buildStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 26)),
                new EmptyBorder(14, 16, 14, 16)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phaseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(turnLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(phaseLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionLabel);

        return panel;
    }

    private JPanel buildPlayerSection(
            String title,
            JLabel infoLabel,
            JPanel handPanel,
            JPanel boardPanel,
            boolean bottomSection) {
        JPanel section = new JPanel();
        section.setBackground(bottomSection ? PANEL : PANEL_ALT);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 26)),
                new EmptyBorder(14, 14, 14, 14)));
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(titleLabel);
        section.add(Box.createVerticalStrut(4));
        section.add(infoLabel);
        section.add(Box.createVerticalStrut(10));
        section.add(createSectionLabel(bottomSection ? "Mano activa" : "Mano del rival"));
        section.add(createScroller(handPanel, 190));
        section.add(Box.createVerticalStrut(10));
        section.add(createSectionLabel("Tablero"));
        section.add(createScroller(boardPanel, 250));

        return section;
    }

    private JPanel buildCenterSection() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(BACKGROUND);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setBackground(BACKGROUND);
        buttons.add(playButton);
        buttons.add(attackPlayerButton);
        buttons.add(attackCreatureButton);
        buttons.add(endTurnButton);
        buttons.add(graveyardButton);
        buttons.add(newGameButton);

        JScrollPane logScroller = new JScrollPane(logArea);
        logScroller.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 26)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        logScroller.getViewport().setBackground(PANEL);
        logScroller.setPreferredSize(new Dimension(200, 190));

        center.add(buttons, BorderLayout.NORTH);
        center.add(logScroller, BorderLayout.CENTER);
        return center;
    }

    private void wireActions() {
        playButton.addActionListener(event -> handlePlayCard());
        attackPlayerButton.addActionListener(event -> handleAttackPlayer());
        attackCreatureButton.addActionListener(event -> handleAttackCreature());
        endTurnButton.addActionListener(event -> handleEndTurn());
        graveyardButton.addActionListener(event -> showGraveyards());
        newGameButton.addActionListener(event -> startNewGame());
    }

    private void startNewGame() {
        String playerOneName = askName("Nombre del Jugador 1", "Jugador 1");
        String playerTwoName = askName("Nombre del Jugador 2", "Jugador 2");

        Player player1 = new Player(playerOneName);
        Player player2 = new Player(playerTwoName);
        player1.setDeckTemplate(CardCatalog.createDefaultDeck());
        player2.setDeckTemplate(CardCatalog.createDefaultDeck());

        game = new Game(player1, player2);
        selectedHandCard = null;
        selectedAttacker = null;
        selectedEnemyCreature = null;

        ActionResult result = game.startGame();
        refreshUi();
        showPassScreen("Empieza la partida.\n\nTurno de " + game.getCurrentPlayer().getName() + ".");
        applyResult(result, false);
    }

    private void handlePlayCard() {
        if (selectedHandCard == null) {
            showInfo("Selecciona primero una carta de tu mano.");
            return;
        }

        CreatureCard target = null;
        if (selectedHandCard instanceof SpellCard spellCard && spellCard.requiresFriendlyTarget()) {
            target = chooseFriendlyCreature("Elige la criatura que recibira el hechizo");
            if (target == null) {
                return;
            }
        }

        ActionResult result = game.playCard(selectedHandCard, target);
        applyResult(result, true);
    }

    private void handleAttackPlayer() {
        if (selectedAttacker == null) {
            showInfo("Selecciona una criatura de tu tablero para atacar.");
            return;
        }

        CreatureCard blocker = null;
        List<CreatureCard> blockers = game.getAvailableBlockers();
        if (!blockers.isEmpty()) {
            showPassScreen("El rival puede decidir si quiere bloquear este ataque.");
            blocker = chooseBlocker(blockers);
            if (blocker == BLOCKER_CANCELLED) {
                return;
            }
            if (blocker == NO_BLOCK) {
                blocker = null;
            }
        }

        ActionResult result = game.attackPlayer(selectedAttacker, blocker);
        applyResult(result, true);
    }

    private void handleAttackCreature() {
        if (selectedAttacker == null) {
            showInfo("Selecciona una criatura atacante de tu tablero.");
            return;
        }
        if (selectedEnemyCreature == null) {
            showInfo("Selecciona una criatura rival como objetivo.");
            return;
        }

        ActionResult result = game.attackCreature(selectedAttacker, selectedEnemyCreature);
        applyResult(result, true);
    }

    private void handleEndTurn() {
        ActionResult result = game.nextTurn();
        if (!result.successful()) {
            showInfo(result.publicMessage());
            return;
        }

        selectedHandCard = null;
        selectedAttacker = null;
        selectedEnemyCreature = null;
        refreshUi();
        showPassScreen("Cambia el turno.\n\nAhora juega " + game.getCurrentPlayer().getName() + ".");
        applyResult(result, false);
    }

    private void applyResult(ActionResult result, boolean clearSelections) {
        if (result == null) {
            return;
        }

        if (!result.successful()) {
            showInfo(result.publicMessage());
            return;
        }

        if (clearSelections) {
            selectedHandCard = null;
            selectedAttacker = null;
            selectedEnemyCreature = null;
        }

        if (result.privateMessage() != null && !result.privateMessage().isBlank()) {
            showPrivateInfo(result.privateMessage());
        }

        refreshUi();

        if (game.isFinished()) {
            showInfo("Partida terminada.\n\nGanador: " + game.getWinner().getName());
        }
    }

    private void refreshUi() {
        if (game == null) {
            return;
        }

        if (selectedHandCard != null && !game.getCurrentPlayer().getHand().contains(selectedHandCard)) {
            selectedHandCard = null;
        }
        if (selectedAttacker != null && !game.getCurrentPlayer().getBattlefield().contains(selectedAttacker)) {
            selectedAttacker = null;
        }
        if (selectedEnemyCreature != null && !game.getWaitingPlayer().getBattlefield().contains(selectedEnemyCreature)) {
            selectedEnemyCreature = null;
        }

        turnLabel.setText("Turno " + game.getTurnNumber() + "  |  Juega " + game.getCurrentPlayer().getName());
        phaseLabel.setText("Fase: " + game.getPhase() + "  |  Acciones del turno: " + game.getActionCounter());
        actionLabel.setText(game.getLastAction());

        topPlayerLabel.setText(buildPlayerInfo(game.getWaitingPlayer(), false));
        bottomPlayerLabel.setText(buildPlayerInfo(game.getCurrentPlayer(), true));

        rebuildOpponentHand();
        rebuildBoard(opponentBoardPanel, game.getWaitingPlayer().getBattlefield(), false);
        rebuildHand(playerHandPanel, game.getCurrentPlayer().getHand());
        rebuildBoard(playerBoardPanel, game.getCurrentPlayer().getBattlefield(), true);

        StringBuilder logBuilder = new StringBuilder();
        for (String entry : game.getLog()) {
            logBuilder.append("- ").append(entry).append("\n");
        }
        logArea.setText(logBuilder.toString());
        logArea.setCaretPosition(logArea.getDocument().getLength());

        playButton.setEnabled(!game.isFinished() && selectedHandCard != null);
        attackPlayerButton.setEnabled(!game.isFinished() && selectedAttacker != null);
        attackCreatureButton.setEnabled(!game.isFinished() && selectedAttacker != null && selectedEnemyCreature != null);
        endTurnButton.setEnabled(!game.isFinished());
    }

    private void rebuildOpponentHand() {
        opponentHandPanel.removeAll();

        if (game.getCurrentPlayer().isRevealOpponentHand()) {
            for (Card card : game.getWaitingPlayer().getHand()) {
                CardButton button = buildCardButton(card, false, false);
                button.setEnabled(false);
                opponentHandPanel.add(button);
            }
        } else {
            int handSize = game.getWaitingPlayer().getHand().size();
            for (int i = 0; i < handSize; i++) {
                CardButton hiddenButton = new CardButton(
                        "Carta rival",
                        -1,
                        "Oculta",
                        "No visible mientras no uses Scanner.",
                        "Reverso",
                        NEUTRAL,
                        null,
                        true);
                hiddenButton.setEnabled(false);
                opponentHandPanel.add(hiddenButton);
            }
        }

        opponentHandPanel.revalidate();
        opponentHandPanel.repaint();
    }

    private void rebuildHand(JPanel panel, List<Card> hand) {
        panel.removeAll();

        for (Card card : hand) {
            CardButton button = buildCardButton(card, false, true);
            button.setSelected(card == selectedHandCard);
            button.addActionListener(event -> {
                selectedHandCard = card;
                selectedAttacker = null;
                selectedEnemyCreature = null;
                refreshUi();
            });
            panel.add(button);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void rebuildBoard(JPanel panel, List<CreatureCard> creatures, boolean ownBoard) {
        panel.removeAll();

        for (CreatureCard creature : creatures) {
            boolean hidden = !ownBoard && creature.isHiddenFromOpponent();
            CardButton button = buildCardButton(creature, hidden, ownBoard);
            button.setSelected((ownBoard && creature == selectedAttacker) || (!ownBoard && creature == selectedEnemyCreature));
            button.addActionListener(event -> {
                selectedHandCard = null;
                if (ownBoard) {
                    selectedAttacker = creature;
                } else {
                    selectedEnemyCreature = creature;
                }
                refreshUi();
            });
            panel.add(button);
        }

        panel.revalidate();
        panel.repaint();
    }

    private CardButton buildCardButton(Card card, boolean hidden, boolean ownView) {
        Color accent = NEUTRAL;
        String footer;
        String typeLabel;
        String description;
        String title;
        int manaCost = hidden ? -1 : card.getManaCost();
        String imagePath = hidden ? null : card.getImagePath();

        if (hidden) {
            title = "Carta oculta";
            typeLabel = "Humo activo";
            description = "Tu rival no puede ver esta jugada todavia.";
            footer = "?? / ??";
            accent = NEUTRAL;
        } else if (card instanceof CreatureCard creature) {
            title = creature.getName();
            typeLabel = "Criatura";
            description = creature.getDescription();
            footer = creature.getAttack() + " / " + creature.getHealth() + "  |  " + creatureState(creature);
            accent = ownView ? BLUE : new Color(169, 84, 84);
        } else {
            SpellCard spell = (SpellCard) card;
            title = spell.getName();
            typeLabel = switch (spell.getColor()) {
                case RED -> "Hechizo rojo";
                case BLUE -> "Hechizo azul";
                case GRAY -> "Hechizo tactico";
            };
            description = spell.getDescription();
            footer = "Mana " + spell.getManaCost();
            accent = switch (spell.getColor()) {
                case RED -> RED;
                case BLUE -> BLUE;
                case GRAY -> NEUTRAL;
            };
        }

        return new CardButton(title, manaCost, typeLabel, description, footer, accent, imagePath, hidden);
    }

    private String creatureState(CreatureCard creature) {
        if (creature.isExhausted()) {
            return "Girado";
        }
        if (creature.hasSummoningSickness()) {
            return "Mareo";
        }
        return "Listo";
    }

    private String buildPlayerInfo(Player player, boolean currentPlayerView) {
        List<String> tags = new ArrayList<>();
        if (player.isSmokeUnlocked()) {
            tags.add("Humo desbloqueado");
        }
        if (player.isHideNextPlay() && currentPlayerView) {
            tags.add("Proxima carta oculta");
        }
        if (player.isAlliesProtected()) {
            tags.add("Simeone activo");
        }
        if (player.hasBathroomCrewOnBoard()) {
            tags.add("Combo Bano listo");
        }
        if (player.isRevealOpponentHand() && currentPlayerView) {
            tags.add("Scanner activo");
        }

        String extras = tags.isEmpty() ? "Sin estados extra" : String.join("  |  ", tags);
        return "<html><span style='color:#f0f3f8;'>Vida: " + player.getLife()
                + "  |  Mana: " + player.getCurrentMana() + "/" + player.getMaxMana()
                + "  |  Mazo: " + player.getDeck().size()
                + "  |  Cementerio: " + player.getDeck().getGraveyard().size()
                + "</span><br><span style='color:#b8c0d0;'>" + extras + "</span></html>";
    }

    private CreatureCard chooseFriendlyCreature(String title) {
        List<CreatureCard> creatures = new ArrayList<>(game.getCurrentPlayer().getBattlefield());
        if (creatures.isEmpty()) {
            showInfo("No tienes criaturas disponibles.");
            return null;
        }

        Object[] options = creatures.stream()
                .map(creature -> creature.getName() + " [" + creature.getAttack() + "/" + creature.getHealth() + "]")
                .toArray();

        Object selection = JOptionPane.showInputDialog(
                this,
                title,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (selection == null) {
            return null;
        }

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selection)) {
                return creatures.get(i);
            }
        }
        return null;
    }

    private CreatureCard chooseBlocker(List<CreatureCard> blockers) {
        List<Object> options = new ArrayList<>();
        options.add("No bloquear");
        for (CreatureCard creature : blockers) {
            options.add(creature.getName() + " [" + creature.getAttack() + "/" + creature.getHealth() + "]");
        }

        Object selection = JOptionPane.showInputDialog(
                this,
                "Defensa de " + game.getWaitingPlayer().getName(),
                "Defensa de " + game.getWaitingPlayer().getName(),
                JOptionPane.PLAIN_MESSAGE,
                null,
                options.toArray(),
                options.get(0));

        if (selection == null) {
            return BLOCKER_CANCELLED;
        }
        if ("No bloquear".equals(selection)) {
            return NO_BLOCK;
        }

        int selectedIndex = options.indexOf(selection) - 1;
        if (selectedIndex < 0 || selectedIndex >= blockers.size()) {
            return BLOCKER_CANCELLED;
        }
        return blockers.get(selectedIndex);
    }

    private void showGraveyards() {
        String message = buildGraveyardText(game.getPlayer1()) + "\n\n" + buildGraveyardText(game.getPlayer2());
        JOptionPane.showMessageDialog(this, message, "Cementerios", JOptionPane.INFORMATION_MESSAGE);
    }

    private String buildGraveyardText(Player player) {
        if (player.getDeck().getGraveyard().isEmpty()) {
            return player.getName() + ": cementerio vacio.";
        }

        StringBuilder builder = new StringBuilder(player.getName()).append(": ");
        for (Card card : player.getDeck().getGraveyard()) {
            builder.append(card.getName()).append(", ");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }

    private String askName(String title, String fallback) {
        String input = JOptionPane.showInputDialog(this, title, fallback);
        if (input == null || input.isBlank()) {
            return fallback;
        }
        return input.trim();
    }

    private void showPassScreen(String message) {
        JOptionPane.showMessageDialog(this, message, "Cambio de turno", JOptionPane.PLAIN_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Sombras del Abismo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPrivateInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informacion privada", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createCardStrip() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setBackground(PANEL_ALT);
        return panel;
    }

    private JScrollPane createScroller(JPanel panel, int height) {
        JScrollPane scroller = new JScrollPane(
                panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroller.setPreferredSize(new Dimension(100, height));
        scroller.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        scroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20)));
        scroller.getViewport().setBackground(PANEL_ALT);
        return scroller;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(186, 195, 214));
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));
        return label;
    }

    private JTextArea createLogArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setBackground(PANEL);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        return area;
    }

    private JButton createActionButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(TEXT);
        button.setBackground(new Color(43, 50, 71));
        return button;
    }

    private JLabel createHeaderLabel(int fontSize) {
        JLabel label = new JLabel();
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        return label;
    }

    private JLabel createInfoLabel() {
        JLabel label = new JLabel();
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }
}
