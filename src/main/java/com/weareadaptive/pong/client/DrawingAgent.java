package com.weareadaptive.pong.client;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import src.main.resources.InputCommandDecoder;
import src.main.resources.InputType;
import src.main.resources.MessageHeaderDecoder;

public class DrawingAgent implements Agent
{
    private final GameWindow gameWindow;

    private final OneToOneRingBuffer outerRingBuffer;
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();

    // TODO: Delete
    private final InputCommandDecoder inputCommandDecoder = new InputCommandDecoder();

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
        System.out.println("[Drawing Agent] received message from outer ring buffer");

        inputCommandDecoder.wrapAndApplyHeader(buffer, index, headerDecoder);

        final InputType inputType = inputCommandDecoder.inputType();
        final short playerId = inputCommandDecoder.playerId();

        gameWindow.drawText(inputType.name());
    }

    @Override
    public String roleName()
    {
        return "drawing-agent";
    }
}
