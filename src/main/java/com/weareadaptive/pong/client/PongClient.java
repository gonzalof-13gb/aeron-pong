package com.weareadaptive.pong.client;

import com.weareadaptive.pong.utils.AgentErrorHandler;
import org.agrona.concurrent.*;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;

import static com.weareadaptive.pong.Globals.*;

public class PongClient
{
    public static void main(final String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("[Pong Client] Missing player id input argument in main() method");
            System.exit(1);
        }
        final short playerId = (short) Integer.parseInt(args[0]);

        final String serverIp = askServerIp();
        if (serverIp == null)
        {
            System.exit(0);
        }

        final String inboundChannel = buildInboundChannel(serverIp);
        final String outboundChannel = buildOutboundChannel(serverIp);

        final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);

        final int bufferLength = 2048 + RingBufferDescriptor.TRAILER_LENGTH;
        final OneToOneRingBuffer innerRingBuffer = new OneToOneRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength)));
        final OneToOneRingBuffer outerRingBuffer = new OneToOneRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength)));

        final Keyboard keyboard = new Keyboard();
        final GameWindow gameWindow = new GameWindow(keyboard);

        System.out.println("Setup InputAgent");
        final InputAgent inputAgent = new InputAgent(innerRingBuffer, playerId, keyboard);
        final AgentRunner inputAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, inputAgent);

        System.out.println("Setup DrawingAgent");
        final DrawingAgent drawingAgent = new DrawingAgent(outerRingBuffer, gameWindow);
        final AgentRunner drawingAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, drawingAgent);

        System.out.println("Setup PublishingAgent");
        final PublishingAgent publishingAgent = new PublishingAgent(innerRingBuffer, inboundChannel);
        final AgentRunner publishingAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, publishingAgent);

        System.out.println("Setup SubscriptionAgent");
        final SubscriptionAgent subscriptionAgent = new SubscriptionAgent(outerRingBuffer, outboundChannel);
        final AgentRunner subscriptionAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, subscriptionAgent);

        System.out.println("Start agent runners");
        AgentRunner.startOnThread(inputAgentRunner);
        AgentRunner.startOnThread(drawingAgentRunner);
        AgentRunner.startOnThread(publishingAgentRunner);
        AgentRunner.startOnThread(subscriptionAgentRunner);
    }

    private static String askServerIp()
    {
        final JTextField ipField = new JTextField(getLocalIp(), 20);

        final JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.add(new JLabel("Server IP address:"));
        panel.add(ipField);

        final int result = JOptionPane.showConfirmDialog(
                null, panel, "Aeron Pong - Connect",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
        {
            return null;
        }

        final String ip = ipField.getText().trim();
        return ip.isEmpty() ? null : ip;
    }
}
