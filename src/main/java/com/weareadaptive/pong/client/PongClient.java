package com.weareadaptive.pong.client;

import com.weareadaptive.pong.agent.AgentErrorHandler;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

import javax.swing.*;
import java.nio.ByteBuffer;

public class PongClient
{
    private static short PLAYER_ID = 1;

    public static void main(final String[] args)
    {
        final IdleStrategy idleStrategy = new BackoffIdleStrategy();

        final int bufferLength = 256 + RingBufferDescriptor.TRAILER_LENGTH;
        final OneToOneRingBuffer innerRingBuffer = new OneToOneRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength)));
        final OneToOneRingBuffer outerRingBuffer = new OneToOneRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength)));

        final Keyboard keyboard = new Keyboard();
        final GameWindow gameWindow = new GameWindow(keyboard);

        System.out.println("Setup InputAgent");
        final InputAgent inputAgent = new InputAgent(innerRingBuffer, PLAYER_ID, keyboard);
        final AgentRunner inputAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, inputAgent);

        System.out.println("Setup DrawingAgent");
        final DrawingAgent drawingAgent = new DrawingAgent(outerRingBuffer, gameWindow);
        final AgentRunner drawingAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, drawingAgent);

        System.out.println("Setup PublishingAgent");
        final PublishingAgent publishingAgent = new PublishingAgent(innerRingBuffer);
        final AgentRunner publishingAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, publishingAgent);

        System.out.println("Setup SubscriptionAgent");
        final SubscriptionAgent subscriptionAgent = new SubscriptionAgent(outerRingBuffer);
        final AgentRunner subscriptionAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, subscriptionAgent);

        System.out.println("Start agent runners");
        AgentRunner.startOnThread(inputAgentRunner);
        AgentRunner.startOnThread(drawingAgentRunner);
        AgentRunner.startOnThread(publishingAgentRunner);
        AgentRunner.startOnThread(subscriptionAgentRunner);
    }


}
