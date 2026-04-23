package com.weareadaptive.pong.server;

import com.weareadaptive.pong.agent.AgentState;
import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.Header;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.UnsafeBuffer;
import src.main.resources.InputCommandDecoder;
import src.main.resources.InputCommandEncoder;
import src.main.resources.MessageHeaderDecoder;
import src.main.resources.MessageHeaderEncoder;

import java.nio.ByteBuffer;

import static com.weareadaptive.pong.Globals.*;

public class ServerAgent implements Agent
{
    private Aeron aeron;
    private Subscription subscription;
    private Publication publication;
    private final UnsafeBuffer outBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(256));

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final InputCommandDecoder inputDecoder = new InputCommandDecoder();
    private AgentState agentState = AgentState.INITIAL;

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
                    publication = aeron.addPublication(CHAT_OUTBOUND_CHANNEL, STREAM_ID);
                }

                if (subscription == null)
                {
                    subscription = aeron.addSubscription(CHAT_INBOUND_CHANNEL, STREAM_ID);
                }

                if (publication.isConnected() && subscription.isConnected())
                {
                    agentState = AgentState.STEADY;
                }
            }
            case STEADY ->
            {
                if (subscription.isConnected())
                {
                    workCount += subscription.poll(this::readInput, 10);
                }
                else
                {
                    onClose();
                }
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
        System.out.println("[Server Agent] Received input from player");
        inputDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);
        System.out.println("[Server Agent] InputType: " + inputDecoder.inputType() + " - PlayerId: " + inputDecoder.playerId());

        // For now just resend input to draw what's happening
        outBuffer.putBytes(0, buffer, offset, length);
        final long offerResult = publication.offer(outBuffer, 0, length);
        if (offerResult < 0)
        {
            System.err.println("Server publishing failed | Response Code: " + offerResult);
        }

        // TODO: Apply this input to game state
    }

    private void sendGameState()
    {
        // TODO: Send game state to players
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