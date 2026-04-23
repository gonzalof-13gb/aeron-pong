package com.weareadaptive.pong.client;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import src.main.resources.GameStateDecoder;
import src.main.resources.MessageHeaderDecoder;

public class DrawingAgent implements Agent
{
    private final GameWindow gameWindow;

    private final OneToOneRingBuffer outerRingBuffer;
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();

    private final GameStateDecoder gameStateDecoder = new GameStateDecoder();

    public DrawingAgent(final OneToOneRingBuffer outerRingBuffer, final GameWindow gameWindow)
    {
        this.gameWindow = gameWindow;
        this.outerRingBuffer = outerRingBuffer;
    }

    @Override
    public int doWork()
    {
        int workCount = 0;

        workCount += outerRingBuffer.read(this::readAndDrawGameState);

        gameWindow.repaint();
        return workCount;
    }

    private void readAndDrawGameState(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
    {
        //System.out.println("[Drawing Agent] received message from outer ring buffer");

        gameStateDecoder.wrapAndApplyHeader(buffer, index, headerDecoder);

        final int player1x = gameStateDecoder.player1position().x();
        final int player1y = gameStateDecoder.player1position().y();
        final int player1w = gameStateDecoder.player1size().x();
        final int player1h = gameStateDecoder.player1size().y();
        gameWindow.drawPlayer((short)1, player1x, player1y, player1w, player1h);

        final int player2x = gameStateDecoder.player2position().x();
        final int player2y = gameStateDecoder.player2position().y();
        final int player2w = gameStateDecoder.player2size().x();
        final int player2h = gameStateDecoder.player2size().y();
        gameWindow.drawPlayer((short)2, player2x, player2y, player2w, player2h);

        final int ballX = gameStateDecoder.ballPosition().x();
        final int ballY = gameStateDecoder.ballPosition().y();
        final int ballRadius = gameStateDecoder.ballRadius();
        gameWindow.drawBall(ballX, ballY, ballRadius);
    }

    @Override
    public String roleName()
    {
        return "drawing-agent";
    }
}
