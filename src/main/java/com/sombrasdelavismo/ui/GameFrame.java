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
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
    private static final Color BACKGROUND = new Color(12, 15, 24);
    private static final Color PANEL = new Color(26, 31, 45);
    private static final Color PANEL_ALT = new Color(20, 25, 36);
    private static final Color PANEL_SOFT = new Color(32, 39, 56);
    private static final Color TEXT = new Color(241, 244, 249);
    private static final Color MUTED_TEXT = new Color(183, 192, 210);
    private static final Color RED = new Color(197, 79, 79);
    private static final Color BLUE = new Color(67, 139, 234);
    private static final Color NEUTRAL = new Color(117, 132, 160);
    private static final Dimension HAND_CARD_SIZE = new Dimension(208, 286);
    private static final Dimension BOARD_CARD_SIZE = new Dimension(162, 228);
    private static final Dimension PREVIEW_CARD_SIZE = new Dimension(320, 470);
    private static final Dimension INSPECTOR_CARD_SIZE = new Dimension(390, 575);
    private static final CreatureCard BLOCKER_CANCELLED =
            new CreatureCard("BLOCKER_CANCELLED", "BLOCKER_CANCELLED", 0, 0, 0, "", null);
    private static final CreatureCard NO_BLOCK =
            new CreatureCard("NO_BLOCK", "NO_BLOCK", 0, 0, 0, "", null);

    private final JLabel turnLabel;
    private final JLabel phaseLabel;
    private final JTextArea actionLabel;
    private final JLabel topPlayerLabel;
    private final JLabel bottomPlayerLabel;
    private final JLabel previewTitleLabel;
    private final JLabel previewMetaLabel;
    private final JPanel previewCardHolder;
    private final JPanel opponentHandPanel;
    private final JPanel opponentBoardPanel;
    private final JPanel playerHandPanel;
    private final JPanel playerBoardPanel;
    private final JTextArea previewDescriptionArea;
    private final JTextArea guideArea;
    private final JTextArea logArea;
    private final JButton playButton;
    private final JButton attackPlayerButton;
    private final JButton attackCreatureButton;
    private final JButton endTurnButton;
    private final JButton graveyardButton;
    private final JButton newGameButton;
    private final JButton viewCardButton;
    private final JButton helpButton;

    private Game game;
    private Card selectedHandCard;
    private CreatureCard selectedAttacker;
    private CreatureCard selectedEnemyCreature;

    public GameFrame() {
        setTitle("Sombras del Abismo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1560, 940));
        setSize(1760, 1020);

        turnLabel = createHeaderLabel(30);
        phaseLabel = createHeaderLabel(16);
        actionLabel = createStatusArea();
        topPlayerLabel = createInfoLabel();
        bottomPlayerLabel = createInfoLabel();
        previewTitleLabel = createHeaderLabel(20);
        previewMetaLabel = createInfoLabel();

        opponentHandPanel = createCardStrip();
        opponentBoardPanel = createCardStrip();
        playerHandPanel = createCardStrip();
        playerBoardPanel = createCardStrip();
        previewCardHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        previewCardHolder.setOpaque(false);
        previewCardHolder.setBorder(new EmptyBorder(4, 0, 6, 0));
        previewCardHolder.setPreferredSize(new Dimension(PREVIEW_CARD_SIZE.width + 12, PREVIEW_CARD_SIZE.height + 12));

        previewDescriptionArea = createInfoArea(14);
        guideArea = createInfoArea(13);
        guideArea.setBackground(PANEL_ALT);
        logArea = createLogArea();

        playButton = createActionButton("Jugar carta");
        attackPlayerButton = createActionButton("Atacar rival");
        attackCreatureButton = createActionButton("Atacar criatura");
        endTurnButton = createActionButton("Terminar turno");
        graveyardButton = createActionButton("Ver cementerios");
        newGameButton = createActionButton("Nueva partida");
        viewCardButton = createActionButton("Ver carta");
        helpButton = createActionButton("Cómo jugar");

        setContentPane(buildContent());
        wireActions();
        startNewGame();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildBoardArea(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 12));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(420, 100));

        JPanel topStack = new JPanel();
        topStack.setOpaque(false);
        topStack.setLayout(new BoxLayout(topStack, BoxLayout.Y_AXIS));
        topStack.add(buildStatusPanel());
        topStack.add(Box.createVerticalStrut(12));
        topStack.add(buildActionPanel());

        sidebar.add(topStack, BorderLayout.NORTH);
        sidebar.add(buildLogPanel(), BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel buildStatusPanel() {
        JPanel panel = createPanelShell(PANEL);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phaseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.add(createSectionLabel("Estado de la partida"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(turnLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(phaseLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createSubsectionLabel("Resumen del turno"));
        panel.add(actionLabel);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel shell = createPanelShell(PANEL_ALT);
        shell.setLayout(new BorderLayout(0, 10));

        JPanel grid = new JPanel(new GridLayout(3, 2, 8, 8));
        grid.setOpaque(false);
        grid.add(playButton);
        grid.add(attackPlayerButton);
        grid.add(attackCreatureButton);
        grid.add(endTurnButton);
        grid.add(graveyardButton);
        grid.add(newGameButton);

        JScrollPane guideScroller = new JScrollPane(guideArea);
        guideScroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 14)));
        guideScroller.getViewport().setBackground(PANEL_ALT);
        guideScroller.setPreferredSize(new Dimension(100, 152));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JPanel utilityRow = new JPanel(new GridLayout(1, 2, 8, 0));
        utilityRow.setOpaque(false);
        utilityRow.add(viewCardButton);
        utilityRow.add(helpButton);

        content.add(grid);
        content.add(Box.createVerticalStrut(10));
        content.add(utilityRow);
        content.add(Box.createVerticalStrut(10));
        content.add(createSubsectionLabel("Guía rápida"));
        content.add(guideScroller);

        shell.add(createSectionLabel("Acciones"), BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildPreviewPanel() {
        JPanel shell = createPanelShell(PANEL_SOFT);
        shell.setLayout(new BorderLayout(0, 10));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createSectionLabel("Carta ampliada"));
        header.add(Box.createVerticalStrut(8));
        header.add(previewTitleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(previewMetaLabel);

        JScrollPane previewTextScroller = new JScrollPane(previewDescriptionArea);
        previewTextScroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 14)));
        previewTextScroller.getViewport().setBackground(PANEL_SOFT);
        previewTextScroller.setPreferredSize(new Dimension(100, 170));

        shell.add(header, BorderLayout.NORTH);
        shell.add(previewCardHolder, BorderLayout.CENTER);
        shell.add(previewTextScroller, BorderLayout.SOUTH);
        return shell;
    }

    private JPanel buildLogPanel() {
        JPanel shell = createPanelShell(PANEL);
        shell.setLayout(new BorderLayout(0, 10));

        JScrollPane logScroller = new JScrollPane(logArea);
        logScroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 18)));
        logScroller.getViewport().setBackground(PANEL);
        logScroller.setPreferredSize(new Dimension(100, 320));

        shell.add(createSectionLabel("Historial"), BorderLayout.NORTH);
        shell.add(logScroller, BorderLayout.CENTER);
        return shell;
    }

    private JScrollPane buildBoardArea() {
        JPanel boardColumn = new JPanel();
        boardColumn.setOpaque(false);
        boardColumn.setLayout(new BoxLayout(boardColumn, BoxLayout.Y_AXIS));
        boardColumn.add(buildHandSection("Jugador rival", topPlayerLabel, opponentHandPanel, "Mano rival", 308));
        boardColumn.add(Box.createVerticalStrut(14));
        boardColumn.add(buildBattlefieldSection());
        boardColumn.add(Box.createVerticalStrut(14));
        boardColumn.add(buildHandSection("Tu lado", bottomPlayerLabel, playerHandPanel, "Mano activa", 308));

        JScrollPane scroller = new JScrollPane(
                boardColumn,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(BorderFactory.createEmptyBorder());
        scroller.getViewport().setBackground(BACKGROUND);
        scroller.getVerticalScrollBar().setUnitIncrement(16);
        return scroller;
    }

    private JPanel buildHandSection(
            String title,
            JLabel infoLabel,
            JPanel handPanel,
            String rowLabel,
            int scrollerHeight) {
        JPanel section = createPanelShell(PANEL_ALT);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        JLabel titleLabel = createZoneTitle(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(titleLabel);
        section.add(Box.createVerticalStrut(4));
        section.add(infoLabel);
        section.add(Box.createVerticalStrut(12));
        section.add(createSubsectionLabel(rowLabel));
        section.add(createScroller(handPanel, scrollerHeight));
        return section;
    }

    private JPanel buildBattlefieldSection() {
        JPanel section = createPanelShell(PANEL);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        section.add(createZoneTitle("Tablero"));
        section.add(Box.createVerticalStrut(10));
        section.add(createSubsectionLabel("Campo rival"));
        section.add(createScroller(opponentBoardPanel, 236));
        section.add(Box.createVerticalStrut(12));
        section.add(createSubsectionLabel("Tu campo"));
        section.add(createScroller(playerBoardPanel, 236));
        return section;
    }

    private void wireActions() {
        playButton.addActionListener(event -> handlePlayCard());
        attackPlayerButton.addActionListener(event -> handleAttackPlayer());
        attackCreatureButton.addActionListener(event -> handleAttackCreature());
        endTurnButton.addActionListener(event -> handleEndTurn());
        graveyardButton.addActionListener(event -> showGraveyards());
        newGameButton.addActionListener(event -> startNewGame());
        viewCardButton.addActionListener(event -> handleViewCard());
        helpButton.addActionListener(event -> showHowToPlay());
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
            target = chooseFriendlyCreature("Elige la criatura que recibirá el hechizo");
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

    private void handleViewCard() {
        Card card = currentPreviewCard();
        if (card == null) {
            showInfo("Selecciona una carta o criatura para verla en grande.");
            return;
        }

        showCardInspector(card, isPreviewHidden(card), isPreviewOwnView(card));
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

        turnLabel.setText("Turno " + game.getTurnNumber());
        phaseLabel.setText("Juega: " + game.getCurrentPlayer().getName());
        actionLabel.setText("Fase: " + game.getPhase()
                + "\nAcciones usadas: " + game.getActionCounter()
                + "\n\nÚltima acción:\n" + game.getLastAction());
        actionLabel.setCaretPosition(0);

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

        String playRestriction = selectedHandCard == null ? "Selecciona una carta de tu mano." : game.explainWhyCannotPlay(selectedHandCard);
        String attackRestriction = selectedAttacker == null ? "Selecciona una criatura de tu campo." : game.explainWhyCannotAttack(selectedAttacker);
        String attackCreatureRestriction = buildAttackCreatureRestriction();

        playButton.setEnabled(!game.isFinished() && selectedHandCard != null && playRestriction == null);
        attackPlayerButton.setEnabled(!game.isFinished() && selectedAttacker != null && attackRestriction == null);
        attackCreatureButton.setEnabled(!game.isFinished() && attackCreatureRestriction == null);
        endTurnButton.setEnabled(!game.isFinished());
        viewCardButton.setEnabled(currentPreviewCard() != null);
        helpButton.setEnabled(true);

        playButton.setToolTipText(playRestriction == null ? "Juega la carta seleccionada." : playRestriction);
        attackPlayerButton.setToolTipText(
                attackRestriction == null ? "Ataca directamente al rival con la criatura seleccionada." : attackRestriction);
        attackCreatureButton.setToolTipText(
                attackCreatureRestriction == null
                        ? "Haz que tu criatura combata contra la criatura rival seleccionada."
                        : attackCreatureRestriction);
        endTurnButton.setToolTipText("Termina tu turno y cede el juego al rival.");
        graveyardButton.setToolTipText("Muestra las cartas derrotadas de ambos jugadores.");
        newGameButton.setToolTipText("Reinicia la partida desde cero.");
        viewCardButton.setToolTipText(currentPreviewCard() == null
                ? "Selecciona una carta o criatura para ampliarla."
                : "Abre la carta seleccionada en grande.");
        helpButton.setToolTipText("Abre una guía corta para aprender a jugar.");

        guideArea.setText(buildGuideText(playRestriction, attackRestriction, attackCreatureRestriction));
        guideArea.setCaretPosition(0);
    }

    private void rebuildOpponentHand() {
        opponentHandPanel.removeAll();

        if (game.getCurrentPlayer().isRevealOpponentHand()) {
            for (Card card : game.getWaitingPlayer().getHand()) {
                CardButton button = buildCardButton(card, false, false, HAND_CARD_SIZE);
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
                        true,
                        HAND_CARD_SIZE);
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
            CardButton button = buildCardButton(card, false, true, HAND_CARD_SIZE);
            button.setSelected(card == selectedHandCard);
            enhanceHandTooltip(button, card);
            button.addActionListener(event -> {
                selectedHandCard = card;
                selectedAttacker = null;
                selectedEnemyCreature = null;
                refreshUi();
            });
            attachInspector(button, card, false, true);
            panel.add(button);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void rebuildBoard(JPanel panel, List<CreatureCard> creatures, boolean ownBoard) {
        panel.removeAll();

        for (CreatureCard creature : creatures) {
            boolean hidden = !ownBoard && creature.isHiddenFromOpponent();
            CardButton button = buildCardButton(creature, hidden, ownBoard, BOARD_CARD_SIZE);
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
            attachInspector(button, creature, hidden, ownBoard);
            panel.add(button);
        }

        panel.revalidate();
        panel.repaint();
    }

    private CardButton buildCardButton(Card card, boolean hidden, boolean ownView, Dimension size) {
        CardViewData view = buildCardViewData(card, hidden, ownView);
        return new CardButton(
                view.title(),
                view.manaCost(),
                view.typeLabel(),
                view.description(),
                view.footer(),
                view.accent(),
                view.imagePath(),
                view.hidden(),
                size);
    }

    private CardViewData buildCardViewData(Card card, boolean hidden, boolean ownView) {
        if (hidden) {
            return new CardViewData(
                    "Carta oculta",
                    -1,
                    "Humo activo",
                    "Tu rival no puede ver esta jugada todavía.",
                    "?? / ??",
                    NEUTRAL,
                    null,
                    true);
        }

        if (card instanceof CreatureCard creature) {
            return new CardViewData(
                    creature.getName(),
                    creature.getManaCost(),
                    "Criatura",
                    creature.getDescription(),
                    creature.getAttack() + " / " + creature.getHealth() + "  |  " + creatureState(creature),
                    ownView ? BLUE : new Color(177, 88, 88),
                    creature.getImagePath(),
                    false);
        }

        SpellCard spell = (SpellCard) card;
        Color accent = switch (spell.getColor()) {
            case RED -> RED;
            case BLUE -> BLUE;
            case GRAY -> NEUTRAL;
        };
        String typeLabel = switch (spell.getColor()) {
            case RED -> "Hechizo rojo";
            case BLUE -> "Hechizo azul";
            case GRAY -> "Hechizo táctico";
        };

        return new CardViewData(
                spell.getName(),
                spell.getManaCost(),
                typeLabel,
                spell.getDescription(),
                "Maná " + spell.getManaCost(),
                accent,
                spell.getImagePath(),
                false);
    }

    private void enhanceHandTooltip(CardButton button, Card card) {
        if (game == null || card == null) {
            return;
        }

        String restriction = game.explainWhyCannotPlay(card);
        if (restriction == null) {
            return;
        }

        button.setToolTipText("<html><div style='width:280px'><b>" + card.getName()
                + "</b><br><br>No disponible ahora:<br>" + restriction
                + "<br><br><i>Selecciona la carta y pulsa Ver carta para ampliarla.</i></div></html>");
    }

    private void attachInspector(CardButton button, Card card, boolean hidden, boolean ownView) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
                    showCardInspector(card, hidden, ownView);
                }
            }
        });
    }

    private void refreshPreview() {
        Card previewCard = currentPreviewCard();
        boolean hidden = isPreviewHidden(previewCard);
        boolean ownView = isPreviewOwnView(previewCard);

        previewCardHolder.removeAll();

        if (previewCard == null) {
            previewTitleLabel.setText("Sin seleccion");
            previewMetaLabel.setText("Selecciona una carta de la mano o del tablero para verla en grande.");
            previewDescriptionArea.setText(
                    "La vista ampliada muestra mejor el texto, el coste y el estado de la carta seleccionada.");

            JLabel placeholder = new JLabel(
                    "<html><div style='text-align:center;color:#d8e0ee;'>Selecciona una carta<br>para ampliarla aqui.</div></html>",
                    SwingConstants.CENTER);
            placeholder.setForeground(TEXT);
            placeholder.setFont(new Font("SansSerif", Font.BOLD, 18));
            placeholder.setPreferredSize(PREVIEW_CARD_SIZE);
            previewCardHolder.add(placeholder);
        } else {
            CardViewData view = buildCardViewData(previewCard, hidden, ownView);
            CardButton previewCardButton = new CardButton(
                    view.title(),
                    view.manaCost(),
                    view.typeLabel(),
                    view.description(),
                    view.footer(),
                    view.accent(),
                    view.imagePath(),
                    view.hidden(),
                    PREVIEW_CARD_SIZE);
            previewCardButton.setFocusable(false);
            previewCardButton.setSelected(true);
            previewCardHolder.add(previewCardButton);

            previewTitleLabel.setText(view.title());
            previewMetaLabel.setText(buildPreviewMeta(previewCard, view));
            previewDescriptionArea.setText(buildPreviewNarrative(previewCard, hidden, view));
        }

        previewCardHolder.revalidate();
        previewCardHolder.repaint();
    }

    private Card currentPreviewCard() {
        if (selectedHandCard != null) {
            return selectedHandCard;
        }
        if (selectedAttacker != null) {
            return selectedAttacker;
        }
        return selectedEnemyCreature;
    }

    private boolean isPreviewHidden(Card card) {
        if (!(card instanceof CreatureCard creature)) {
            return false;
        }
        return card == selectedEnemyCreature && creature.isHiddenFromOpponent();
    }

    private boolean isPreviewOwnView(Card card) {
        return card != null && (card == selectedHandCard || card == selectedAttacker);
    }

    private String buildPreviewMeta(Card card, CardViewData view) {
        String selectionLabel;
        if (card == selectedHandCard) {
            selectionLabel = game.explainWhyCannotPlay(selectedHandCard) == null
                    ? "Lista para jugar"
                    : "No disponible ahora";
        } else if (card == selectedAttacker) {
            selectionLabel = game.explainWhyCannotAttack(selectedAttacker) == null
                    ? "Atacante seleccionado"
                    : "Atacante bloqueado";
        } else if (card == selectedEnemyCreature) {
            selectionLabel = "Objetivo seleccionado";
        } else {
            selectionLabel = "Vista previa";
        }
        return selectionLabel + "  |  " + view.typeLabel() + "  |  " + view.footer();
    }

    private String buildPreviewNarrative(Card card, boolean hidden, CardViewData view) {
        if (hidden) {
            return "Esta carta sigue oculta por el efecto de Humo.\n\n"
                    + "Podrás descubrirla cuando se revele en el combate o cuando el rival la muestre.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(view.description());

        if (card instanceof CreatureCard creature) {
            builder.append("\n\nAtaque actual: ")
                    .append(creature.getAttack())
                    .append("\nVida actual: ")
                    .append(creature.getHealth())
                    .append("\nEstado: ")
                    .append(creatureState(creature));
            if (card == selectedAttacker) {
                String reason = game.explainWhyCannotAttack(creature);
                builder.append("\nPuede atacar: ").append(reason == null ? "Sí" : "No");
                if (reason != null) {
                    builder.append("\nMotivo: ").append(reason);
                }
            }
        } else if (card instanceof SpellCard spell) {
            builder.append("\n\nCoste de maná: ")
                    .append(spell.getManaCost())
                    .append("\nColor del hechizo: ")
                    .append(view.typeLabel());
        }

        if (card == selectedHandCard) {
            String reason = game.explainWhyCannotPlay(selectedHandCard);
            builder.append("\n\nEstado para jugar: ").append(reason == null ? "Disponible" : "Bloqueada");
            if (reason != null) {
                builder.append("\nMotivo: ").append(reason);
            }
        }

        return builder.toString();
    }

    private String buildAttackCreatureRestriction() {
        if (game == null || game.isFinished()) {
            return "La partida ya terminó.";
        }
        if (selectedAttacker == null) {
            return "Selecciona primero una criatura de tu campo.";
        }

        String attackRestriction = game.explainWhyCannotAttack(selectedAttacker);
        if (attackRestriction != null) {
            return attackRestriction;
        }
        if (selectedEnemyCreature == null) {
            return "Selecciona una criatura rival como objetivo.";
        }
        if (!game.getWaitingPlayer().getBattlefield().contains(selectedEnemyCreature)) {
            return "El objetivo rival ya no está en el tablero.";
        }
        return null;
    }

    private String buildGuideText(
            String playRestriction,
            String attackRestriction,
            String attackCreatureRestriction) {
        StringBuilder guide = new StringBuilder();
        guide.append("1. Mira tu maná y selecciona una carta o criatura.\n");
        guide.append("2. Usa los botones de arriba para jugar o atacar.\n");
        guide.append("3. Pulsa Ver carta para abrir la selección en grande.\n\n");

        if (selectedHandCard != null) {
            guide.append("Carta seleccionada: ").append(selectedHandCard.getName()).append("\n");
            guide.append(selectedHandCard.getDescription()).append("\n");
            if (playRestriction == null) {
                guide.append("Puedes jugarla ahora mismo.");
                if (selectedHandCard instanceof CreatureCard) {
                    guide.append(" Entrará con mareo y no atacará hasta tu siguiente turno.");
                } else if (selectedHandCard instanceof SpellCard spell && spell.requiresFriendlyTarget()) {
                    guide.append(" Al pulsar Jugar carta te pedirá una criatura propia como objetivo.");
                }
            } else {
                guide.append("No puedes jugarla ahora: ").append(playRestriction);
            }
            return guide.toString();
        }

        if (selectedAttacker != null) {
            guide.append("Atacante seleccionado: ").append(selectedAttacker.getName()).append("\n");
            guide.append(selectedAttacker.getDescription()).append("\n");
            if (attackRestriction == null) {
                guide.append("Puede atacar. ");
                if (selectedEnemyCreature == null) {
                    guide.append("Elige Atacar rival o selecciona una criatura enemiga para usar Atacar criatura.");
                } else {
                    guide.append("Ya tienes objetivo rival. Puedes usar Atacar criatura.");
                }
            } else {
                guide.append("No puede atacar: ").append(attackRestriction);
            }
            return guide.toString();
        }

        if (selectedEnemyCreature != null) {
            guide.append("Objetivo rival marcado: ").append(selectedEnemyCreature.getName()).append("\n");
            guide.append(selectedEnemyCreature.getDescription()).append("\n");
            guide.append("Ahora selecciona una criatura de tu campo preparada para atacar.\n");
            guide.append("Si el botón de ataque sigue bloqueado: ").append(attackCreatureRestriction);
            return guide.toString();
        }

        guide.append("Empieza seleccionando una carta de tu mano o una criatura de tu campo.\n");
        guide.append("Si un botón queda bloqueado, este panel te dirá exactamente el motivo.\n");
        guide.append("Pista: las criaturas con Mareo no pueden atacar en el turno en que entran.");
        return guide.toString();
    }

    private void showHowToPlay() {
        JTextArea helpArea = new JTextArea();
        helpArea.setEditable(false);
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpArea.setForeground(TEXT);
        helpArea.setBackground(PANEL);
        helpArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        helpArea.setBorder(new EmptyBorder(14, 14, 14, 14));
        helpArea.setText("""
                Flujo básico del turno:
                1. Empiezas el turno robando carta y recargando el maná.
                2. Selecciona una carta de tu mano.
                3. Si el botón Jugar carta se activa, puedes bajarla.
                4. Las criaturas entran con Mareo y no pueden atacar hasta tu siguiente turno.
                5. Para atacar, selecciona una criatura de tu campo que no tenga Mareo ni esté Girada.
                6. Usa Atacar rival o selecciona antes una criatura enemiga para usar Atacar criatura.

                Motivos habituales por los que una carta no se puede jugar:
                - Falta maná.
                - Tu tablero ya tiene el máximo de 7 criaturas.
                - El hechizo necesita una criatura propia en mesa.
                - Humo sigue bloqueado hasta jugar Baño con Adrian, Fabio y Fernando en tu campo.

                Consejos de interfaz:
                - Mira la Guía rápida de la izquierda: te dirá por qué un botón está bloqueado.
                - Pasa el raton por una carta para ver su texto completo.
                - Selecciona una carta y pulsa Ver carta para abrirla en grande.
                - El texto Mareo significa que la criatura acaba de entrar y aún no puede atacar.
                """);

        JScrollPane scroller = new JScrollPane(helpArea);
        scroller.setPreferredSize(new Dimension(640, 420));
        scroller.setBorder(BorderFactory.createEmptyBorder());
        scroller.getViewport().setBackground(PANEL);
        JOptionPane.showMessageDialog(this, scroller, "Cómo jugar", JOptionPane.PLAIN_MESSAGE);
    }

    private void showCardInspector(Card card, boolean hidden, boolean ownView) {
        if (card == null) {
            return;
        }

        CardViewData view = buildCardViewData(card, hidden, ownView);
        Component inspectorVisual = buildInspectorVisual(view);

        JTextArea inspectorText = createInfoArea(15);
        inspectorText.setBackground(PANEL);
        inspectorText.setBorder(new EmptyBorder(14, 14, 14, 14));
        inspectorText.setText(buildPreviewNarrative(card, hidden, view) + "\n\n" + buildInspectorNotes(card, hidden));

        JScrollPane inspectorScroller = new JScrollPane(inspectorText);
        inspectorScroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 18)));
        inspectorScroller.getViewport().setBackground(PANEL);
        inspectorScroller.setPreferredSize(new Dimension(330, 690));

        JPanel visualColumn = new JPanel(new BorderLayout());
        visualColumn.setOpaque(false);
        visualColumn.add(inspectorVisual, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(16, 12));
        content.setBackground(PANEL_SOFT);
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setPreferredSize(new Dimension(980, 760));

        JLabel header = new JLabel(view.title() + "  |  " + buildPreviewMeta(card, view));
        header.setForeground(TEXT);
        header.setFont(new Font("Serif", Font.BOLD, 20));

        content.add(header, BorderLayout.NORTH);
        content.add(visualColumn, BorderLayout.WEST);
        content.add(inspectorScroller, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, content, "Inspector de carta", JOptionPane.PLAIN_MESSAGE);
    }

    private Component buildInspectorVisual(CardViewData view) {
        JLabel artworkLabel = buildInspectorArtwork(view.imagePath(), view.hidden());
        if (artworkLabel != null) {
            return artworkLabel;
        }

        CardButton inspectorCard = new CardButton(
                view.title(),
                view.manaCost(),
                view.typeLabel(),
                view.description(),
                view.footer(),
                view.accent(),
                view.imagePath(),
                view.hidden(),
                INSPECTOR_CARD_SIZE);
        inspectorCard.setFocusable(false);
        inspectorCard.setSelected(true);
        return inspectorCard;
    }

    private JLabel buildInspectorArtwork(String imagePath, boolean hidden) {
        if (hidden) {
            return null;
        }

        BufferedImage image = loadInspectorImage(imagePath);
        if (image == null) {
            return null;
        }

        Dimension scaledSize = scaleToFit(image.getWidth(), image.getHeight(), 470, 690);
        BufferedImage scaledImage = scaleInspectorImage(image, scaledSize.width, scaledSize.height);

        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setOpaque(true);
        label.setBackground(new Color(9, 12, 19));
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 18)),
                new EmptyBorder(10, 10, 10, 10)));
        label.setPreferredSize(new Dimension(scaledSize.width + 22, scaledSize.height + 22));
        return label;
    }

    private BufferedImage loadInspectorImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        try (InputStream stream = GameFrame.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (stream == null) {
                return null;
            }
            return ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private Dimension scaleToFit(int width, int height, int maxWidth, int maxHeight) {
        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        ratio = Math.min(ratio, 1.0d);
        return new Dimension((int) Math.round(width * ratio), (int) Math.round(height * ratio));
    }

    private BufferedImage scaleInspectorImage(BufferedImage source, int targetWidth, int targetHeight) {
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return scaled;
    }

    private String buildInspectorNotes(Card card, boolean hidden) {
        if (hidden) {
            return "La carta sigue oculta para el rival mientras dure el efecto de Humo.";
        }
        if (card instanceof CreatureCard creature) {
            return "Notas tácticas:\n"
                    + "- Las criaturas entran con Mareo.\n"
                    + "- Si quedan Giradas, tendrás que esperar a tu siguiente turno para volver a atacar.\n"
                    + "- Estado actual de " + creature.getName() + ": " + creatureState(creature) + ".";
        }
        if (card instanceof SpellCard spell) {
            StringBuilder notes = new StringBuilder("Notas tácticas:\n");
            if (spell.requiresFriendlyTarget()) {
                notes.append("- Este hechizo necesita una criatura propia como objetivo.\n");
            }
            switch (spell.getEffect()) {
                case SMOKE -> notes.append("- Humo oculta la siguiente carta que juegues.\n");
                case UNLOCK_SMOKE -> notes.append("- Baño solo funciona con Adrian, Fabio y Fernando en mesa.\n");
                case SUMMON_LIN, SUMMON_MONO_A -> notes.append("- Este hechizo invoca una criatura, así que necesita hueco en tu tablero.\n");
                default -> notes.append("- Revisa el texto de la carta para ver cuándo conviene usarla.\n");
            }
            return notes.toString();
        }
        return "";
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
            tags.add("Próxima carta oculta");
        }
        if (player.isAlliesProtected()) {
            tags.add("Simeone activo");
        }
        if (player.hasBathroomCrewOnBoard()) {
            tags.add("Combo Baño listo");
        }
        if (player.isRevealOpponentHand() && currentPlayerView) {
            tags.add("Scanner activo");
        }

        String extras = tags.isEmpty() ? "Sin estados extra" : String.join("  |  ", tags);
        return "<html><span style='color:#f0f3f8;'>Vida: " + player.getLife()
                + "  |  Maná: " + player.getCurrentMana() + "/" + player.getMaxMana()
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
            return player.getName() + ": cementerio vacío.";
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
        JOptionPane.showMessageDialog(this, message, "Información privada", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createCardStrip() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panel.setBackground(PANEL_ALT);
        panel.setBorder(new EmptyBorder(4, 4, 4, 4));
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
        scroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 18)));
        scroller.getViewport().setBackground(PANEL_ALT);
        scroller.getHorizontalScrollBar().setUnitIncrement(18);
        return scroller;
    }

    private JPanel createPanelShell(Color background) {
        JPanel panel = new JPanel();
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 18)),
                new EmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(214, 221, 236));
        label.setFont(new Font("Serif", Font.BOLD, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createZoneTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Serif", Font.BOLD, 24));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createSubsectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED_TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
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

    private JTextArea createInfoArea(int fontSize) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setBackground(PANEL_SOFT);
        area.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        area.setBorder(new EmptyBorder(8, 4, 0, 4));
        return area;
    }

    private JTextArea createStatusArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setBackground(PANEL_SOFT);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        return area;
    }

    private JButton createActionButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(TEXT);
        button.setBackground(new Color(49, 58, 84));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 18)),
                new EmptyBorder(10, 14, 10, 14)));
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

    private record CardViewData(
            String title,
            int manaCost,
            String typeLabel,
            String description,
            String footer,
            Color accent,
            String imagePath,
            boolean hidden) {
    }
}
