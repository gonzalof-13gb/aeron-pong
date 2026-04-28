package com.weareadaptive.pong.client.visuals;

import javax.swing.*;
import java.awt.*;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class WaitingPanel extends JPanel
{
    public WaitingPanel()
    {
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));

        final String message = "Waiting for players...";
        final FontMetrics fm = g.getFontMetrics();
        final int x = (SCREEN_WIDTH - fm.stringWidth(message)) / 2;
        final int y = SCREEN_HEIGHT / 2;
        g.drawString(message, x, y);
    }
}
