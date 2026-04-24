package com.sombrasdelavismo;

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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main extends JFrame {
    private static final Color APP_BACKGROUND = new Color(18, 24, 32);
    private static final Color PANEL_BACKGROUND = new Color(28, 34, 42);
    private static final Color CARD_BACKGROUND = new Color(34, 40, 48);
    private static final Color CARD_BORDER = new Color(88, 100, 112);
    private static final Color SELECTED_BORDER = new Color(233, 189, 81);
    private static final Color TEXT_PRIMARY = new Color(245, 240, 230);
    private static final Color TEXT_SECONDARY = new Color(210, 220, 230);
    private static final Color TEXT_MUTED = new Color(195, 205, 215);

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
    private final JLabel turnLabel = new JLabel();
    private final JLabel playerLabel = new JLabel();
    private final JLabel opponentLabel = new JLabel();
    private final JLabel previewNameLabel = new JLabel();
    private final JLabel previewMetaLabel = new JLabel();
    private final JLabel previewImageLabel = new JLabel();
    private final JLabel previewHelpLabel = new JLabel();
    private final JTextArea logArea = new JTextArea();
    private final JPanel opponentBoardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JPanel playerBoardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JPanel collectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JButton playButton = new JButton("Jugar");
    private final JButton attackButton = new JButton("Atacar");
    private final JButton endTurnButton = new JButton("Pasar turno");
    private final JButton helpButton = new JButton("Como jugar");

    public Main() {
        startNewGame();
        configureWindow();
        buildInterface();
        refreshUi(game.getLastAction());
        SwingUtilities.invokeLater(this::showHowToPlayDialog);
    }

    private void startNewGame() {
        collection = buildCollection();
        Player player1 = new Player("Jugador 1");
        Player player2 = new Player("Jugador 2");
        dealCards(player1, player2, collection);
        game = new Game(player1, player2);
        game.startGame();
        selectedCard = game.getCurrentPlayer().getHand().isEmpty() ? null : game.getCurrentPlayer().getHand().get(0);
        logArea.setText("");
    }

    private void configureWindow() {
        setTitle("Sombras del Avismo");
        setSize(1450, 900);
        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(APP_BACKGROUND);
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(APP_BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottomTabs(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel panel = createSection(new BorderLayout(12, 0));

        JLabel title = new JLabel("Sombras del Avismo");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        statusLabel.setForeground(new Color(255, 210, 120));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(title, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);
        panel.add(buildPreviewSection(), BorderLayout.CENTER);
        panel.add(buildControlSection(), BorderLayout.EAST);
        return panel;
    }

    private JPanel buildPreviewSection() {
        JPanel panel = createSection(new BorderLayout(12, 12));

        previewImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        previewImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        previewImageLabel.setPreferredSize(new Dimension(560, 620));
        previewImageLabel.setBorder(BorderFactory.createLineBorder(new Color(90, 105, 120), 1, true));

        previewNameLabel.setForeground(TEXT_PRIMARY);
        previewNameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        previewMetaLabel.setForeground(TEXT_SECONDARY);
        previewMetaLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        previewHelpLabel.setForeground(TEXT_MUTED);
        previewHelpLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(previewNameLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(previewMetaLabel);
        info.add(Box.createVerticalStrut(10));
        info.add(previewHelpLabel);

        panel.add(previewImageLabel, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildControlSection() {
        JPanel panel = createSection(new BorderLayout(0, 12));
        panel.setPreferredSize(new Dimension(340, 0));

        turnLabel.setForeground(new Color(255, 210, 120));
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        playerLabel.setForeground(new Color(230, 236, 242));
        playerLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        opponentLabel.setForeground(new Color(230, 236, 242));
        opponentLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        stats.add(turnLabel);
        stats.add(Box.createVerticalStrut(10));
        stats.add(playerLabel);
        stats.add(Box.createVerticalStrut(8));
        stats.add(opponentLabel);

        styleButton(playButton, new Color(190, 130, 50));
        styleButton(attackButton, new Color(150, 70, 60));
        styleButton(endTurnButton, new Color(60, 95, 130));
        styleButton(helpButton, new Color(84, 106, 66));

        JButton newGameButton = new JButton("Nueva partida");
        styleButton(newGameButton, new Color(70, 90, 100));

        playButton.addActionListener(event -> handlePlayCard());
        attackButton.addActionListener(event -> handleAttack());
        endTurnButton.addActionListener(event -> handleEndTurn());
        helpButton.addActionListener(event -> showHowToPlayDialog());
        newGameButton.addActionListener(event -> {
            startNewGame();
            refreshUi("Nueva partida iniciada.");
            showHowToPlayDialog();
        });

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new GridLayout(5, 1, 0, 10));
        buttons.add(playButton);
        buttons.add(attackButton);
        buttons.add(endTurnButton);
        buttons.add(helpButton);
        buttons.add(newGameButton);

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(12, 16, 22));
        logArea.setForeground(new Color(215, 222, 230));
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(80, 92, 105)), "Registro"));
        scrollPane.setPreferredSize(new Dimension(0, 220));

        panel.add(stats, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        return panel;
    }

    private JTabbedPane buildBottomTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("Mesa y mano", buildPlayTab());
        tabs.addTab("Coleccion", buildCollectionTab());
        return tabs;
    }

    private JPanel buildPlayTab() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 12));
        panel.setBackground(APP_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(buildCardRow("Mesa rival", opponentBoardPanel));
        panel.add(buildCardRow("Tu mesa", playerBoardPanel));
        panel.add(buildCardRow("Tu mano", handPanel));
        return panel;
    }

    private JScrollPane buildCollectionTab() {
        JPanel wrapper = createSection(new BorderLayout());
        collectionPanel.setOpaque(false);
        wrapper.add(collectionPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        styleScrollPane(scrollPane);
        return scrollPane;
    }

    private JPanel buildCardRow(String title, JPanel content) {
        JPanel panel = createSection(new BorderLayout(0, 8));
        JLabel label = new JLabel(title);
        label.setForeground(new Color(240, 236, 228));
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        content.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(content);
        styleScrollPane(scrollPane);

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void handlePlayCard() {
        if (selectedCard == null) {
            appendLog("Selecciona una carta de tu mano antes de jugar.");
            refreshUi("Selecciona una carta de tu mano antes de jugar.");
            return;
        }

        appendLog(game.playCard(selectedCard));
        if (!game.getCurrentPlayer().getHand().contains(selectedCard)) {
            selectedCard = findBestSelection();
        }
        refreshUi(game.getLastAction());
    }

    private void handleAttack() {
        CreatureCard creature = selectedCard instanceof CreatureCard ? (CreatureCard) selectedCard : null;
        appendLog(game.attackWith(creature));
        refreshUi(game.getLastAction());
    }

    private void handleEndTurn() {
        if (game.getWinner() != null) {
            appendLog("La partida ya ha terminado.");
            refreshUi("La partida ya ha terminado.");
            return;
        }
        game.nextTurn();
        selectedCard = findBestSelection();
        appendLog(game.getLastAction());
        refreshUi(game.getLastAction());
    }

    private void refreshUi(String message) {
        Player current = game.getCurrentPlayer();
        Player opponent = game.getWaitingPlayer();

        statusLabel.setText(game.getWinner() == null ? message : "Ganador: " + game.getWinner().getName());
        turnLabel.setText("Turno " + game.getTurnNumber() + " - " + current.getName());
        playerLabel.setText("Tu estado: vida " + current.getLife() + " | mana " + current.getMana() + "/" + current.getMaxMana() + " | mano " + current.getHand().size());
        opponentLabel.setText("Rival: vida " + opponent.getLife() + " | mana " + opponent.getMana() + "/" + opponent.getMaxMana() + " | mano " + opponent.getHand().size());

        rebuildOpponentBoard();
        rebuildPlayerBoard();
        rebuildHand();
        rebuildCollection();
        updatePreview();
        updateButtons();
    }

    private void rebuildOpponentBoard() {
        opponentBoardPanel.removeAll();
        for (CreatureCard creature : game.getWaitingPlayer().getBattlefield()) {
            opponentBoardPanel.add(createCreatureCard(creature, false));
        }
        opponentBoardPanel.revalidate();
        opponentBoardPanel.repaint();
    }

    private void rebuildPlayerBoard() {
        playerBoardPanel.removeAll();
        for (CreatureCard creature : game.getCurrentPlayer().getBattlefield()) {
            playerBoardPanel.add(createCreatureCard(creature, true));
        }
        playerBoardPanel.revalidate();
        playerBoardPanel.repaint();
    }

    private void rebuildHand() {
        handPanel.removeAll();
        for (Card card : game.getCurrentPlayer().getHand()) {
            handPanel.add(createHandCard(card));
        }
        handPanel.revalidate();
        handPanel.repaint();
    }

    private void rebuildCollection() {
        collectionPanel.removeAll();
        for (Card card : collection) {
            collectionPanel.add(createCollectionCard(card));
        }
        collectionPanel.revalidate();
        collectionPanel.repaint();
    }

    private JPanel createCreatureCard(CreatureCard creature, boolean currentPlayersCard) {
        JPanel panel = cardShell(creature);
        JLabel image = new JLabel(loadCardImage(creature, 180, 140));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel name = smallCenteredLabel(creature.getName(), true);
        String status = currentPlayersCard ? (creature.isReadyToAttack() ? "Lista para atacar" : "Esperando turno") : "Criatura rival";
        JLabel meta = smallCenteredLabel("Coste " + creature.getCost() + " | " + creature.getPower() + "/" + creature.getToughness(), false);
        JLabel detail = smallCenteredLabel(status, false);

        JButton button = new JButton("Seleccionar");
        styleMiniButton(button);
        button.setEnabled(currentPlayersCard);
        button.addActionListener(event -> {
            selectedCard = creature;
            refreshUi("Carta seleccionada.");
        });

        panel.add(image, BorderLayout.NORTH);
        panel.add(name, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        meta.setAlignmentX(Component.CENTER_ALIGNMENT);
        detail.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        south.add(meta);
        south.add(Box.createVerticalStrut(3));
        south.add(detail);
        south.add(Box.createVerticalStrut(6));
        south.add(button);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHandCard(Card card) {
        JPanel panel = cardShell(card);
        JLabel image = new JLabel(loadCardImage(card, 180, 140));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel name = smallCenteredLabel(card.getName(), true);
        JLabel meta = smallCenteredLabel(compactMeta(card), false);
        JLabel detail = smallCenteredLabel(shortDescription(card), false);

        JButton button = new JButton("Seleccionar");
        styleMiniButton(button);
        button.addActionListener(event -> {
            selectedCard = card;
            refreshUi("Carta seleccionada.");
        });

        panel.add(image, BorderLayout.NORTH);
        panel.add(name, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        meta.setAlignmentX(Component.CENTER_ALIGNMENT);
        detail.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        south.add(meta);
        south.add(Box.createVerticalStrut(3));
        south.add(detail);
        south.add(Box.createVerticalStrut(6));
        south.add(button);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCollectionCard(Card card) {
        JPanel panel = cardShell(card);
        panel.setPreferredSize(new Dimension(220, 300));
        JLabel image = new JLabel(loadCardImage(card, 190, 150));
        image.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel name = smallCenteredLabel(card.getName(), true);
        JLabel meta = smallCenteredLabel(compactMeta(card), false);
        JLabel detail = smallCenteredLabel(shortDescription(card), false);

        JButton button = new JButton("Ver");
        styleMiniButton(button);
        button.addActionListener(event -> {
            selectedCard = card;
            refreshUi("Vista previa actualizada.");
        });

        panel.add(image, BorderLayout.NORTH);
        panel.add(name, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        meta.setAlignmentX(Component.CENTER_ALIGNMENT);
        detail.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        south.add(meta);
        south.add(Box.createVerticalStrut(3));
        south.add(detail);
        south.add(Box.createVerticalStrut(6));
        south.add(button);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel cardShell(Card card) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(CARD_BACKGROUND);
        panel.setPreferredSize(new Dimension(220, 260));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isSelectedCard(card) ? SELECTED_BORDER : CARD_BORDER, isSelectedCard(card) ? 2 : 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return panel;
    }

    private JLabel smallCenteredLabel(String text, boolean bold) {
        JLabel label = new JLabel("<html><center>" + text + "</center></html>", SwingConstants.CENTER);
        label.setForeground(new Color(235, 238, 242));
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, bold ? 13 : 12));
        return label;
    }

    private void updatePreview() {
        if (selectedCard == null) {
            previewNameLabel.setText("Sin carta seleccionada");
            previewMetaLabel.setText("Selecciona una carta de tu mano o de tu mesa.");
            previewHelpLabel.setText("<html><body style='width:520px'>Selecciona una carta de tu mano, tu mesa o la coleccion para verla grande. Si esta en tu mano podras jugarla, y si ya esta en tu mesa podras atacar cuando quede lista.</body></html>");
            previewImageLabel.setIcon(null);
            previewImageLabel.setText("Sin carta");
            return;
        }

        previewNameLabel.setText(selectedCard.getName());
        previewMetaLabel.setText(buildMeta(selectedCard));
        previewHelpLabel.setText("<html><body style='width:520px'>" + buildPreviewHelp(selectedCard) + "<br><br><b>Descripcion:</b> " + selectedCard.getDescription() + "</body></html>");
        previewImageLabel.setIcon(loadCardImage(selectedCard, 540, 600));
        previewImageLabel.setText(previewImageLabel.getIcon() == null ? "Imagen no disponible" : "");
    }

    private void updateButtons() {
        boolean finished = game.getWinner() != null;
        boolean inHand = selectedCard != null && game.getCurrentPlayer().getHand().contains(selectedCard);
        boolean canAttack = selectedCard instanceof CreatureCard
                && game.getCurrentPlayer().getBattlefield().contains(selectedCard)
                && ((CreatureCard) selectedCard).isReadyToAttack();

        playButton.setEnabled(!finished && inHand && game.getCurrentPlayer().canPlay(selectedCard));
        attackButton.setEnabled(!finished && canAttack);
        endTurnButton.setEnabled(!finished);
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
            return new SpellCard(name, cost, buildSpellDescription(name, damage, healing, cardsToDraw), damage, healing, cardsToDraw, imagePath);
        }

        int power = 2 + Math.floorMod(name.hashCode(), 5);
        int toughness = 3 + Math.floorMod(name.hashCode() / 7, 4);
        return new CreatureCard(name, cost, power, toughness,
                name + " entra en mesa como criatura y puede atacar en turnos posteriores.", imagePath);
    }

    private String buildSpellDescription(String name, int damage, int healing, int cardsToDraw) {
        StringBuilder text = new StringBuilder(name).append(" activa un efecto.");
        if (damage > 0) {
            text.append(" Hace ").append(damage).append(" de dano.");
        }
        if (healing > 0) {
            text.append(" Cura ").append(healing).append(" vidas.");
        }
        if (cardsToDraw > 0) {
            text.append(" Roba ").append(cardsToDraw).append(" carta.");
        }
        return text.toString();
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
            return "Criatura | Coste " + card.getCost() + " | " + creature.getPower() + "/" + creature.getToughness();
        }
        if (card instanceof SpellCard spellCard) {
            return "Hechizo | Coste " + card.getCost() + " | " + spellCard.getEffect();
        }
        return card.getType();
    }

    private String compactMeta(Card card) {
        if (card instanceof CreatureCard creature) {
            return "Coste " + card.getCost() + " | " + creature.getPower() + "/" + creature.getToughness();
        }
        return "Coste " + card.getCost() + " | Hechizo";
    }

    private String shortDescription(Card card) {
        if (card instanceof CreatureCard creature) {
            return creature.isReadyToAttack() ? "Puede atacar" : "Invocacion";
        }
        if (card instanceof SpellCard spellCard) {
            return spellCard.getDamage() > 0 ? "Hace dano" : "Efecto especial";
        }
        return "Carta";
    }

    private String buildPreviewHelp(Card card) {
        if (game.getWinner() != null) {
            return "La partida ha terminado. Puedes iniciar otra desde el boton de nueva partida.";
        }
        if (collection.contains(card)
                && !game.getCurrentPlayer().getHand().contains(card)
                && !game.getCurrentPlayer().getBattlefield().contains(card)
                && !game.getWaitingPlayer().getBattlefield().contains(card)) {
            return "Esta carta esta en la coleccion. Puedes verla aqui en grande para conocer su coste y su efecto antes de que aparezca en partida.";
        }
        if (game.getCurrentPlayer().getHand().contains(card)) {
            return game.getCurrentPlayer().canPlay(card)
                    ? "Esta carta esta en tu mano y puedes jugarla ahora."
                    : "Esta carta esta en tu mano, pero no tienes mana suficiente.";
        }
        if (card instanceof CreatureCard creature && game.getCurrentPlayer().getBattlefield().contains(creature)) {
            return creature.isReadyToAttack()
                    ? "Esta criatura esta lista para atacar."
                    : "Esta criatura aun no puede atacar este turno.";
        }
        return card.getDescription();
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

    private JPanel createSection(BorderLayout layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 84, 96), 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        return panel;
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    }

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(new Color(250, 247, 241));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private void styleMiniButton(JButton button) {
        styleButton(button, new Color(70, 90, 110));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private boolean isSelectedCard(Card card) {
        return selectedCard == card;
    }

    private Card findBestSelection() {
        if (selectedCard != null && game.getCurrentPlayer().getHand().contains(selectedCard)) {
            return selectedCard;
        }
        if (!game.getCurrentPlayer().getHand().isEmpty()) {
            return game.getCurrentPlayer().getHand().get(0);
        }
        if (!game.getCurrentPlayer().getBattlefield().isEmpty()) {
            return game.getCurrentPlayer().getBattlefield().get(0);
        }
        return collection.isEmpty() ? null : collection.get(0);
    }

    private void showHowToPlayDialog() {
        JTextArea infoArea = new JTextArea(buildHelpText());
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setCaretPosition(0);
        infoArea.setBackground(new Color(250, 246, 237));
        infoArea.setForeground(new Color(40, 38, 34));
        infoArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        infoArea.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setPreferredSize(new Dimension(620, 520));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Guia de Sombras del Avismo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String buildHelpText() {
        StringBuilder text = new StringBuilder();
        text.append("COMO SE JUEGA\n\n");
        text.append("1. En tu turno robas carta y recuperas mana automaticamente.\n");
        text.append("2. Selecciona una carta de tu mano y pulsa Jugar para bajarla.\n");
        text.append("3. Las criaturas entran a tu mesa y no atacan el mismo turno en que se juegan.\n");
        text.append("4. En un turno posterior, selecciona una criatura lista y pulsa Atacar.\n");
        text.append("5. Los hechizos hacen dano, curan o te hacen robar cartas al momento.\n");
        text.append("6. Gana quien deje al rival sin vidas.\n\n");
        text.append("QUE PUEDES VER EN LA PANTALLA\n\n");
        text.append("- Tu mano: cartas que puedes jugar si tienes mana suficiente.\n");
        text.append("- Tu mesa: criaturas ya invocadas.\n");
        text.append("- Mesa rival: criaturas del oponente.\n");
        text.append("- Coleccion: todas las cartas del juego para verlas en grande.\n");
        text.append("- Panel central: muestra la carta seleccionada con su descripcion completa.\n\n");
        text.append("PERSONAJES Y CARTAS DESTACADAS\n\n");
        for (Card card : collection) {
            text.append("• ")
                    .append(card.getName())
                    .append(" - ")
                    .append(buildMeta(card))
                    .append(". ")
                    .append(card.getDescription())
                    .append("\n");
        }
        return text.toString();
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
}
