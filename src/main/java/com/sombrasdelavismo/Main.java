package com.sombrasdelavismo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private Game game;
    private JLabel handLabel;
    private JTextArea handArea;

    public Main() {
        // Create sample cards
        CreatureCard dragon = new CreatureCard("Dragon", 5, 4, 4);
        SpellCard fireball = new SpellCard("Fireball", 3, "Deals 3 damage");
        CreatureCard goblin = new CreatureCard("Goblin", 1, 1, 1);
        SpellCard lightning = new SpellCard("Lightning Bolt", 1, "Deals 3 damage to any target");

        // Create players
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");

        // Add cards to deck
        for (int i = 0; i < 10; i++) {
            player1.addCardToDeck(dragon);
            player1.addCardToDeck(fireball);
            player1.addCardToDeck(goblin);
            player1.addCardToDeck(lightning);
        }

        game = new Game(player1, player2);
        game.startGame();

        // GUI setup
        setTitle("Sombras del Avismo");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Sombras del Avismo - " + game.getCurrentPlayer().getName() + "'s turn"));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        handLabel = new JLabel("Hand:");
        handArea = new JTextArea(10, 50);
        handArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(handArea);

        JButton drawButton = new JButton("Draw Card");
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.getCurrentPlayer().drawCard();
                updateHand();
            }
        });

        JButton nextTurnButton = new JButton("Next Turn");
        nextTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.nextTurn();
                topPanel.removeAll();
                topPanel.add(new JLabel("Sombras del Avismo - " + game.getCurrentPlayer().getName() + "'s turn"));
                topPanel.revalidate();
                topPanel.repaint();
                updateHand();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(drawButton);
        buttonPanel.add(nextTurnButton);

        centerPanel.add(handLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        updateHand();
    }

    private void updateHand() {
        StringBuilder sb = new StringBuilder();
        for (Card card : game.getCurrentPlayer().getHand()) {
            sb.append(card.getName()).append(" (").append(card.getType()).append(", Cost: ").append(card.getCost()).append(")\n");
        }
        handArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}