package com.weareadaptive.pong.client;

import javax.swing.*;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class GameWindow
{
    private final JFrame frame;
    private final GamePanel panel;

    public GameWindow(final Keyboard keyboard)
    {
        frame = new JFrame("Pong");
        panel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
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

    public void drawPlayer(final short playerId, final int x, final int y, final int width, final int height)
    {
        panel.drawPlayer(playerId, x, y, width, height);
    }

    public void repaint()
    {
        panel.repaint();
    }
}
