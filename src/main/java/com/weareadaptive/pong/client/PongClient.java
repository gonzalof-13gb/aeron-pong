package com.weareadaptive.pong.client;

import com.weareadaptive.pong.client.agents.DrawingAgent;
import com.weareadaptive.pong.client.agents.InputAgent;
import com.weareadaptive.pong.client.agents.PublishingAgent;
import com.weareadaptive.pong.client.agents.SubscriptionAgent;
import com.weareadaptive.pong.client.visuals.GameWindow;
import com.weareadaptive.pong.utils.AgentErrorHandler;
import org.agrona.CloseHelper;
import org.agrona.concurrent.*;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.weareadaptive.pong.Globals.*;

public class PongClient
{
    private final short playerId;
    private final Keyboard keyboard;
    private GameWindow gameWindow;

    private AgentRunner inputRunner;
    private AgentRunner drawingRunner;
    private AgentRunner publishingRunner;
    private AgentRunner subscriptionRunner;

    public PongClient(final short playerId, final Keyboard keyboard)
    {
        this.playerId = playerId;
        this.keyboard = keyboard;
    }

    public void setGameWindow(final GameWindow gw)
    {
        this.gameWindow = gw;
    }

    public void connect()
    {
        stopAgents();

        final String serverIp = askServerIp();
        if (serverIp == null)
        {
            return;
        }

        final String inboundChannel = buildInboundChannel(serverIp);
        final String outboundChannel = buildOutboundChannel(serverIp);

        final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);
        final int bufferLength = 2048 + RingBufferDescriptor.TRAILER_LENGTH;

        final OneToOneRingBuffer innerRingBuffer = new OneToOneRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength)));
        final OneToOneRingBuffer outerRingBuffer = new OneToOneRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength)));

        final AtomicBoolean gameOver = new AtomicBoolean(false);

        final InputAgent inputAgent = new InputAgent(innerRingBuffer, playerId, keyboard);
        final DrawingAgent drawingAgent = new DrawingAgent(outerRingBuffer, gameWindow, gameOver);
        final PublishingAgent publishingAgent = new PublishingAgent(innerRingBuffer, inboundChannel, gameOver);
        final SubscriptionAgent subscriptionAgent = new SubscriptionAgent(outerRingBuffer, outboundChannel, gameOver);

        inputRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, inputAgent);
        drawingRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, drawingAgent);
        publishingRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, publishingAgent);
        subscriptionRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, subscriptionAgent);

        AgentRunner.startOnThread(inputRunner);
        AgentRunner.startOnThread(drawingRunner);
        AgentRunner.startOnThread(publishingRunner);
        AgentRunner.startOnThread(subscriptionRunner);
    }

    private void stopAgents()
    {
        CloseHelper.closeAll(inputRunner, drawingRunner, publishingRunner, subscriptionRunner);
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

    public static void main(final String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("[Pong Client] Missing player id input argument in main() method");
            System.exit(1);
        }
        final short playerId = (short) Integer.parseInt(args[0]);

        final Keyboard keyboard = new Keyboard();
        final PongClient client = new PongClient(playerId, keyboard);
        final GameWindow[] ref = {null};
        final GameWindow gameWindow = new GameWindow(keyboard, client::connect,
                () -> new ReplayClient(ref[0]).loadAndShowRecordings());
        ref[0] = gameWindow;
        client.setGameWindow(gameWindow);
    }
}
