package com.sombrasdelavismo.ui;

import com.sombrasdelavismo.model.ActionResult;
import com.sombrasdelavismo.model.Card;
import com.sombrasdelavismo.model.CardCatalog;
import com.sombrasdelavismo.model.CardRarity;
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
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class GameFrame extends JFrame {
    private static final Color BACKGROUND = new Color(10, 13, 20);
    private static final Color PANEL = new Color(22, 27, 39);
    private static final Color PANEL_ALT = new Color(16, 21, 31);
    private static final Color PANEL_SOFT = new Color(28, 34, 48);
    private static final Color TEXT = new Color(241, 244, 249);
    private static final Color MUTED_TEXT = new Color(183, 192, 210);
    private static final Color RED = new Color(197, 79, 79);
    private static final Color BLUE = new Color(67, 139, 234);
    private static final Color NEUTRAL = new Color(117, 132, 160);
    private static final Color BORDER = new Color(255, 255, 255, 10);
    private static final Color BORDER_SOFT = new Color(255, 255, 255, 6);
    private static final Color BUTTON = new Color(45, 55, 78);
    private static final Color BUTTON_DANGER = new Color(87, 54, 62);
    private static final int HAND_CARD_WIDTH = 180;
    private static final int HAND_CARD_HEIGHT = 248;
    private static final int OPPONENT_HAND_CARD_WIDTH = 104;
    private static final int OPPONENT_HAND_CARD_HEIGHT = 144;
    private static final int BOARD_CARD_WIDTH = 142;
    private static final int BOARD_CARD_HEIGHT = 198;
    private static final int PREVIEW_CARD_WIDTH = 270;
    private static final int PREVIEW_CARD_HEIGHT = 396;
    private static final int INSPECTOR_CARD_WIDTH = 360;
    private static final int INSPECTOR_CARD_HEIGHT = 530;
    private static final int BASE_WINDOW_WIDTH = 1760;
    private static final int BASE_WINDOW_HEIGHT = 1020;
    private static final int SCREEN_MARGIN = 34;
    private static final CreatureCard BLOCKER_CANCELLED =
            new CreatureCard("BLOCKER_CANCELLED", "BLOCKER_CANCELLED", 0, 0, 0, "", null);
    private static final CreatureCard NO_BLOCK =
            new CreatureCard("NO_BLOCK", "NO_BLOCK", 0, 0, 0, "", null);

    private final double uiScale;
    private final Rectangle screenBounds;
    private final Dimension windowSize;
    private final Dimension minimumWindowSize;
    private final boolean maximizeOnOpen;
    private final Dimension handCardSize;
    private final Dimension opponentHandCardSize;
    private final Dimension boardCardSize;
    private final Dimension previewCardSize;
    private final Dimension inspectorCardSize;

    private final JLabel turnLabel;
    private final JLabel phaseLabel;
    private final JLabel topStatusLabel;
    private final JTextArea actionLabel;
    private final JTextArea actionHintArea;
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
    private final JTextArea logArea;
    private final JButton playButton;
    private final JButton attackPlayerButton;
    private final JButton attackCreatureButton;
    private final JButton endTurnButton;
    private final JButton graveyardButton;
    private final JButton newGameButton;
    private final JButton viewCardButton;
    private final JButton pauseButton;

    private Game game;
    private Card selectedHandCard;
    private CreatureCard selectedAttacker;
    private CreatureCard selectedEnemyCreature;
    private String playerOneName;
    private String playerTwoName;
    private boolean passScreensEnabled;
    private WindowMode windowMode;
    private Dimension preferredWindowResolution;

    public GameFrame() {
        screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        uiScale = calculateUiScale(screenBounds);
        windowSize = calculateWindowSize(screenBounds, uiScale);
        minimumWindowSize = calculateMinimumWindowSize(screenBounds, uiScale);
        maximizeOnOpen = shouldMaximizeOnOpen(screenBounds, windowSize);
        handCardSize = scaledDimension(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, 96, 132);
        opponentHandCardSize = scaledDimension(OPPONENT_HAND_CARD_WIDTH, OPPONENT_HAND_CARD_HEIGHT, 74, 104);
        boardCardSize = scaledDimension(BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT, 76, 106);
        previewCardSize = scaledDimension(PREVIEW_CARD_WIDTH, PREVIEW_CARD_HEIGHT, 156, 226);
        inspectorCardSize = scaledDimension(INSPECTOR_CARD_WIDTH, INSPECTOR_CARD_HEIGHT, 220, 324);
        playerOneName = "Jugador 1";
        playerTwoName = "Jugador 2";
        passScreensEnabled = true;
        windowMode = maximizeOnOpen ? WindowMode.MAXIMIZED : WindowMode.WINDOWED;
        preferredWindowResolution = new Dimension(windowSize);

        setTitle("Sombras del Abismo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(minimumWindowSize);
        setSize(windowSize);

        turnLabel = createHeaderLabel(30);
        phaseLabel = createHeaderLabel(16);
        topStatusLabel = createInfoLabel();
        actionLabel = createStatusArea();
        actionHintArea = createInfoArea(13);
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
        previewCardHolder.setBorder(new EmptyBorder(scaled(4), 0, scaled(6), 0));
        previewCardHolder.setPreferredSize(new Dimension(
                previewCardSize.width + scaled(12),
                previewCardSize.height + scaled(12)));

        previewDescriptionArea = createInfoArea(14);
        logArea = createLogArea();

        playButton = createActionButton("Jugar carta");
        attackPlayerButton = createActionButton("Atacar rival");
        attackCreatureButton = createActionButton("Atacar criatura");
        endTurnButton = createActionButton("Terminar turno");
        graveyardButton = createActionButton("Ver cementerios");
        newGameButton = createActionButton("Nueva partida");
        viewCardButton = createActionButton("Ver carta");
        pauseButton = createActionButton("Pausa");

        setContentPane(buildLoadingScreen());
        wireActions();

        if (windowMode == WindowMode.MAXIMIZED) {
            setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
        setLocationRelativeTo(null);
        setVisible(true);

        Timer loadingTimer = new Timer(900, event -> showMainMenu());
        loadingTimer.setRepeats(false);
        loadingTimer.start();
    }

    private enum WindowMode {
        WINDOWED("Ventana"),
        MAXIMIZED("Maximizada"),
        FULLSCREEN("Pantalla completa");

        private final String label;

        WindowMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private record ResolutionOption(int width, int height) {
        private Dimension toDimension() {
            return new Dimension(width, height);
        }

        @Override
        public String toString() {
            return width + " x " + height;
        }
    }

    private static double calculateUiScale(Rectangle screenBounds) {
        int availableWidth = Math.max(1, screenBounds.width - SCREEN_MARGIN);
        int availableHeight = Math.max(1, screenBounds.height - SCREEN_MARGIN);
        double widthScale = (double) availableWidth / BASE_WINDOW_WIDTH;
        double heightScale = (double) availableHeight / BASE_WINDOW_HEIGHT;
        return clamp(Math.min(widthScale, heightScale), 0.42d, 1.0d);
    }

    private static Dimension calculateWindowSize(Rectangle screenBounds, double scale) {
        int width = Math.round((float) (BASE_WINDOW_WIDTH * scale));
        int height = Math.round((float) (BASE_WINDOW_HEIGHT * scale));
        width = Math.min(width, Math.max(320, screenBounds.width - SCREEN_MARGIN));
        height = Math.min(height, Math.max(360, screenBounds.height - SCREEN_MARGIN));
        return new Dimension(width, height);
    }

    private static Dimension calculateMinimumWindowSize(Rectangle screenBounds, double scale) {
        int width = Math.max(640, Math.round((float) (980 * scale)));
        int height = Math.max(430, Math.round((float) (650 * scale)));
        return new Dimension(
                Math.min(width, Math.max(320, screenBounds.width - 8)),
                Math.min(height, Math.max(360, screenBounds.height - 8)));
    }

    private static boolean shouldMaximizeOnOpen(Rectangle screenBounds, Dimension requestedWindowSize) {
        return requestedWindowSize.width >= screenBounds.width - SCREEN_MARGIN
                || requestedWindowSize.height >= screenBounds.height - SCREEN_MARGIN
                || screenBounds.width < 1500
                || screenBounds.height < 820;
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private Dimension scaledDimension(int width, int height, int minimumWidth, int minimumHeight) {
        return new Dimension(
                Math.max(minimumWidth, scaled(width)),
                Math.max(minimumHeight, scaled(height)));
    }

    private int scaled(int value) {
        return Math.max(1, Math.round((float) (value * uiScale)));
    }

    private int scaledFontSize(int value) {
        return Math.max(11, scaled(value));
    }

    private JPanel buildLoadingScreen() {
        JPanel root = createMenuRoot();
        JPanel center = createPanelShell(PANEL);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = createMenuTitle("Sombras del Abismo", 42);
        JLabel subtitle = createMenuText("Preparando la mesa...");
        JLabel loading = createMenuText("Cargando cartas y hechizos");

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(title);
        center.add(Box.createVerticalStrut(scaled(10)));
        center.add(subtitle);
        center.add(Box.createVerticalStrut(scaled(26)));
        center.add(loading);
        center.add(Box.createVerticalGlue());

        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private void showMainMenu() {
        setTitle("Sombras del Abismo - Menu principal");
        selectedHandCard = null;
        selectedAttacker = null;
        selectedEnemyCreature = null;
        setContentPane(buildMainMenu());
        revalidate();
        repaint();
    }

    private JPanel buildMainMenu() {
        JPanel root = createMenuRoot();

        JPanel menu = createPanelShell(PANEL);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(Math.max(360, scaled(520)), Math.max(430, scaled(560))));

        JLabel title = createMenuTitle("Sombras del Abismo", 38);
        JLabel subtitle = createMenuText("Duelo de cartas por turnos");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playMenuButton = createMenuButton("Jugar");
        JButton albumButton = createMenuButton("Ver Album");
        JButton helpMenuButton = createMenuButton("Ayuda");
        JButton settingsButton = createMenuButton("Ajustes");
        JButton exitButton = createMenuButton("Salir");

        playMenuButton.addActionListener(event -> startNewGame());
        albumButton.addActionListener(event -> showAlbumScreen());
        helpMenuButton.addActionListener(event -> showHowToPlay());
        settingsButton.addActionListener(event -> showSettingsDialog());
        exitButton.addActionListener(event -> {
            dispose();
            System.exit(0);
        });

        menu.add(Box.createVerticalGlue());
        menu.add(title);
        menu.add(Box.createVerticalStrut(scaled(8)));
        menu.add(subtitle);
        menu.add(Box.createVerticalStrut(scaled(34)));
        menu.add(playMenuButton);
        menu.add(Box.createVerticalStrut(scaled(12)));
        menu.add(albumButton);
        menu.add(Box.createVerticalStrut(scaled(12)));
        menu.add(helpMenuButton);
        menu.add(Box.createVerticalStrut(scaled(12)));
        menu.add(settingsButton);
        menu.add(Box.createVerticalStrut(scaled(12)));
        menu.add(exitButton);
        menu.add(Box.createVerticalGlue());

        root.add(menu, BorderLayout.CENTER);
        return root;
    }

    private void showAlbumScreen() {
        setTitle("Sombras del Abismo - Album");
        setContentPane(buildAlbumScreen());
        revalidate();
        repaint();
    }

    private JPanel buildAlbumScreen() {
        JPanel root = new JPanel(new BorderLayout(scaled(14), scaled(14)));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(scaled(16), scaled(16), scaled(16), scaled(16)));

        JPanel header = createPanelShell(PANEL);
        header.setLayout(new BorderLayout(scaled(12), 0));

        JLabel title = createMenuTitle("Album", 30);
        JButton backButton = createActionButton("Volver");
        backButton.addActionListener(event -> showMainMenu());

        JComboBox<String> rarityFilter = new JComboBox<>(new String[] {
                "Todas",
                CardRarity.MYTHIC.getDisplayName(),
                CardRarity.EPIC.getDisplayName(),
                CardRarity.RARE.getDisplayName(),
                CardRarity.COMMON.getDisplayName(),
                CardRarity.SPELL.getDisplayName()
        });
        rarityFilter.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(13)));
        rarityFilter.setForeground(TEXT);
        rarityFilter.setBackground(PANEL_SOFT);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, scaled(10), 0));
        controls.setOpaque(false);
        controls.add(rarityFilter);
        controls.add(backButton);

        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        Dimension albumCardSize = scaledDimension(168, 236, 122, 172);
        int columns = Math.max(2, (windowSize.width - scaled(96)) / (albumCardSize.width + scaled(18)));
        JPanel cardGrid = new JPanel(new GridLayout(0, columns, scaled(14), scaled(14)));
        cardGrid.setBackground(PANEL_ALT);
        cardGrid.setBorder(new EmptyBorder(scaled(14), scaled(14), scaled(14), scaled(14)));

        JScrollPane albumScroller = new JScrollPane(
                cardGrid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        albumScroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 18)));
        albumScroller.getViewport().setBackground(PANEL_ALT);
        albumScroller.getVerticalScrollBar().setUnitIncrement(scaled(18));

        populateAlbum(cardGrid, albumCardSize, null);
        rarityFilter.addActionListener(event ->
                populateAlbum(cardGrid, albumCardSize, rarityFromFilter((String) rarityFilter.getSelectedItem())));

        root.add(header, BorderLayout.NORTH);
        root.add(albumScroller, BorderLayout.CENTER);
        return root;
    }

    private void populateAlbum(JPanel cardGrid, Dimension albumCardSize, CardRarity selectedRarity) {
        cardGrid.removeAll();
        for (Card card : CardCatalog.createAlbumCards()) {
            if (selectedRarity != null && card.getRarity() != selectedRarity) {
                continue;
            }

            CardButton button = buildAlbumCardButton(card, albumCardSize);
            button.addActionListener(event -> showCardInspector(card, false, true));
            attachInspector(button, card, false, true);
            cardGrid.add(button);
        }
        cardGrid.revalidate();
        cardGrid.repaint();
    }

    private CardRarity rarityFromFilter(String filter) {
        if (filter == null || "Todas".equals(filter)) {
            return null;
        }
        for (CardRarity rarity : CardRarity.values()) {
            if (rarity.getDisplayName().equals(filter)) {
                return rarity;
            }
        }
        return null;
    }

    private CardButton buildAlbumCardButton(Card card, Dimension size) {
        if (card instanceof CreatureCard creature) {
            return new CardButton(
                    creature.getName(),
                    creature.getManaCost(),
                    "Criatura - " + creature.getRarity().getDisplayName(),
                    creature.getDescription(),
                    creature.getBaseAttack() + " / " + creature.getBaseHealth(),
                    rarityAccent(creature.getRarity()),
                    creature.getImagePath(),
                    false,
                    size);
        }
        return buildCardButton(card, false, true, size);
    }

    private void showSettingsDialog() {
        JTextField playerOneField = new JTextField(playerOneName, 18);
        JTextField playerTwoField = new JTextField(playerTwoName, 18);
        JComboBox<WindowMode> windowModeBox = new JComboBox<>(WindowMode.values());
        ResolutionOption[] resolutionOptions = buildResolutionOptions();
        JComboBox<ResolutionOption> resolutionBox = new JComboBox<>(resolutionOptions);
        JCheckBox passScreensCheck = new JCheckBox("Pantallas de paso de turno", passScreensEnabled);

        windowModeBox.setSelectedItem(windowMode);
        resolutionBox.setSelectedItem(findResolutionOption(resolutionOptions, preferredWindowResolution));
        resolutionBox.setEnabled(windowMode == WindowMode.WINDOWED);
        windowModeBox.addActionListener(event -> resolutionBox.setEnabled(windowModeBox.getSelectedItem() == WindowMode.WINDOWED));

        playerOneField.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        playerTwoField.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        windowModeBox.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        resolutionBox.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        passScreensCheck.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));

        JPanel form = createSettingsForm(playerOneField, playerTwoField, windowModeBox, resolutionBox, passScreensCheck);
        int option = JOptionPane.showConfirmDialog(this, form, "Ajustes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        playerOneName = safePlayerName(playerOneField.getText(), "Jugador 1");
        playerTwoName = safePlayerName(playerTwoField.getText(), "Jugador 2");
        passScreensEnabled = passScreensCheck.isSelected();
        ResolutionOption selectedResolution = (ResolutionOption) resolutionBox.getSelectedItem();
        applyDisplayMode((WindowMode) windowModeBox.getSelectedItem(), selectedResolution.toDimension());
    }

    private JPanel createSettingsForm(
            JTextField playerOneField,
            JTextField playerTwoField,
            JComboBox<WindowMode> windowModeBox,
            JComboBox<ResolutionOption> resolutionBox,
            JCheckBox passScreensCheck) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(PANEL);
        form.setBorder(new EmptyBorder(scaled(14), scaled(14), scaled(14), scaled(14)));
        form.add(createDialogLabel("Jugador 1"));
        form.add(playerOneField);
        form.add(Box.createVerticalStrut(scaled(10)));
        form.add(createDialogLabel("Jugador 2"));
        form.add(playerTwoField);
        form.add(Box.createVerticalStrut(scaled(14)));
        form.add(createDialogLabel("Modo de pantalla"));
        form.add(windowModeBox);
        form.add(Box.createVerticalStrut(scaled(10)));
        form.add(createDialogLabel("Resolución de ventana"));
        form.add(resolutionBox);
        form.add(createSmallDialogText("La resolución se aplica cuando el modo es Ventana."));
        form.add(Box.createVerticalStrut(scaled(14)));
        passScreensCheck.setOpaque(false);
        passScreensCheck.setForeground(TEXT);
        form.add(passScreensCheck);
        return form;
    }

    private ResolutionOption[] buildResolutionOptions() {
        List<ResolutionOption> options = new ArrayList<>();
        addResolutionOption(options, 1024, 576);
        addResolutionOption(options, 1280, 720);
        addResolutionOption(options, 1366, 768);
        addResolutionOption(options, 1600, 900);
        addResolutionOption(options, 1760, 990);
        addResolutionOption(options, 1920, 1080);
        addResolutionOption(options, 2560, 1440);
        addResolutionOption(options, preferredWindowResolution.width, preferredWindowResolution.height);
        return options.toArray(new ResolutionOption[0]);
    }

    private void addResolutionOption(List<ResolutionOption> options, int width, int height) {
        if (width > screenBounds.width || height > screenBounds.height) {
            return;
        }
        ResolutionOption option = new ResolutionOption(width, height);
        if (!options.contains(option)) {
            options.add(option);
        }
    }

    private ResolutionOption findResolutionOption(ResolutionOption[] options, Dimension resolution) {
        ResolutionOption fallback = options.length == 0
                ? new ResolutionOption(windowSize.width, windowSize.height)
                : options[0];
        for (ResolutionOption option : options) {
            if (option.width() == resolution.width && option.height() == resolution.height) {
                return option;
            }
        }
        return fallback;
    }

    private void applyDisplayMode(WindowMode selectedMode, Dimension selectedResolution) {
        if (selectedMode == null || selectedResolution == null) {
            return;
        }

        windowMode = selectedMode;
        preferredWindowResolution = new Dimension(selectedResolution);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (selectedMode != WindowMode.FULLSCREEN && device.getFullScreenWindow() == this) {
            device.setFullScreenWindow(null);
        }

        if (selectedMode == WindowMode.FULLSCREEN) {
            setExtendedState(JFrame.NORMAL);
            if (!isUndecorated()) {
                dispose();
                setUndecorated(true);
                setVisible(true);
            }
            device.setFullScreenWindow(this);
            revalidate();
            repaint();
            return;
        }

        if (isUndecorated()) {
            dispose();
            setUndecorated(false);
            setVisible(true);
        }

        if (selectedMode == WindowMode.MAXIMIZED) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            return;
        }

        setExtendedState(JFrame.NORMAL);
        setSize(selectedResolution);
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JLabel createDialogLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(13)));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createSmallDialogText(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED_TEXT);
        label.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(12)));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createMenuRoot() {
        JPanel root = new JPanel(new GridLayout(1, 1));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(scaled(26), scaled(26), scaled(26), scaled(26)));
        return root;
    }

    private JLabel createMenuTitle(String text, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(TEXT);
        label.setFont(new Font("Serif", Font.BOLD, scaledFontSize(fontSize)));
        return label;
    }

    private JLabel createMenuText(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(MUTED_TEXT);
        label.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(15)));
        return label;
    }

    private JButton createMenuButton(String text) {
        JButton button = createActionButton(text);
        Dimension size = new Dimension(Math.max(240, scaled(290)), Math.max(48, scaled(56)));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(size);
        button.setPreferredSize(size);
        return button;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(scaled(12), scaled(12)));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(scaled(12), scaled(12), scaled(20), scaled(12)));
        root.add(buildGameTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildBoardArea(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildGameTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PANEL);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SOFT),
                new EmptyBorder(scaled(7), scaled(12), scaled(7), scaled(12))));
        topBar.setPreferredSize(new Dimension(100, Math.max(44, scaled(52))));

        JLabel title = new JLabel("Sombras del Abismo");
        title.setForeground(TEXT);
        title.setFont(new Font("Serif", Font.BOLD, scaledFontSize(20)));
        topStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        pauseButton.setText("Pausa");
        pauseButton.setPreferredSize(new Dimension(Math.max(118, scaled(128)), Math.max(34, scaled(38))));
        pauseButton.setToolTipText("Pausa la partida y abre opciones de reinicio, ajustes y menu principal.");
        topBar.add(title, BorderLayout.WEST);
        topBar.add(topStatusLabel, BorderLayout.CENTER);
        topBar.add(pauseButton, BorderLayout.EAST);
        return topBar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, scaled(8)));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(Math.max(300, scaled(380)), 100));
        sidebar.setMinimumSize(new Dimension(Math.max(260, scaled(320)), 100));

        JPanel topStack = new JPanel();
        topStack.setOpaque(false);
        topStack.setLayout(new BoxLayout(topStack, BoxLayout.Y_AXIS));
        topStack.add(Box.createVerticalStrut(scaled(6)));
        topStack.add(buildStatusPanel());
        topStack.add(Box.createVerticalStrut(scaled(8)));
        topStack.add(buildActionPanel());

        sidebar.add(topStack, BorderLayout.NORTH);
        sidebar.add(buildLogPanel(), BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel buildStatusPanel() {
        JPanel panel = createPanelShell(PANEL);
        panel.setLayout(new BorderLayout(0, scaled(10)));

        turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phaseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionLabel.setBorder(new EmptyBorder(scaled(10), scaled(10), scaled(10), scaled(10)));
        actionLabel.setMinimumSize(new Dimension(0, 0));

        JPanel metrics = new JPanel(new GridLayout(1, 2, scaled(8), 0));
        metrics.setOpaque(false);
        metrics.setAlignmentX(Component.LEFT_ALIGNMENT);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(68, scaled(78))));
        metrics.add(createMetricTile("Turno", turnLabel));
        metrics.add(createMetricTile("Juega", phaseLabel));

        JScrollPane actionScroller = createTextScroller(
                actionLabel,
                PANEL_SOFT,
                Math.max(160, scaled(190)),
                Math.max(126, scaled(150)));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(metrics);
        content.add(Box.createVerticalStrut(scaled(8)));
        content.add(createSubsectionLabel("Resumen"));
        content.add(actionScroller);

        panel.add(createSectionLabel("Estado de la partida"), BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel shell = createPanelShell(PANEL_ALT);
        shell.setLayout(new BorderLayout(0, scaled(10)));

        JPanel primaryActions = new JPanel(new GridLayout(2, 2, scaled(7), scaled(7)));
        primaryActions.setOpaque(false);
        primaryActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        primaryActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(76, scaled(92))));
        primaryActions.add(playButton);
        primaryActions.add(viewCardButton);
        primaryActions.add(attackPlayerButton);
        primaryActions.add(attackCreatureButton);

        JPanel utilityActions = new JPanel(new GridLayout(1, 2, scaled(7), 0));
        utilityActions.setOpaque(false);
        utilityActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        utilityActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(36, scaled(44))));
        utilityActions.add(endTurnButton);
        utilityActions.add(graveyardButton);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        actionHintArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionHintArea.setBackground(PANEL);
        actionHintArea.setBorder(new EmptyBorder(scaled(10), scaled(10), scaled(10), scaled(10)));
        JScrollPane hintScroller = createTextScroller(
                actionHintArea,
                PANEL,
                Math.max(92, scaled(112)),
                Math.max(78, scaled(92)));

        content.add(createSubsectionLabel("Comandos"));
        content.add(primaryActions);
        content.add(Box.createVerticalStrut(scaled(7)));
        content.add(utilityActions);
        content.add(Box.createVerticalStrut(scaled(8)));
        content.add(createSubsectionLabel("Ayuda"));
        content.add(hintScroller);

        shell.add(createSectionLabel("Acciones"), BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildPreviewPanel() {
        JPanel shell = createPanelShell(PANEL_SOFT);
        shell.setLayout(new BorderLayout(0, scaled(10)));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createSectionLabel("Carta ampliada"));
        header.add(Box.createVerticalStrut(scaled(8)));
        header.add(previewTitleLabel);
        header.add(Box.createVerticalStrut(scaled(4)));
        header.add(previewMetaLabel);

        JScrollPane previewTextScroller = new JScrollPane(previewDescriptionArea);
        previewTextScroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 14)));
        previewTextScroller.getViewport().setBackground(PANEL_SOFT);
        previewTextScroller.setPreferredSize(new Dimension(100, Math.max(118, scaled(170))));

        shell.add(header, BorderLayout.NORTH);
        shell.add(previewCardHolder, BorderLayout.CENTER);
        shell.add(previewTextScroller, BorderLayout.SOUTH);
        return shell;
    }

    private JPanel buildLogPanel() {
        JPanel shell = createPanelShell(PANEL);
        shell.setLayout(new BorderLayout(0, scaled(8)));

        JScrollPane logScroller = new JScrollPane(logArea);
        logScroller.setBorder(BorderFactory.createEmptyBorder());
        logScroller.getViewport().setBackground(PANEL);
        logScroller.setPreferredSize(new Dimension(100, Math.max(120, scaled(240))));

        shell.add(createSectionLabel("Historial"), BorderLayout.NORTH);
        shell.add(logScroller, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildBoardArea() {
        JPanel boardColumn = new JPanel(new BorderLayout(0, scaled(8)));
        boardColumn.setOpaque(false);
        boardColumn.setMinimumSize(new Dimension(0, 0));

        boardColumn.add(buildHandSection(
                "Jugador rival",
                topPlayerLabel,
                opponentHandPanel,
                "Mano rival",
                opponentHandCardSize,
                compactHandSectionHeight()), BorderLayout.NORTH);
        boardColumn.add(buildBattlefieldSection(), BorderLayout.CENTER);
        boardColumn.add(buildHandSection(
                "Tu lado",
                bottomPlayerLabel,
                playerHandPanel,
                "Mano activa",
                handCardSize,
                playerHandSectionHeight()), BorderLayout.SOUTH);
        return boardColumn;
    }

    private JPanel buildHandSection(
            String title,
            JLabel infoLabel,
            JPanel handPanel,
            String rowLabel,
            Dimension cardSize,
            int sectionHeight) {
        JPanel section = createPanelShell(PANEL_ALT);
        section.setLayout(new BorderLayout(0, scaled(5)));
        section.setMinimumSize(new Dimension(0, 0));
        section.setPreferredSize(new Dimension(100, sectionHeight));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = createZoneTitle(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(scaled(2)));
        header.add(infoLabel);
        header.add(Box.createVerticalStrut(scaled(3)));
        header.add(createSubsectionLabel(rowLabel));

        section.add(header, BorderLayout.NORTH);
        section.add(createScroller(handPanel, cardSize.height + scaled(12)), BorderLayout.CENTER);
        return section;
    }

    private int compactHandSectionHeight() {
        return opponentHandCardSize.height + scaled(92);
    }

    private int playerHandSectionHeight() {
        return handCardSize.height + scaled(126);
    }

    private JPanel buildBattlefieldSection() {
        JPanel section = createPanelShell(PANEL);
        section.setLayout(new BorderLayout(0, scaled(8)));
        section.setMinimumSize(new Dimension(0, 0));

        JPanel fields = new JPanel(new GridLayout(1, 2, scaled(10), 0));
        fields.setOpaque(false);
        fields.setMinimumSize(new Dimension(0, 0));

        section.add(createZoneTitle("Tablero"), BorderLayout.NORTH);
        fields.add(buildBattlefieldRow("Campo rival", opponentBoardPanel));
        fields.add(buildBattlefieldRow("Tu campo", playerBoardPanel));
        section.add(fields, BorderLayout.CENTER);
        return section;
    }

    private JPanel buildBattlefieldRow(String title, JPanel cardPanel) {
        JPanel row = new JPanel(new BorderLayout(0, scaled(4)));
        row.setOpaque(false);
        row.setMinimumSize(new Dimension(0, 0));
        row.add(createSubsectionLabel(title), BorderLayout.NORTH);
        row.add(createScroller(cardPanel, boardCardSize.height + scaled(4)), BorderLayout.CENTER);
        return row;
    }

    private void wireActions() {
        playButton.addActionListener(event -> handlePlayCard());
        attackPlayerButton.addActionListener(event -> handleAttackPlayer());
        attackCreatureButton.addActionListener(event -> handleAttackCreature());
        endTurnButton.addActionListener(event -> handleEndTurn());
        graveyardButton.addActionListener(event -> showGraveyards());
        newGameButton.addActionListener(event -> startNewGame());
        viewCardButton.addActionListener(event -> handleViewCard());
        pauseButton.addActionListener(event -> showPauseMenu());
    }

    private void startNewGame() {
        if (!showPlayerSetupDialog()) {
            return;
        }

        startMatch();
    }

    private void restartCurrentGame() {
        startMatch();
    }

    private void startMatch() {

        Player player1 = new Player(playerOneName);
        Player player2 = new Player(playerTwoName);
        player1.setDeckTemplate(CardCatalog.createDefaultDeck());
        player2.setDeckTemplate(CardCatalog.createDefaultDeck());

        game = new Game(player1, player2);
        selectedHandCard = null;
        selectedAttacker = null;
        selectedEnemyCreature = null;

        ActionResult result = game.startGame();
        setTitle("Sombras del Abismo");
        setContentPane(buildContent());
        refreshUi();
        revalidate();
        repaint();
        showPassScreen("Empieza la partida.\n\nTurno de " + game.getCurrentPlayer().getName() + ".");
        applyResult(result, false);
    }

    private void showPauseMenu() {
        if (game == null) {
            return;
        }

        JDialog pauseDialog = new JDialog(this, "Pausa", true);
        pauseDialog.setUndecorated(true);
        pauseDialog.setContentPane(buildPauseMenu(pauseDialog));
        pauseDialog.pack();
        pauseDialog.setResizable(false);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setVisible(true);
    }

    private JPanel buildPauseMenu(JDialog pauseDialog) {
        JPanel shell = new JPanel(new BorderLayout(0, scaled(14)));
        shell.setBackground(PANEL);
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 24)),
                new EmptyBorder(scaled(20), scaled(22), scaled(20), scaled(22))));
        shell.setPreferredSize(new Dimension(Math.max(360, scaled(460)), Math.max(320, scaled(390))));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = createMenuTitle("Pausa", 34);
        JLabel subtitle = createMenuText(game.getCurrentPlayer().getName()
                + " juega el turno " + game.getTurnNumber());
        JLabel stats = createMenuText("Vida " + game.getCurrentPlayer().getLife()
                + "  |  Maná " + game.getCurrentPlayer().getCurrentMana()
                + "/" + game.getCurrentPlayer().getMaxMana()
                + "  |  Mazo " + game.getCurrentPlayer().getDeck().size());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(scaled(8)));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(scaled(4)));
        header.add(stats);

        JPanel actionStack = new JPanel();
        actionStack.setOpaque(false);
        actionStack.setLayout(new BoxLayout(actionStack, BoxLayout.Y_AXIS));

        JButton resumeButton = createPauseButton("Reanudar", BUTTON);
        JButton restartButton = createPauseButton("Reiniciar partida", BUTTON_DANGER);
        JButton settingsButton = createPauseButton("Ajustes", BUTTON);
        JButton mainMenuButton = createPauseButton("Menú principal", BUTTON_DANGER);

        resumeButton.addActionListener(event -> pauseDialog.dispose());
        restartButton.addActionListener(event -> {
            if (showStyledConfirmation(
                    pauseDialog,
                    "Reiniciar partida",
                    "Se reiniciará la partida actual con los mismos jugadores.",
                    "Reiniciar")) {
                pauseDialog.dispose();
                restartCurrentGame();
            }
        });
        settingsButton.addActionListener(event -> showSettingsDialog());
        mainMenuButton.addActionListener(event -> {
            if (showStyledConfirmation(
                    pauseDialog,
                    "Volver al menú",
                    "La partida actual se cerrará y volverás al menú principal.",
                    "Salir")) {
                pauseDialog.dispose();
                game = null;
                showMainMenu();
            }
        });

        actionStack.add(resumeButton);
        actionStack.add(Box.createVerticalStrut(scaled(9)));
        actionStack.add(restartButton);
        actionStack.add(Box.createVerticalStrut(scaled(9)));
        actionStack.add(settingsButton);
        actionStack.add(Box.createVerticalStrut(scaled(9)));
        actionStack.add(mainMenuButton);

        shell.add(header, BorderLayout.NORTH);
        shell.add(actionStack, BorderLayout.CENTER);
        return shell;
    }

    private JButton createPauseButton(String text, Color background) {
        JButton button = createActionButton(text);
        Dimension size = new Dimension(Integer.MAX_VALUE, Math.max(44, scaled(52)));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(size);
        button.setPreferredSize(new Dimension(Math.max(260, scaled(320)), size.height));
        button.setBackground(background);
        return button;
    }

    private boolean showStyledConfirmation(
            JDialog parentDialog,
            String title,
            String message,
            String confirmText) {
        final boolean[] confirmed = {false};
        JDialog confirmDialog = new JDialog(parentDialog, title, true);
        confirmDialog.setUndecorated(true);

        JPanel shell = new JPanel(new BorderLayout(0, scaled(14)));
        shell.setBackground(PANEL);
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 24)),
                new EmptyBorder(scaled(18), scaled(18), scaled(18), scaled(18))));
        shell.setPreferredSize(new Dimension(Math.max(320, scaled(390)), Math.max(190, scaled(230))));

        JLabel titleLabel = createMenuTitle(title, 24);
        JTextArea messageArea = createInfoArea(14);
        messageArea.setBackground(PANEL);
        messageArea.setBorder(new EmptyBorder(0, scaled(8), 0, scaled(8)));
        messageArea.setText(message);

        JPanel buttons = new JPanel(new GridLayout(1, 2, scaled(8), 0));
        buttons.setOpaque(false);
        JButton cancelButton = createActionButton("Cancelar");
        JButton confirmButton = createActionButton(confirmText);
        confirmButton.setBackground(BUTTON_DANGER);

        cancelButton.addActionListener(event -> confirmDialog.dispose());
        confirmButton.addActionListener(event -> {
            confirmed[0] = true;
            confirmDialog.dispose();
        });

        buttons.add(cancelButton);
        buttons.add(confirmButton);

        shell.add(titleLabel, BorderLayout.NORTH);
        shell.add(messageArea, BorderLayout.CENTER);
        shell.add(buttons, BorderLayout.SOUTH);

        confirmDialog.setContentPane(shell);
        confirmDialog.pack();
        confirmDialog.setResizable(false);
        confirmDialog.setLocationRelativeTo(parentDialog);
        confirmDialog.setVisible(true);
        return confirmed[0];
    }

    private boolean showPlayerSetupDialog() {
        JTextField playerOneField = new JTextField(playerOneName, 18);
        JTextField playerTwoField = new JTextField(playerTwoName, 18);
        playerOneField.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        playerTwoField.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(PANEL);
        form.setBorder(new EmptyBorder(scaled(14), scaled(14), scaled(14), scaled(14)));
        form.add(createDialogLabel("Jugador 1"));
        form.add(playerOneField);
        form.add(Box.createVerticalStrut(scaled(10)));
        form.add(createDialogLabel("Jugador 2"));
        form.add(playerTwoField);

        int option = JOptionPane.showConfirmDialog(
                this,
                form,
                "Nueva partida",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return false;
        }

        playerOneName = safePlayerName(playerOneField.getText(), "Jugador 1");
        playerTwoName = safePlayerName(playerTwoField.getText(), "Jugador 2");
        return true;
    }

    private String safePlayerName(String rawName, String fallback) {
        if (rawName == null || rawName.isBlank()) {
            return fallback;
        }
        return rawName.trim();
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

        turnLabel.setText(String.valueOf(game.getTurnNumber()));
        phaseLabel.setText(game.getCurrentPlayer().getName());
        topStatusLabel.setText("Turno " + game.getTurnNumber()
                + "  |  Juega " + game.getCurrentPlayer().getName()
                + "  |  Maná " + game.getCurrentPlayer().getCurrentMana()
                + "/" + game.getCurrentPlayer().getMaxMana());
        actionLabel.setText("Fase: " + game.getPhase()
                + "\nManá: " + game.getCurrentPlayer().getCurrentMana()
                + "/" + game.getCurrentPlayer().getMaxMana()
                + "\nAcciones usadas: " + game.getActionCounter()
                + "\n\nÚltima acción\n" + game.getLastAction());
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
        pauseButton.setEnabled(!game.isFinished());
        actionHintArea.setText(buildActionHint(playRestriction, attackRestriction, attackCreatureRestriction));
        actionHintArea.setCaretPosition(0);

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
        pauseButton.setToolTipText("Pausa la partida y abre opciones de reinicio, ajustes y menu principal.");
    }

    private void rebuildOpponentHand() {
        opponentHandPanel.removeAll();

        if (game.getCurrentPlayer().isRevealOpponentHand()) {
            List<Card> opponentHand = game.getWaitingPlayer().getHand();
            if (opponentHand.isEmpty()) {
                addEmptyZoneMessage(opponentHandPanel, "Sin cartas en mano");
            }
            for (Card card : opponentHand) {
                CardButton button = buildCardButton(card, false, false, opponentHandCardSize);
                button.setEnabled(false);
                opponentHandPanel.add(button);
            }
        } else {
            int handSize = game.getWaitingPlayer().getHand().size();
            if (handSize == 0) {
                addEmptyZoneMessage(opponentHandPanel, "Sin cartas en mano");
            }
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
                        opponentHandCardSize);
                hiddenButton.setEnabled(false);
                opponentHandPanel.add(hiddenButton);
            }
        }

        opponentHandPanel.revalidate();
        opponentHandPanel.repaint();
    }

    private void rebuildHand(JPanel panel, List<Card> hand) {
        panel.removeAll();

        if (hand.isEmpty()) {
            addEmptyZoneMessage(panel, "No tienes cartas en mano");
        }
        for (Card card : hand) {
            CardButton button = buildCardButton(card, false, true, handCardSize);
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

        if (creatures.isEmpty()) {
            addEmptyZoneMessage(panel, ownBoard ? "Tu campo esta vacio" : "Campo rival vacio");
        }
        for (CreatureCard creature : creatures) {
            boolean hidden = !ownBoard && creature.isHiddenFromOpponent();
            CardButton button = buildCardButton(creature, hidden, ownBoard, boardCardSize);
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

    private void addEmptyZoneMessage(JPanel panel, String message) {
        JLabel label = new JLabel(message);
        label.setForeground(MUTED_TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(13)));
        label.setBorder(new EmptyBorder(scaled(10), scaled(12), scaled(10), scaled(12)));
        panel.add(label);
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
                    "Criatura - " + creature.getRarity().getDisplayName(),
                    creature.getDescription(),
                    creature.getAttack() + " / " + creature.getHealth() + "  |  " + creatureState(creature),
                    rarityAccent(creature.getRarity()),
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

    private Color rarityAccent(CardRarity rarity) {
        return switch (rarity) {
            case MYTHIC -> new Color(231, 171, 57);
            case EPIC -> new Color(158, 105, 230);
            case RARE -> BLUE;
            case COMMON -> NEUTRAL;
            case SPELL -> new Color(177, 88, 88);
        };
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
            placeholder.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(18)));
            placeholder.setPreferredSize(previewCardSize);
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
                    previewCardSize);
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
                    .append(creatureState(creature))
                    .append("\nRareza: ")
                    .append(creature.getRarity().getDisplayName());
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

    private String buildActionHint(
            String playRestriction,
            String attackRestriction,
            String attackCreatureRestriction) {
        if (game.isFinished()) {
            return "Partida terminada. Abre Pausa para volver al menu principal o empieza otra partida.";
        }
        if (selectedHandCard != null) {
            if (playRestriction == null) {
                return "Carta lista: pulsa Jugar carta. Si quieres verla grande, usa Ver carta.";
            }
            return "Carta seleccionada, pero no se puede jugar: " + playRestriction;
        }
        if (selectedAttacker != null) {
            if (attackRestriction == null && selectedEnemyCreature != null && attackCreatureRestriction == null) {
                return "Atacante y objetivo listos: pulsa Atacar criatura.";
            }
            if (attackRestriction == null) {
                return "Atacante listo: pulsa Atacar rival o elige una criatura rival como objetivo.";
            }
            return "Esa criatura no puede atacar ahora: " + attackRestriction;
        }
        if (selectedEnemyCreature != null) {
            return "Objetivo rival marcado. Selecciona ahora una criatura de tu campo para atacar.";
        }
        return "Empieza seleccionando una carta de tu mano o una criatura de tu campo.";
    }

    private void showHowToPlay() {
        JTextArea helpArea = new JTextArea();
        helpArea.setEditable(false);
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpArea.setForeground(TEXT);
        helpArea.setBackground(PANEL);
        helpArea.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        helpArea.setBorder(new EmptyBorder(scaled(14), scaled(14), scaled(14), scaled(14)));
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
                - Los botones de acción muestran el motivo si algo no se puede hacer.
                - Pasa el raton por una carta para ver su texto completo.
                - Selecciona una carta y pulsa Ver carta para abrirla en grande.
                - El texto Mareo significa que la criatura acaba de entrar y aún no puede atacar.
                """);

        JScrollPane scroller = new JScrollPane(helpArea);
        scroller.setPreferredSize(new Dimension(Math.max(460, scaled(640)), Math.max(320, scaled(420))));
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
        inspectorText.setBorder(new EmptyBorder(scaled(14), scaled(14), scaled(14), scaled(14)));
        inspectorText.setText(buildPreviewNarrative(card, hidden, view) + "\n\n" + buildInspectorNotes(card, hidden));

        JScrollPane inspectorScroller = new JScrollPane(inspectorText);
        inspectorScroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 18)));
        inspectorScroller.getViewport().setBackground(PANEL);
        inspectorScroller.setPreferredSize(new Dimension(Math.max(250, scaled(330)), Math.max(420, scaled(690))));

        JPanel visualColumn = new JPanel(new BorderLayout());
        visualColumn.setOpaque(false);
        visualColumn.add(inspectorVisual, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(scaled(16), scaled(12)));
        content.setBackground(PANEL_SOFT);
        content.setBorder(new EmptyBorder(scaled(12), scaled(12), scaled(12), scaled(12)));
        content.setPreferredSize(new Dimension(Math.max(720, scaled(980)), Math.max(560, scaled(760))));

        JLabel header = new JLabel(view.title() + "  |  " + buildPreviewMeta(card, view));
        header.setForeground(TEXT);
        header.setFont(new Font("Serif", Font.BOLD, scaledFontSize(20)));

        content.add(header, BorderLayout.NORTH);
        content.add(visualColumn, BorderLayout.WEST);
        content.add(inspectorScroller, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, content, "Inspector de carta", JOptionPane.PLAIN_MESSAGE);
    }

    private Component buildInspectorVisual(CardViewData view) {
        ImageUtils.ResolvedImage inspectorImage = ImageUtils.resolveInspectorImage(view.imagePath(), view.title());
        JLabel artworkLabel = buildInspectorArtwork(inspectorImage, view.hidden());
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
                inspectorCardSize);
        inspectorCard.setFocusable(false);
        inspectorCard.setSelected(true);
        return inspectorCard;
    }

    private JLabel buildInspectorArtwork(ImageUtils.ResolvedImage inspectorImage, boolean hidden) {
        if (hidden || inspectorImage == null) {
            return null;
        }

        // We intentionally avoid upscaling here: if the source is smaller than the available
        // area, showing it at native size is usually sharper than stretching it.
        // If you later add 800x1120 or 1000x1400 cards, increase the 470x690 bounds and the
        // inspector dialog size instead of forcing blurrier upscaling.
        BufferedImage scaledImage = ImageUtils.getScaledToFit(
                inspectorImage,
                Math.max(320, scaled(470)),
                Math.max(450, scaled(690)),
                false);
        if (scaledImage == null) {
            return null;
        }

        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setOpaque(true);
        label.setBackground(new Color(9, 12, 19));
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 18)),
                new EmptyBorder(scaled(10), scaled(10), scaled(10), scaled(10))));
        label.setPreferredSize(new Dimension(scaledImage.getWidth() + scaled(22), scaledImage.getHeight() + scaled(22)));
        return label;
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

    private void showPassScreen(String message) {
        if (!passScreensEnabled) {
            return;
        }
        JOptionPane.showMessageDialog(this, message, "Cambio de turno", JOptionPane.PLAIN_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Sombras del Abismo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPrivateInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Información privada", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createCardStrip() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, scaled(10), scaled(8)));
        panel.setBackground(PANEL_ALT);
        panel.setBorder(new EmptyBorder(scaled(5), scaled(5), scaled(5), scaled(5)));
        panel.setMinimumSize(new Dimension(0, 0));
        panel.setOpaque(true);
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
        scroller.setMinimumSize(new Dimension(0, 0));
        scroller.setBorder(BorderFactory.createEmptyBorder());
        scroller.getViewport().setBackground(PANEL_ALT);
        scroller.getViewport().setOpaque(true);
        scroller.getHorizontalScrollBar().setUnitIncrement(scaled(18));
        return scroller;
    }

    private JPanel createPanelShell(Color background) {
        JPanel panel = new JPanel();
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(scaled(10), scaled(10), scaled(10), scaled(10))));
        panel.setMinimumSize(new Dimension(0, 0));
        return panel;
    }

    private JPanel createMetricTile(String label, JLabel valueLabel) {
        JPanel tile = new JPanel(new BorderLayout(0, scaled(2)));
        tile.setBackground(PANEL_SOFT);
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 10)),
                new EmptyBorder(scaled(8), scaled(9), scaled(8), scaled(9))));
        tile.setMinimumSize(new Dimension(0, 0));

        JLabel caption = new JLabel(label.toUpperCase());
        caption.setForeground(MUTED_TEXT);
        caption.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(11)));

        valueLabel.setForeground(TEXT);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        valueLabel.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                "Turno".equals(label) ? scaledFontSize(24) : scaledFontSize(15)));

        tile.add(caption, BorderLayout.NORTH);
        tile.add(valueLabel, BorderLayout.CENTER);
        return tile;
    }

    private JScrollPane createTextScroller(
            JTextArea area,
            Color background,
            int preferredHeight,
            int minimumHeight) {
        JScrollPane scroller = new JScrollPane(
                area,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroller.setPreferredSize(new Dimension(100, preferredHeight));
        scroller.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredHeight));
        scroller.setMinimumSize(new Dimension(0, minimumHeight));
        scroller.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        scroller.getViewport().setBackground(background);
        scroller.getViewport().setOpaque(true);
        scroller.getVerticalScrollBar().setUnitIncrement(scaled(14));
        return scroller;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(226, 232, 244));
        label.setFont(new Font("Serif", Font.BOLD, scaledFontSize(17)));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createZoneTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Serif", Font.BOLD, scaledFontSize(19)));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createSubsectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED_TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(12)));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, scaled(4), 0));
        return label;
    }

    private JTextArea createLogArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setBackground(PANEL_ALT);
        area.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        area.setBorder(new EmptyBorder(scaled(10), scaled(10), scaled(10), scaled(10)));
        return area;
    }

    private JTextArea createInfoArea(int fontSize) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setBackground(PANEL_SOFT);
        area.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(fontSize)));
        area.setBorder(new EmptyBorder(scaled(8), scaled(4), 0, scaled(4)));
        return area;
    }

    private JTextArea createStatusArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setBackground(PANEL_SOFT);
        area.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(14)));
        area.setBorder(new EmptyBorder(scaled(10), scaled(10), scaled(10), scaled(10)));
        return area;
    }

    private JButton createActionButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(13)));
        button.setForeground(TEXT);
        button.setBackground(BUTTON);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 10)),
                new EmptyBorder(scaled(8), scaled(11), scaled(8), scaled(11))));
        return button;
    }

    private JLabel createHeaderLabel(int fontSize) {
        JLabel label = new JLabel();
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, scaledFontSize(fontSize)));
        return label;
    }

    private JLabel createInfoLabel() {
        JLabel label = new JLabel();
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.PLAIN, scaledFontSize(13)));
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
