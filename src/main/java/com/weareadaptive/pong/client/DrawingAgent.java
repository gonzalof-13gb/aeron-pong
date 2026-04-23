package com.weareadaptive.pong.client;

import org.agrona.concurrent.Agent;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;

public class DrawingAgent implements Agent
{
    private final GameWindow gameWindow;

    private final OneToOneRingBuffer outerRingBuffer;

    public DrawingAgent(final OneToOneRingBuffer outerRingBuffer, final GameWindow gameWindow)
    {
        this.gameWindow = gameWindow;
        this.outerRingBuffer = outerRingBuffer;
    }

    @Override
    public int doWork()
    {
        int workCount = 0;

        gameWindow.repaint();
        return workCount;
    }

    @Override
    public String roleName()
    {
        return "drawing-agent";
    }
}
