package com.weareadaptive.pong.client;

import com.weareadaptive.pong.server.state.Ball;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel
{
    private String text = "";

    private final PlayerData player1 = new PlayerData();
    private final PlayerData player2 = new PlayerData();
    private final BallData ball = new BallData();

    public GamePanel()
    {
        setBackground(Color.BLACK);
    }

    public void drawText(final String text)
    {
        this.text = text;
        repaint();
    }

    public void drawPlayer(final short playerId, final int x, final int y, final int width, final int height)
    {
        final PlayerData playerToDraw = playerId == 1 ? player1 : player2;
        playerToDraw.x(x);
        playerToDraw.y(y);
        playerToDraw.width(width);
        playerToDraw.height(height);
    }

    public void drawBall(final int x, final int y, final int radius)
    {
        ball.setX(x);
        ball.setY(y);
        ball.setRadius(radius);
        repaint();
    }


    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        //Players
        System.out.println(player1.x() + " " + player1.y() + " " + player1.width() + " " + player1.height());
        g.fillRect(player1.x(),  player1.y(), player1.width(), player1.height());
        g.fillRect(player2.x(), player2.y(), player2.width(), player2.height());

        //Ball
        g.fillOval(ball.getX() - ball.getRadius(),
                ball.getY() - ball.getRadius(), ball.getRadius() * 2, ball.getRadius() * 2);

    }
}
