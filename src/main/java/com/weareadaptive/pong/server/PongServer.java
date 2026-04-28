package com.weareadaptive.pong.server;

import com.weareadaptive.pong.server.state.Ball;
import com.weareadaptive.pong.server.state.Bar;
import com.weareadaptive.pong.server.state.GameState;
import com.weareadaptive.pong.utils.AgentErrorHandler;
import org.agrona.collections.IntArrayList;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;

import static com.weareadaptive.pong.Globals.*;

public class PongServer
{
    public static void main(final String[] args)
    {
        final String serverIp = getLocalIp();

        final String inboundChannel = buildInboundChannel(serverIp);
        final String outboundChannel = buildOutboundChannel(serverIp);

        System.out.println("Server IP: " + serverIp);
        System.out.println("Inbound:   " + inboundChannel);
        System.out.println("Outbound:  " + outboundChannel);

        final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);

        final Ball ball = new Ball((float) (SCREEN_WIDTH / 2), (float) (SCREEN_HEIGHT / 2), 10,
                (float) (Math.random() * 3 - 1), (float) (Math.random() * 2 - 1));
        final int startY = SCREEN_HEIGHT / 2 - 50;
        final Bar player1 = new Bar(50, startY, 20, 100);
        final Bar player2 = new Bar(SCREEN_WIDTH - 75, startY, 20, 100);
        final IntArrayList scores = new IntArrayList(new int[]{0, 0}, 2, -1);
        final GameState gameState = new GameState(player1, player2, ball, scores);

        final ServerAgent serverAgent = new ServerAgent(gameState, inboundChannel, outboundChannel);
        final AgentRunner serverAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, serverAgent);

        AgentRunner.startOnThread(serverAgentRunner);

        System.out.println("Server agent is running");
    }
}
