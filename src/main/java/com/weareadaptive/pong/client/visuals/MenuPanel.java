package com.weareadaptive.pong.client.visuals;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel
{
    public MenuPanel(final Runnable onPlay)
    {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        final JLabel title = new JLabel("PONG");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 80));
        gbc.gridy = 0;
        add(title, gbc);

        final JButton playButton = createButton("Play");
        playButton.addActionListener(e -> onPlay.run());
        gbc.gridy = 1;
        add(playButton, gbc);

        final JButton replaysButton = createButton("Replays");
        replaysButton.setEnabled(false);
        replaysButton.setForeground(Color.GRAY);
        replaysButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridy = 2;
        add(replaysButton, gbc);
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
