package com.weareadaptive.pong.server;

import com.weareadaptive.pong.server.state.Ball;
import com.weareadaptive.pong.server.state.Bar;
import com.weareadaptive.pong.server.state.GameState;
import com.weareadaptive.pong.utils.AgentErrorHandler;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;

import static com.weareadaptive.pong.Globals.SCREEN_HEIGHT;
import static com.weareadaptive.pong.Globals.SCREEN_WIDTH;

public class PongServer
{
    public static void main(final String[] args)
    {
        final IdleStrategy idleStrategy = new SleepingIdleStrategy();

        final Bar player1 = new Bar(50, SCREEN_HEIGHT / 2 + 50, 20, 100);
        final Bar player2 = new Bar(SCREEN_WIDTH - 75, SCREEN_HEIGHT / 2 + 50, 20, 100);
        final Ball ball = new Ball();
        final GameState gameState = new GameState(player1, player2, ball);

        final ServerAgent serverAgent = new ServerAgent(gameState);
        final AgentRunner serverAgentRunner = new AgentRunner(idleStrategy, new AgentErrorHandler(), null, serverAgent);

        AgentRunner.startOnThread(serverAgentRunner);

        System.out.println("Server agent is running");
    }
}
