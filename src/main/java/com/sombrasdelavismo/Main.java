package com.sombrasdelavismo;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main extends JFrame {

    private static final List<String> CARD_FILES = Arrays.asList(
            "createcard Fernando.jpg",
            "createcard GONZALO BUENA.jpg",
            "createcard Humoo.jpg",
            "createcard Lin.jpg",
            "createcard Manuel.jpg",
            "createcard Pandaa.jpg",
            "createcard adrian.jpg",
            "createcard alonsoo.jpg",
            "createcard alvaro.jpg",
            "createcard amayita.jpg",
            "createcard antonioo.jpg",
            "createcard apuestas.jpg",
            "createcard bartol.jpg",
            "createcard carmenn.jpg",
            "createcard carpazoo.jpg",
            "createcard crespo.jpg",
            "createcard dieguitoo.jpg",
            "createcard elgrito.jpg",
            "createcard enchino.jpg",
            "createcard fabio.jpg",
            "createcard fran.jpg",
            "createcard fueguitoo.jpg",
            "createcard hackear.jpg",
            "createcard hugooo.jpg",
            "createcard inees.jpg",
            "createcard jaime.jpg",
            "createcard javi medio.jpg",
            "createcard juanma.jpg",
            "createcard kung fu.jpg",
            "createcard luismiii.jpg",
            "createcard mamen.jpg",
            "createcard marco.jpg",
            "createcard marcooo.jpg",
            "createcard marito.jpg",
            "createcard monof1.jpg",
            "createcard oviedo.jpg",
            "createcard pedro.jpg",
            "createcard quemao.jpg",
            "createcard rodri.jpg",
            "createcard urbano.jpg"
    );

    private Game game;
    private List<Card> collection;
    private Card selectedCard;

    private final JLabel statusLabel = new JLabel();
    private final JLabel currentTurnLabel = new JLabel();
    private final JLabel opponentStatsLabel = new JLabel();
    private final JLabel currentStatsLabel = new JLabel();
    private final JLabel opponentBoardLabel = new JLabel();
    private final JLabel currentBoardLabel = new JLabel();
    private final JLabel previewNameLabel = new JLabel();
    private final JLabel previewMetaLabel = new JLabel();
    private final JLabel previewImageLabel = new JLabel();
    private final JLabel previewHintLabel = new JLabel();
    private final JTextArea logArea = new JTextArea();
    private final JPanel opponentBoardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
    private final JPanel playerBoardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
    private final JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
    private final JPanel collectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
    private final JButton playButton = new JButton("Jugar carta");
    private final JButton attackButton = new JButton("Atacar con criatura");
    private final JButton endTurnButton = new JButton("Finalizar turno");

    public Main() {
        startNewGame();
        configureWindow();
        buildInterface();
        refreshUi(game.getLastAction());
    }

    private void startNewGame() {
        collection = buildCollection();
        Player player1 = new Player("Jugador 1");
        Player player2 = new Player("Jugador 2");
        dealCards(player1, player2, collection);
        game = new Game(player1, player2);
        game.startGame();
        selectedCard = game.getCurrentPlayer().getHand().isEmpty() ? null : game.getCurrentPlayer().getHand().get(0);
    }

    private void configureWindow() {
        setTitle("Sombras del Avismo");
        setSize(1540, 960);
        setMinimumSize(new Dimension(1280, 820));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new GradientPanel());
    }

    private void buildInterface() {
        JPanel root = (JPanel) getContentPane();
        root.setLayout(new BorderLayout(18, 18));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBoardSection(), BorderLayout.CENTER);
        root.add(buildBottomSection(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = createPanel(new BorderLayout(12, 12), new Color(8, 16, 28, 230));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(244, 194, 98, 80), 1, true),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Sombras del Avismo");
        title.setForeground(new Color(245, 232, 202));
        title.setFont(new Font("Palatino Linotype", Font.BOLD, 34));

        JLabel subtitle = new JLabel("Juego de cartas funcional con tablero, mano, ataques y hechizos");
        subtitle.setForeground(new Color(188, 205, 220));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));

        left.add(title);
        left.add(Box.createVerticalStrut(5));
        left.add(subtitle);

        statusLabel.setForeground(new Color(255, 220, 138));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(left, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBoardSection() {
        JPanel section = new JPanel(new BorderLayout(18, 18));
        section.setOpaque(false);
        section.add(buildArena(), BorderLayout.CENTER);
        section.add(buildSidePanel(), BorderLayout.EAST);
        return section;
    }

    private JPanel buildArena() {
        JPanel arena = new JPanel(new GridLayout(3, 1, 0, 16));
        arena.setOpaque(false);
        arena.add(buildPlayerZone(false));
        arena.add(buildCenterStrip());
        arena.add(buildPlayerZone(true));
        return arena;
    }

    private JPanel buildPlayerZone(boolean currentPlayerZone) {
        JPanel zone = createPanel(new BorderLayout(12, 12),
                currentPlayerZone ? new Color(12, 26, 42, 220) : new Color(24, 18, 12, 220));
        zone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentPlayerZone ? new Color(95, 140, 182, 80) : new Color(217, 180, 92, 80), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel nameLabel = new JLabel(currentPlayerZone ? "Tu mesa" : "Mesa rival");
        nameLabel.setFont(new Font("Palatino Linotype", Font.BOLD, 24));
        nameLabel.setForeground(new Color(245, 233, 205));

        JLabel statsLabel = currentPlayerZone ? currentStatsLabel : opponentStatsLabel;
        JLabel boardLabel = currentPlayerZone ? currentBoardLabel : opponentBoardLabel;

        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        statsLabel.setForeground(new Color(227, 235, 245));
        boardLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        boardLabel.setForeground(new Color(189, 204, 219));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(nameLabel, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(statsLabel);
        right.add(Box.createVerticalStrut(4));
        right.add(boardLabel);
        top.add(right, BorderLayout.EAST);

        JPanel cardsPanel = currentPlayerZone ? playerBoardPanel : opponentBoardPanel;
        cardsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        styleScrollPane(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        zone.add(top, BorderLayout.NORTH);
        zone.add(scrollPane, BorderLayout.CENTER);
        return zone;
    }

    private JPanel buildCenterStrip() {
        JPanel strip = createPanel(new BorderLayout(16, 16), new Color(13, 12, 18, 205));
        strip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(126, 120, 134, 60), 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JPanel turnPanel = new JPanel();
        turnPanel.setOpaque(false);
        turnPanel.setLayout(new BoxLayout(turnPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Zona de combate");
        title.setForeground(new Color(241, 231, 206));
        title.setFont(new Font("Palatino Linotype", Font.BOLD, 28));

        currentTurnLabel.setForeground(new Color(255, 215, 120));
        currentTurnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel helper = new JLabel("<html><body style='width:500px'>Selecciona una carta en tu mano para jugarla. Si seleccionas una criatura de tu mesa que ya este lista, puedes atacar con ella directamente al rival.</body></html>");
        helper.setForeground(new Color(194, 203, 216));
        helper.setFont(new Font("SansSerif", Font.PLAIN, 15));

        turnPanel.add(title);
        turnPanel.add(Box.createVerticalStrut(8));
        turnPanel.add(currentTurnLabel);
        turnPanel.add(Box.createVerticalStrut(10));
        turnPanel.add(helper);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setOpaque(false);

        stylePrimaryButton(playButton);
        styleAccentButton(attackButton);
        styleSecondaryButton(endTurnButton);

        playButton.addActionListener(event -> handlePlayCard());
        attackButton.addActionListener(event -> handleAttack());
        endTurnButton.addActionListener(event -> handleEndTurn());

        JButton newGameButton = new JButton("Nueva partida");
        styleSecondaryButton(newGameButton);
        newGameButton.addActionListener(event -> {
            startNewGame();
            refreshUi("Nueva partida iniciada.");
        });

        actionPanel.add(playButton);
        actionPanel.add(attackButton);
        actionPanel.add(endTurnButton);
        actionPanel.add(newGameButton);

        strip.add(turnPanel, BorderLayout.WEST);
        strip.add(actionPanel, BorderLayout.EAST);
        return strip;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel(new BorderLayout(0, 16));
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(390, 0));
        side.add(buildPreviewPanel(), BorderLayout.CENTER);
        side.add(buildLogPanel(), BorderLayout.SOUTH);
        return side;
    }

    private JPanel buildPreviewPanel() {
        JPanel preview = createPanel(new BorderLayout(12, 12), new Color(27, 17, 10, 220));
        preview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 181, 89, 80), 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Carta seleccionada");
        title.setForeground(new Color(246, 232, 201));
        title.setFont(new Font("Palatino Linotype", Font.BOLD, 26));

        previewImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        previewImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        previewImageLabel.setPreferredSize(new Dimension(320, 430));
        previewImageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 212, 110, 60), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        previewNameLabel.setForeground(new Color(255, 230, 180));
        previewNameLabel.setFont(new Font("Palatino Linotype", Font.BOLD, 27));

        previewMetaLabel.setForeground(new Color(230, 234, 240));
        previewMetaLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        previewHintLabel.setForeground(new Color(202, 210, 220));
        previewHintLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        previewHintLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(previewNameLabel);
        info.add(Box.createVerticalStrut(8));
        info.add(previewMetaLabel);
        info.add(Box.createVerticalStrut(12));
        info.add(previewHintLabel);

        preview.add(title, BorderLayout.NORTH);
        preview.add(previewImageLabel, BorderLayout.CENTER);
        preview.add(info, BorderLayout.SOUTH);
        return preview;
    }

    private JPanel buildLogPanel() {
        JPanel logPanel = createPanel(new BorderLayout(10, 10), new Color(9, 14, 24, 225));
        logPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(95, 129, 160, 70), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        logPanel.setPreferredSize(new Dimension(0, 220));

        JLabel title = new JLabel("Registro de partida");
        title.setForeground(new Color(238, 228, 200));
        title.setFont(new Font("Palatino Linotype", Font.BOLD, 22));

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(5, 10, 18));
        logArea.setForeground(new Color(212, 220, 230));
        logArea.setCaretColor(new Color(212, 220, 230));
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        styleScrollPane(scrollPane);

        logPanel.add(title, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        return logPanel;
    }

    private JTabbedPane buildBottomSection() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.addTab("Mano actual", buildHandTab());
        tabs.addTab("Coleccion", buildCollectionTab());
        return tabs;
    }

    private JScrollPane buildHandTab() {
        JPanel wrapper = createPanel(new BorderLayout(10, 10), new Color(8, 14, 24, 220));
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Tu mano");
        title.setForeground(new Color(239, 229, 201));
        title.setFont(new Font("Palatino Linotype", Font.BOLD, 22));

        JLabel help = new JLabel("Elige una carta y usa el boton superior para jugarla.");
        help.setForeground(new Color(193, 204, 217));
        help.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.add(title);
        header.add(help);

        handPanel.setOpaque(false);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(handPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        styleScrollPane(scrollPane);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        return scrollPane;
    }

    private JScrollPane buildCollectionTab() {
        JPanel wrapper = createPanel(new BorderLayout(10, 10), new Color(8, 14, 24, 220));
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Coleccion completa");
        title.setForeground(new Color(239, 229, 201));
        title.setFont(new Font("Palatino Linotype", Font.BOLD, 22));

        JLabel help = new JLabel("Puedes revisar aqui todas las cartas que se cargaron desde el zip.");
        help.setForeground(new Color(193, 204, 217));
        help.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.add(title);
        header.add(help);

        collectionPanel.setOpaque(false);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(collectionPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        styleScrollPane(scrollPane);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        return scrollPane;
    }

    private void handlePlayCard() {
        appendLog(game.playCard(selectedCard));
        if (game.getWinner() == null) {
            if (game.getCurrentPlayer().getHand().contains(selectedCard)) {
                refreshUi("Carta seleccionada lista para jugar.");
            } else if (!game.getCurrentPlayer().getHand().isEmpty()) {
                selectedCard = game.getCurrentPlayer().getHand().get(0);
                refreshUi("Carta jugada.");
            } else if (!game.getCurrentPlayer().getBattlefield().isEmpty()) {
                selectedCard = game.getCurrentPlayer().getBattlefield().get(0);
                refreshUi("Carta jugada.");
            } else {
                selectedCard = null;
                refreshUi("Carta jugada.");
            }
        } else {
            refreshUi(game.getWinner().getName() + " ha ganado.");
        }
    }

    private void handleAttack() {
        CreatureCard creatureCard = selectedCard instanceof CreatureCard ? (CreatureCard) selectedCard : null;
        appendLog(game.attackWith(creatureCard));
        refreshUi(game.getWinner() == null ? "Ataque resuelto." : game.getWinner().getName() + " ha ganado.");
    }

    private void handleEndTurn() {
        if (game.getWinner() != null) {
            appendLog("La partida ya ha terminado.");
            refreshUi("La partida ya ha terminado.");
            return;
        }
        game.nextTurn();
        selectedCard = game.getCurrentPlayer().getHand().isEmpty() ? null : game.getCurrentPlayer().getHand().get(0);
        appendLog(game.getLastAction());
        refreshUi("Cambio de turno.");
    }

    private void refreshUi(String message) {
        Player current = game.getCurrentPlayer();
        Player opponent = game.getWaitingPlayer();

        if (selectedCard == null) {
            if (!current.getHand().isEmpty()) {
                selectedCard = current.getHand().get(0);
            } else if (!current.getBattlefield().isEmpty()) {
                selectedCard = current.getBattlefield().get(0);
            }
        }

        statusLabel.setText(game.getWinner() != null
                ? "Ganador: " + game.getWinner().getName()
                : message + "  |  " + game.getLastAction());
        currentTurnLabel.setText("Turno " + game.getTurnNumber() + " de " + current.getName());
        currentStatsLabel.setText("Vida: " + current.getLife() + "  |  Mana: " + current.getMana() + "/" + current.getMaxMana() + "  |  Mano: " + current.getHand().size());
        opponentStatsLabel.setText("Vida: " + opponent.getLife() + "  |  Mana: " + opponent.getMana() + "/" + opponent.getMaxMana() + "  |  Mano: " + opponent.getHand().size());
        currentBoardLabel.setText(current.getBattlefield().size() + " criaturas en mesa");
        opponentBoardLabel.setText(opponent.getBattlefield().size() + " criaturas en mesa");

        rebuildBoardPanels();
        rebuildHandPanel();
        rebuildCollectionPanel();
        updatePreview();
        updateButtons();
    }

    private void rebuildBoardPanels() {
        opponentBoardPanel.removeAll();
        for (CreatureCard creature : game.getWaitingPlayer().getBattlefield()) {
            opponentBoardPanel.add(createBoardCard(creature, false));
        }
        opponentBoardPanel.revalidate();
        opponentBoardPanel.repaint();

        playerBoardPanel.removeAll();
        for (CreatureCard creature : game.getCurrentPlayer().getBattlefield()) {
            playerBoardPanel.add(createBoardCard(creature, true));
        }
        playerBoardPanel.revalidate();
        playerBoardPanel.repaint();
    }

    private void rebuildHandPanel() {
        handPanel.removeAll();
        for (Card card : game.getCurrentPlayer().getHand()) {
            handPanel.add(createHandCard(card));
        }
        handPanel.revalidate();
        handPanel.repaint();
    }

    private void rebuildCollectionPanel() {
        collectionPanel.removeAll();
        for (Card card : collection) {
            collectionPanel.add(createCollectionCard(card));
        }
        collectionPanel.revalidate();
        collectionPanel.repaint();
    }

    private JPanel createBoardCard(CreatureCard creature, boolean currentPlayersCard) {
        JPanel card = createPanel(new BorderLayout(0, 8), new Color(18, 28, 40, 220));
        card.setPreferredSize(new Dimension(156, 200));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isSelected(creature) ? new Color(255, 206, 103) : new Color(92, 120, 148, 80), 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel image = new JLabel(loadCardImage(creature, 132, 100));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel name = new JLabel("<html><center>" + creature.getName() + "</center></html>", SwingConstants.CENTER);
        name.setForeground(new Color(245, 233, 202));
        name.setFont(new Font("SansSerif", Font.BOLD, 13));

        String combatText = creature.getPower() + "/" + creature.getToughness()
                + (currentPlayersCard ? (creature.isReadyToAttack() ? "  |  Lista" : "  |  Espera") : "");
        JLabel meta = new JLabel(combatText, SwingConstants.CENTER);
        meta.setForeground(new Color(205, 214, 225));
        meta.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton button = new JButton(currentPlayersCard ? "Seleccionar" : "Ver");
        styleMiniButton(button, currentPlayersCard ? new Color(43, 72, 103) : new Color(90, 79, 47));
        button.addActionListener(event -> {
            selectedCard = creature;
            refreshUi("Criatura seleccionada.");
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(name);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(meta);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(button);

        card.add(image, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.CENTER);
        return card;
    }

    private JPanel createHandCard(Card card) {
        JPanel panel = createPanel(new BorderLayout(0, 8), new Color(14, 25, 40, 220));
        panel.setPreferredSize(new Dimension(170, 238));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isSelected(card) ? new Color(255, 206, 103) : new Color(95, 129, 160, 80), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel image = new JLabel(loadCardImage(card, 145, 130));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel name = new JLabel("<html><center>" + card.getName() + "</center></html>", SwingConstants.CENTER);
        name.setForeground(new Color(247, 237, 208));
        name.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel meta = new JLabel("<html><center>" + compactMeta(card) + "</center></html>", SwingConstants.CENTER);
        meta.setForeground(new Color(211, 219, 228));
        meta.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton button = new JButton("Seleccionar");
        styleMiniButton(button, new Color(193, 137, 46));
        button.addActionListener(event -> {
            selectedCard = card;
            refreshUi("Carta seleccionada.");
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(name);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(meta);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(button);

        panel.add(image, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCollectionCard(Card card) {
        JPanel panel = createPanel(new BorderLayout(0, 8), new Color(24, 24, 24, 220));
        panel.setPreferredSize(new Dimension(150, 178));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isSelected(card) ? new Color(255, 206, 103) : new Color(214, 179, 90, 60), 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel image = new JLabel(loadCardImage(card, 124, 90));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel name = new JLabel("<html><center>" + card.getName() + "</center></html>", SwingConstants.CENTER);
        name.setForeground(new Color(243, 232, 199));
        name.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton button = new JButton("Ver");
        styleMiniButton(button, new Color(90, 79, 47));
        button.addActionListener(event -> {
            selectedCard = card;
            refreshUi("Carta de la coleccion seleccionada.");
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(name);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(button);

        panel.add(image, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.CENTER);
        return panel;
    }

    private void updatePreview() {
        if (selectedCard == null) {
            previewNameLabel.setText("Sin seleccion");
            previewMetaLabel.setText("Selecciona una carta de tu mano o de la mesa.");
            previewHintLabel.setText("<html><body style='width:320px'>Cuando selecciones una carta veras aqui su imagen grande, sus estadisticas y si se puede jugar o usar para atacar.</body></html>");
            previewImageLabel.setIcon(null);
            previewImageLabel.setText("Sin carta");
            return;
        }

        previewNameLabel.setText(selectedCard.getName());
        previewMetaLabel.setText(buildMeta(selectedCard));
        previewHintLabel.setText("<html><body style='width:320px'>" + buildPreviewHelp(selectedCard) + "</body></html>");
        previewImageLabel.setIcon(loadCardImage(selectedCard, 300, 390));
        previewImageLabel.setText(previewImageLabel.getIcon() == null ? "Imagen no disponible" : "");
    }

    private void updateButtons() {
        boolean gameFinished = game.getWinner() != null;
        boolean cardInHand = selectedCard != null && game.getCurrentPlayer().getHand().contains(selectedCard);
        boolean creatureOnBoard = selectedCard instanceof CreatureCard
                && game.getCurrentPlayer().getBattlefield().contains(selectedCard)
                && ((CreatureCard) selectedCard).isReadyToAttack();

        playButton.setEnabled(!gameFinished && cardInHand && game.getCurrentPlayer().canPlay(selectedCard));
        attackButton.setEnabled(!gameFinished && creatureOnBoard);
        endTurnButton.setEnabled(!gameFinished);
    }

    private void appendLog(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        if (!logArea.getText().isBlank()) {
            logArea.append("\n");
        }
        logArea.append(text);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private List<Card> buildCollection() {
        List<Card> cards = new ArrayList<>();
        for (String fileName : CARD_FILES) {
            cards.add(createCard(fileName));
        }
        return cards;
    }

    private void dealCards(Player player1, Player player2, List<Card> cards) {
        for (int index = 0; index < cards.size(); index++) {
            if (index % 2 == 0) {
                player1.addCardToDeck(cards.get(index));
            } else {
                player2.addCardToDeck(cards.get(index));
            }
        }
    }

    private Card createCard(String fileName) {
        String name = prettifyName(fileName);
        String imagePath = "cards/" + fileName;
        int cost = 1 + Math.floorMod(name.length(), 6);

        if (isSpellCard(name)) {
            int damage = 2 + Math.floorMod(name.hashCode(), 4);
            int healing = name.toLowerCase(Locale.ROOT).contains("humoo") ? 3 : 0;
            int cardsToDraw = name.toLowerCase(Locale.ROOT).contains("hackear") || name.toLowerCase(Locale.ROOT).contains("monof1") ? 1 : 0;
            String effect = buildSpellDescription(name, damage, healing, cardsToDraw);
            return new SpellCard(name, cost, effect, damage, healing, cardsToDraw, imagePath);
        }

        int power = 2 + Math.floorMod(name.hashCode(), 5);
        int toughness = 3 + Math.floorMod(name.hashCode() / 7, 4);
        String description = name + " entra en escena como criatura personalizada. Debe esperar un turno para atacar y luego puede presionar al rival directamente.";
        return new CreatureCard(name, cost, power, toughness, description, imagePath);
    }

    private String buildSpellDescription(String name, int damage, int healing, int cardsToDraw) {
        StringBuilder description = new StringBuilder(name).append(" desata un efecto especial.");
        if (damage > 0) {
            description.append(" Inflige ").append(damage).append(" de dano al rival.");
        }
        if (healing > 0) {
            description.append(" Tambien cura ").append(healing).append(" vidas.");
        }
        if (cardsToDraw > 0) {
            description.append(" Ademas roba ").append(cardsToDraw).append(" carta");
            if (cardsToDraw > 1) {
                description.append("s");
            }
            description.append(".");
        }
        return description.toString();
    }

    private boolean isSpellCard(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        return normalized.contains("hackear")
                || normalized.contains("apuestas")
                || normalized.contains("elgrito")
                || normalized.contains("kung fu")
                || normalized.contains("fueguitoo")
                || normalized.contains("humoo")
                || normalized.contains("monof1");
    }

    private String prettifyName(String fileName) {
        String cleanName = fileName.replace("createcard", "")
                .replace(".jpg", "")
                .replace(".jpeg", "")
                .trim()
                .replaceAll("\\s+", " ");

        if (cleanName.equals(cleanName.toUpperCase(Locale.ROOT)) || cleanName.equals(cleanName.toLowerCase(Locale.ROOT))) {
            String[] parts = cleanName.toLowerCase(Locale.ROOT).split(" ");
            StringBuilder builder = new StringBuilder();
            for (String part : parts) {
                if (part.isBlank()) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
            return builder.toString();
        }
        return cleanName;
    }

    private String buildMeta(Card card) {
        if (card instanceof CreatureCard creature) {
            return "Criatura  |  Coste " + card.getCost() + "  |  " + creature.getPower() + "/" + creature.getToughness();
        }
        if (card instanceof SpellCard spellCard) {
            return "Hechizo  |  Coste " + card.getCost() + "  |  " + spellCard.getEffect();
        }
        return card.getType() + "  |  Coste " + card.getCost();
    }

    private String compactMeta(Card card) {
        if (card instanceof CreatureCard creature) {
            return "Coste " + card.getCost() + "  |  " + creature.getPower() + "/" + creature.getToughness();
        }
        return "Coste " + card.getCost() + "  |  Hechizo";
    }

    private String buildPreviewHelp(Card card) {
        if (game.getWinner() != null) {
            return "La partida ha terminado. Puedes empezar una nueva cuando quieras.";
        }
        if (game.getCurrentPlayer().getHand().contains(card)) {
            if (game.getCurrentPlayer().canPlay(card)) {
                return "Esta carta esta en tu mano y puedes jugarla ahora mismo.";
            }
            return "Esta carta esta en tu mano, pero ahora no tienes mana suficiente para jugarla.";
        }
        if (card instanceof CreatureCard creature && game.getCurrentPlayer().getBattlefield().contains(creature)) {
            if (creature.isReadyToAttack()) {
                return "Esta criatura esta en tu mesa y ya puede atacar.";
            }
            return "Esta criatura esta en tu mesa, pero aun no puede atacar este turno.";
        }
        return card.getDescription();
    }

    private boolean isSelected(Card card) {
        return selectedCard != null && card != null && selectedCard.getName().equals(card.getName());
    }

    private ImageIcon loadCardImage(Card card, int maxWidth, int maxHeight) {
        URL resource = getClass().getClassLoader().getResource(card.getImagePath());
        if (resource == null) {
            return null;
        }
        try {
            BufferedImage original = ImageIO.read(resource);
            if (original == null) {
                return null;
            }
            double scale = Math.min((double) maxWidth / original.getWidth(), (double) maxHeight / original.getHeight());
            scale = Math.min(scale, 1.0);
            int width = Math.max(1, (int) Math.round(original.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(original.getHeight() * scale));
            Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException exception) {
            return null;
        }
    }

    private JPanel createPanel(BorderLayout layout, Color color) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(color);
        return panel;
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(193, 137, 46));
        button.setForeground(new Color(250, 247, 241));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
    }

    private void styleAccentButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(150, 63, 48));
        button.setForeground(new Color(250, 247, 241));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(45, 71, 100));
        button.setForeground(new Color(244, 247, 250));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
    }

    private void styleMiniButton(JButton button, Color background) {
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(new Color(248, 247, 243));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        button.setMargin(new Insets(7, 12, 7, 12));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new Main().setVisible(true);
        });
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, new Color(3, 8, 16), getWidth(), getHeight(), new Color(28, 16, 8)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
