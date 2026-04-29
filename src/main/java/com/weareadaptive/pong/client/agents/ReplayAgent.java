package com.weareadaptive.pong.client.agents;

import com.weareadaptive.pong.client.visuals.GameWindow;
import io.aeron.Subscription;
import io.aeron.logbuffer.Header;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import src.main.resources.GameStateDecoder;
import src.main.resources.GameStatus;
import src.main.resources.MessageHeaderDecoder;

public class ReplayAgent implements Agent
{
    private final Subscription replaySubscription;
    private final GameWindow gameWindow;
    private final Runnable onComplete;
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final GameStateDecoder gameStateDecoder = new GameStateDecoder();
    private boolean started = false;
    private boolean done = false;

    public ReplayAgent(final Subscription replaySubscription, final GameWindow gameWindow, final Runnable onComplete)
    {
        this.replaySubscription = replaySubscription;
        this.gameWindow = gameWindow;
        this.onComplete = onComplete;
    }

    @Override
    public int doWork()
    {
        if (done)
        {
            return 0;
        }

        final int workCount = replaySubscription.poll(this::onFrame, 10);
        if (workCount > 0)
        {
            started = true;
            gameWindow.repaint();
        }

        if (workCount == 0 && started && !done && replaySubscription.imageCount() == 0)
        {
            complete();
        }

        return workCount;
    }

    private void onFrame(final DirectBuffer buffer, final int offset, final int length, final Header header)
    {
        gameStateDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);

        gameWindow.drawPlayer((short) 1,
                gameStateDecoder.player1position().x(), gameStateDecoder.player1position().y(),
                gameStateDecoder.player1size().x(), gameStateDecoder.player1size().y());

        gameWindow.drawPlayer((short) 2,
                gameStateDecoder.player2position().x(), gameStateDecoder.player2position().y(),
                gameStateDecoder.player2size().x(), gameStateDecoder.player2size().y());

        gameWindow.drawBall(
                gameStateDecoder.ballPosition().x(),
                gameStateDecoder.ballPosition().y(),
                gameStateDecoder.ballRadius());

        final int score1 = gameStateDecoder.player1score();
        final int score2 = gameStateDecoder.player2score();
        gameWindow.drawScore((short) 1, score1);
        gameWindow.drawScore((short) 2, score2);

        if (gameStateDecoder.gameStatus() == GameStatus.END)
        {
            final short winner = score1 >= score2 ? (short) 1 : (short) 2;
            done = true;
            gameWindow.showEndGame(winner, score1, score2);
        }
    }

    private void complete()
    {
        done = true;
        onComplete.run();
    }

    @Override
    public void onClose()
    {
        CloseHelper.close(replaySubscription);
    }

    @Override
    public String roleName()
    {
        return "replay-agent";
    }
}