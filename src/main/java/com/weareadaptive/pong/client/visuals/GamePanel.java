package com.weareadaptive.pong.client.visuals;

import com.weareadaptive.pong.client.data.BallData;
import com.weareadaptive.pong.client.data.PlayerData;
import org.agrona.collections.IntArrayList;

import javax.swing.*;
import java.awt.*;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class GamePanel extends JPanel
{
    private String score1 = "0";
    private String score2 = "0";

    private final PlayerData player1 = new PlayerData();
    private final PlayerData player2 = new PlayerData();
    private final BallData ball = new BallData();

    private final int decorationWidth = 2;
    private final int decorationHeight = 10;
    private final IntArrayList decorationPositions = new IntArrayList();

    public GamePanel()
    {
        setBackground(Color.BLACK);

        final int decorationSeparation = 10;
        for (int i = 0; i < SCREEN_HEIGHT; i += decorationHeight + decorationSeparation)
        {
            decorationPositions.add(i);
        }
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
        g.setFont(new Font("Arial", Font.PLAIN, 50));

        g.drawString(score1, (SCREEN_WIDTH / 2) - 100, 50);
        g.drawString(score2, (SCREEN_WIDTH / 2) + 75, 50);

        //Players
        g.fillRect(player1.x(),  player1.y(), player1.width(), player1.height());
        g.fillRect(player2.x(), player2.y(), player2.width(), player2.height());

        //Ball
        g.fillOval(ball.getX() - ball.getRadius(),
                ball.getY() - ball.getRadius(), ball.getRadius() * 2, ball.getRadius() * 2);


        final int decorationX = (SCREEN_WIDTH / 2) - (decorationWidth / 2);
        decorationPositions.forEach(y -> g.fillRect(decorationX, y, decorationWidth, decorationHeight));
    }
}
