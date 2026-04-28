package com.weareadaptive.pong.client;

import org.agrona.concurrent.Agent;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import src.main.resources.InputCommandEncoder;
import src.main.resources.InputType;
import src.main.resources.MessageHeaderEncoder;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;

public class InputAgent implements Agent
{
    private final OneToOneRingBuffer ringBuffer;

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final InputCommandEncoder inputEncoder = new InputCommandEncoder();

    private final Keyboard keyboard;
    private final short playerId;

    private final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(2048));

    private InputType lastSentInput = InputType.NULL_VAL;

    public InputAgent(final OneToOneRingBuffer ringBuffer,
                      final short playerId,
                      final Keyboard keyboard)
    {
        this.ringBuffer = ringBuffer;
        this.playerId = playerId;
        this.keyboard = keyboard;
    }

    @Override
    public int doWork()
    {
        final InputType currentInput;
        if (keyboard.isKeyPressed(KeyEvent.VK_UP))
        {
            currentInput = InputType.UP;
        }
        else if (keyboard.isKeyPressed(KeyEvent.VK_DOWN))
        {
            currentInput = InputType.DOWN;
        }
        else
        {
            currentInput = InputType.NULL_VAL;
        }

        if (currentInput != lastSentInput)
        {
            sendMessage(currentInput);
            lastSentInput = currentInput;
            return 1;
        }

        return 0;
    }

    private void sendMessage(final InputType inputType)
    {
        inputEncoder.wrapAndApplyHeader(unsafeBuffer, 0, headerEncoder);
        inputEncoder.playerId(playerId);
        inputEncoder.inputType(inputType);

        final int length = headerEncoder.encodedLength() + inputEncoder.encodedLength();
        ringBuffer.write(1, unsafeBuffer, 0, length);
    }

    @Override
    public String roleName()
    {
        return "cli-agent";
    }
}
