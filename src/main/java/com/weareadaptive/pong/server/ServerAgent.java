package com.weareadaptive.pong.server;

import com.weareadaptive.pong.server.state.Bar;
import com.weareadaptive.pong.server.state.GameState;
import com.weareadaptive.pong.utils.AgentState;
import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.Header;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.UnsafeBuffer;
import src.main.resources.*;

import java.nio.ByteBuffer;

import static com.weareadaptive.pong.Globals.*;

public class ServerAgent implements Agent
{
    private Aeron aeron;
    private Subscription subscription;
    private Publication publication;
    private final UnsafeBuffer outBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(2048));

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final InputCommandDecoder inputDecoder = new InputCommandDecoder();
    private final GameStateEncoder gameStateEncoder = new GameStateEncoder();
    private AgentState agentState = AgentState.INITIAL;

    private final GameState gameState;
    private final String inboundChannel;
    private final String outboundChannel;
    private float deltaTime = 0;
    private long lastTimeNanos = 0;
    private GameStatus gameStatus = GameStatus.WAITING;

    public ServerAgent(final GameState gameState, final String inboundChannel, final String outboundChannel)
    {
        this.gameState = gameState;
        this.inboundChannel = inboundChannel;
        this.outboundChannel = outboundChannel;
    }

    @Override
    public void onStart()
    {
        agentState = AgentState.STARTING;
        aeron = connectAeron();
        agentState = AgentState.CONNECTING;
    }

    @Override
    public int doWork()
    {
        int workCount = 0;
        switch (agentState)
        {
            case CONNECTING ->
            {
                if (publication == null)
                {
                    publication = aeron.addPublication(outboundChannel, STREAM_ID);
                }

                if (subscription == null)
                {
                    subscription = aeron.addSubscription(inboundChannel, STREAM_ID);
                }

                System.out.println(subscription.imageCount());

                if (publication.isConnected() && subscription.isConnected() && subscription.imageCount() >= 2)
                {
                    agentState = AgentState.STEADY;
                    gameStatus = GameStatus.PLAYING;
                }
            }
            case STEADY ->
            {
                workCount += subscription.poll(this::readInput, 10);
                update();
                sendGameState();
            }
            case STOPPED ->
            {
            }
        }

        return workCount;
    }

    private void readInput(final DirectBuffer buffer, final int offset, final int length, final Header header)
    {
        inputDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);

        final short playerId = inputDecoder.playerId();
        final InputType inputType = inputDecoder.inputType();
        final Bar playerToMove = gameState.getPlayerById(playerId);
        if (playerToMove == null)
        {
            System.err.println("[Server Agent] Player id: " + playerId + " does not exist");
            return;
        }
        playerToMove.setInput(inputType);
    }

    private void update()
    {
        final long now = System.nanoTime();
        deltaTime = lastTimeNanos == 0 ? 0 : (now - lastTimeNanos) / 1_000_000_000.0f;
        lastTimeNanos = now;

        gameState.player1().update(deltaTime);
        gameState.player2().update(deltaTime);
        gameState.ball().update(deltaTime, gameState.player1(), gameState.player2());

        if (gameState.ball().getX() <= gameState.ball().getRadius())
        {
            gameState.scores().setInt(1, gameState.scores().getInt(1) + 1);
            gameState.ball().reset();
        }
        else if (gameState.ball().getX() >= SCREEN_WIDTH - gameState.ball().getRadius())
        {
            gameState.scores().setInt(0, gameState.scores().getInt(0) + 1);
            gameState.ball().reset();
        }
    }

    private void sendGameState()
    {
        gameStateEncoder.wrapAndApplyHeader(outBuffer, 0, headerEncoder);
        gameStateEncoder.player1position()
                .x(gameState.player1().x())
                .y(gameState.player1().y());
        gameStateEncoder.player1size()
                .x(gameState.player1().width())
                .y(gameState.player1().height());

        gameStateEncoder.player2position()
                .x(gameState.player2().x())
                .y(gameState.player2().y());
        gameStateEncoder.player2size()
                .x(gameState.player2().width())
                .y(gameState.player2().height());

        gameStateEncoder.ballPosition()
                .x(gameState.ball().getX())
                .y(gameState.ball().getY());
        gameStateEncoder.ballRadius(gameState.ball().getRadius());

        gameStateEncoder.player1score(gameState.scores().getFirst());
        gameStateEncoder.player2score(gameState.scores().getLast());

        gameStateEncoder.gameStatus(gameStatus);

        final int length = headerEncoder.encodedLength() + gameStateEncoder.encodedLength();
        final long offerResult = publication.offer(outBuffer, 0, length);
        if (offerResult < 0)
        {
            System.err.println("Server publishing failed | Response Code: " + offerResult);
        }
    }

    @Override
    public void onClose()
    {
        CloseHelper.close(aeron);
        agentState = AgentState.CLOSED;
    }

    @Override
    public String roleName()
    {
        return "server-agent";
    }

    private Aeron connectAeron()
    {
        final Aeron.Context aeronContext = new Aeron.Context().aeronDirectoryName(AERON_DIR_PATH);
        return Aeron.connect(aeronContext);
    }
}
