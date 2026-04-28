package com.weareadaptive.pong.client;

import com.weareadaptive.pong.client.visuals.GamePanel;
import com.weareadaptive.pong.client.visuals.WaitingPanel;
import src.main.resources.GameStatus;

import javax.swing.*;
import java.awt.*;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class GameWindow
{
    private static final String CARD_WAITING = "waiting";
    private static final String CARD_GAME = "game";

    private final JFrame frame;
    private final JPanel container;
    private final GamePanel gamePanel;

    public GameWindow(final Keyboard keyboard)
    {
        frame = new JFrame("Pong");
        gamePanel = new GamePanel();

        container = new JPanel(new CardLayout());
        container.add(new WaitingPanel(), CARD_WAITING);
        container.add(gamePanel, CARD_GAME);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.add(container);
        frame.addKeyListener(keyboard);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    public void setGameStatus(final GameStatus status)
    {
        final CardLayout layout = (CardLayout) container.getLayout();
        layout.show(container, status == GameStatus.PLAYING ? CARD_GAME : CARD_WAITING);
    }

    public void drawScore(final short playerId, final int score)
    {
        gamePanel.drawScore(playerId, score);
    }

    public void drawPlayer(final short playerId, final int x, final int y, final int width, final int height)
    {
        gamePanel.drawPlayer(playerId, x, y, width, height);
    }

    public void drawBall(final int x, final int y, final int radius)
    {
        gamePanel.drawBall(x, y, radius);
    }

    public void repaint()
    {
        gamePanel.repaint();
    }
}
