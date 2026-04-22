package com.weareadaptive.pong.client;

import com.weareadaptive.pong.agent.AgentState;
import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.Header;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import src.main.resources.InputCommandDecoder;
import src.main.resources.InputCommandEncoder;
import src.main.resources.MessageHeaderDecoder;

import static com.weareadaptive.pong.Globals.*;

public class SubscriptionAgent implements Agent
{
    private Aeron aeron;
    private Subscription subscription;

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
                if (subscription == null)
                {
                    subscription = aeron.addSubscription(CHAT_OUTBOUND_CHANNEL, STREAM_ID);
                }
                else if (subscription.isConnected())
                {
                    agentState = AgentState.STEADY;
                }
            }
            case STEADY ->
            {
                if (subscription.isConnected())
                {
                    workCount = subscription.poll(this::handleFragment, 10);
                }
                else
                {
                    onClose();
                }
            }
            case STOPPED ->
            {
            }
        }
        return workCount;
    }


    private void handleFragment(final DirectBuffer buffer, final int offset, final int length, final Header header)
    {
        // Decode SBE message
        headerDecoder.wrap(buffer, offset);

        final int actingBlockLength = headerDecoder.blockLength();
        final int actingVersion = headerDecoder.version();

        final int totalOffset = headerDecoder.encodedLength() + offset;
        inputDecoder.wrap(buffer, totalOffset, actingBlockLength, actingVersion);

        // TODO: Write to inner ring buffer and manage in visual agent
//        final String message = inputDecoder.message();
//        final long netTimestamp = inputDecoder.netTimestamp();
//        final long inputTimestamp = inputDecoder.inputTimestamp();
//        final long serverTimestamp = inputDecoder.serverTimestamp();
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
        return "subscription-agent";
    }

    private Aeron connectAeron()
    {
        final Aeron.Context aeronContext = new Aeron.Context().aeronDirectoryName(AERON_DIR_PATH);
        return Aeron.connect(aeronContext);
    }
}