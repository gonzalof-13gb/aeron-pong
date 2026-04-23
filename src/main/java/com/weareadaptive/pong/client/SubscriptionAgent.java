package com.weareadaptive.pong.client;

import com.weareadaptive.pong.utils.AgentState;
import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.Header;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;

import static com.weareadaptive.pong.Globals.*;

public class SubscriptionAgent implements Agent
{
    private Aeron aeron;
    private Subscription subscription;

    private AgentState agentState = AgentState.INITIAL;

    private final OneToOneRingBuffer outerRingBuffer;

    public SubscriptionAgent(final OneToOneRingBuffer outerRingBuffer)
    {
        this.outerRingBuffer = outerRingBuffer;
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
        System.out.println("[Subscription Agent] received message from server, writing it to outer ring buffer");
        outerRingBuffer.write(1, buffer, offset, length);
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