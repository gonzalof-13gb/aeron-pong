package com.weareadaptive.pong.client.visuals;

import com.weareadaptive.pong.client.Keyboard;
import com.weareadaptive.pong.client.visuals.ReplayListPanel.RecordingEntry;
import src.main.resources.GameStatus;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class GameWindow
{
    private static final String CARD_MENU = "menu";
    private static final String CARD_WAITING = "waiting";
    private static final String CARD_GAME = "game";
    private static final String CARD_ENDGAME = "endgame";
    private static final String CARD_REPLAY_LIST = "replaylist";

    private final JPanel container;
    private final GamePanel gamePanel;
    private final EndGamePanel endGamePanel;
    private final ReplayListPanel replayListPanel;

    public GameWindow(final Keyboard keyboard, final Runnable onConnect, final Runnable onReplays)
    {
        final JFrame frame = new JFrame("Pong");
        gamePanel = new GamePanel();
        endGamePanel = new EndGamePanel(onConnect, this::showMenu);
        replayListPanel = new ReplayListPanel(this::showMenu);

        container = new JPanel(new CardLayout());
        container.add(new MenuPanel(() -> onPlay(onConnect), onReplays), CARD_MENU);
        container.add(new WaitingPanel(), CARD_WAITING);
        container.add(gamePanel, CARD_GAME);
        container.add(endGamePanel, CARD_ENDGAME);
        container.add(replayListPanel, CARD_REPLAY_LIST);

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

    public void showReplayList()
    {
        SwingUtilities.invokeLater(() ->
        {
            replayListPanel.showLoading();
            cardLayout().show(container, CARD_REPLAY_LIST);
        });
    }

    public void showEmptyRecordings()
    {
        SwingUtilities.invokeLater(replayListPanel::showEmpty);
    }

    public void showRecordings(final List<RecordingEntry> entries, final Consumer<RecordingEntry> onSelect)
    {
        replayListPanel.showRecordings(entries, onSelect);
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
