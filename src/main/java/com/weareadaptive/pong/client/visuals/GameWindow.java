package com.weareadaptive.pong.client.visuals;

import com.weareadaptive.pong.client.Keyboard;
import src.main.resources.GameStatus;

import javax.swing.*;
import java.awt.*;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class GameWindow
{
    private static final String CARD_MENU = "menu";
    private static final String CARD_WAITING = "waiting";
    private static final String CARD_GAME = "game";
    private static final String CARD_ENDGAME = "endgame";

    private final JPanel container;
    private final GamePanel gamePanel;
    private final EndGamePanel endGamePanel;

    public GameWindow(final Keyboard keyboard, final Runnable onConnect)
    {
        final JFrame frame = new JFrame("Pong");
        gamePanel = new GamePanel();
        endGamePanel = new EndGamePanel(onConnect, this::showMenu);

        container = new JPanel(new CardLayout());
        container.add(new MenuPanel(() -> onPlay(onConnect)), CARD_MENU);
        container.add(new WaitingPanel(), CARD_WAITING);
        container.add(gamePanel, CARD_GAME);
        container.add(endGamePanel, CARD_ENDGAME);

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
        if (status == GameStatus.PLAYING)
        {
            SwingUtilities.invokeLater(() -> cardLayout().show(container, CARD_GAME));
        }
        else if (status == GameStatus.WAITING)
        {
            SwingUtilities.invokeLater(() -> cardLayout().show(container, CARD_WAITING));
        }
    }

    public void showEndGame(final short winner, final int score1, final int score2)
    {
        SwingUtilities.invokeLater(() ->
        {
            endGamePanel.setResult(winner, score1, score2);
            cardLayout().show(container, CARD_ENDGAME);
        });
    }

    public void showMenu()
    {
        SwingUtilities.invokeLater(() -> cardLayout().show(container, CARD_MENU));
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

    private CardLayout cardLayout()
    {
        return (CardLayout) container.getLayout();
    }

    private void onPlay(final Runnable onConnect)
    {
        onConnect.run();
        cardLayout().show(container, CARD_WAITING);
    }
}
