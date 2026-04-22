package com.weareadaptive.pong.client;

import com.weareadaptive.pong.agent.AgentState;
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

    private final Keyboard keyboard = new Keyboard();
    private final short playerId;

    private UnsafeBuffer unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(4096));
    private AgentState agentState = AgentState.INITIAL;

    public InputAgent(final OneToOneRingBuffer ringBuffer, final short playerId)
    {
        this.ringBuffer = ringBuffer;
        this.playerId = playerId;
    }

    @Override
    public int doWork()
    {
        int workCount = 0;

        switch (agentState)
        {
            case INITIAL ->
            {
                if (keyboard.isKeyPressed(KeyEvent.VK_UP))
                {
                    System.out.println("Up Pressed");
                }
            }
        }

        return workCount;
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
    public void onClose()
    {
        agentState = AgentState.CLOSED;
    }

    @Override
    public String roleName()
    {
        return "cli-agent";
    }
}
