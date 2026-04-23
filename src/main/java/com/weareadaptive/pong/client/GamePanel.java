package com.weareadaptive.pong.client;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel
{
    private volatile String text = "";

    public GamePanel()
    {
        setBackground(Color.BLACK);
    }

    public void drawText(final String text)
    {
        this.text = text;
        repaint();
    }

    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 24));
        g.drawString(text, 350, 300);
    }
}
