package com.weareadaptive.pong.client;

import javax.swing.*;

public class GameWindow
{
    private final JFrame frame;
    private final GamePanel panel;

    public GameWindow(final Keyboard keyboard)
    {
        frame = new JFrame("Pong");
        panel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.add(panel);
        frame.addKeyListener(keyboard);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    public void drawText(final String text)
    {
        panel.drawText(text);
    }

    public void repaint()
    {
        panel.repaint();
    }
}
