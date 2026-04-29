package com.weareadaptive.pong.client.visuals;

import javax.swing.*;
import java.awt.*;

public class EndGamePanel extends JPanel
{
    private final JLabel winnerLabel;
    private final JLabel scoreLabel;

    public EndGamePanel(final Runnable onPlayAgain, final Runnable onMenu)
    {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        winnerLabel = new JLabel("Player wins!");
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 52));
        gbc.gridy = 0;
        add(winnerLabel, gbc);

        scoreLabel = new JLabel("0 - 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        gbc.gridy = 1;
        add(scoreLabel, gbc);

        final JButton playAgainButton = createButton("Play Again");
        playAgainButton.addActionListener(e -> onPlayAgain.run());
        gbc.gridy = 2;
        add(playAgainButton, gbc);

        final JButton menuButton = createButton("Main Menu");
        menuButton.addActionListener(e -> onMenu.run());
        gbc.gridy = 3;
        add(menuButton, gbc);
    }

    public void setResult(final short winner, final int score1, final int score2)
    {
        winnerLabel.setText("Player " + winner + " wins!");
        scoreLabel.setText(score1 + " - " + score2);
    }

    private JButton createButton(final String text)
    {
        final JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 26));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(220, 55));
        return button;
    }
}
