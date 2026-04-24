package com.weareadaptive.pong.client;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel
{
    private String score1 = "0";
    private String score2 = "0";

    private final PlayerData player1 = new PlayerData();
    private final PlayerData player2 = new PlayerData();

    public GamePanel()
    {
        setBackground(Color.BLACK);
    }

    public void drawScore(final short playerId, final int score)
    {
        if (playerId == 1) score1 = Integer.toString(score);
        else score2 = Integer.toString(score);
    }

    public void drawPlayer(final short playerId, final int x, final int y, final int width, final int height)
    {
        final PlayerData playerToDraw = playerId == 1 ? player1 : player2;
        playerToDraw.x(x);
        playerToDraw.y(y);
        playerToDraw.width(width);
        playerToDraw.height(height);
    }

    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.PLAIN, 48));
        g.drawString(score1, 275, 50);
        g.drawString(score2, 475, 50);

        g.fillRect(player1.x(),  player1.y(), player1.width(), player1.height());
        g.fillRect(player2.x(), player2.y(), player2.width(), player2.height());
    }
}
