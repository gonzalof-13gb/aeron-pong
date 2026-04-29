package com.weareadaptive.pong.client.agents;

import com.weareadaptive.pong.utils.AgentState;
import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.driver.MediaDriver;
import org.agrona.CloseHelper;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.weareadaptive.pong.Globals.*;

public class PublishingAgent implements Agent
{
    private Aeron aeron;
    private Publication publication;

    private AgentState agentState = AgentState.INITIAL;

    private final OneToOneRingBuffer ringBuffer;
    private final String inboundChannel;
    private final AtomicBoolean gameOver;

    public PublishingAgent(final OneToOneRingBuffer ringBuffer, final String inboundChannel, final AtomicBoolean gameOver)
    {
        this.ringBuffer = ringBuffer;
        this.inboundChannel = inboundChannel;
        this.gameOver = gameOver;
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
                    publication = aeron.addPublication(inboundChannel, STREAM_ID);
                }
                else if (publication.isConnected())
                {
                    agentState = AgentState.STEADY;
                }
            }
            case STEADY ->
            {
                if (gameOver.get())
                {
                    onClose();
                }
                else if (publication.isConnected())
                {
                    workCount += ringBuffer.read(this::readAndOfferMessage);
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

    private void readAndOfferMessage(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
    {
        final long offer = publication.offer(buffer, index, length);
        if (offer < 0)
        {
            System.err.println("Client publishing failed | Response Code: " + offer);
        }
    }

    @Override
    public void onClose()
    {
        CloseHelper.closeAll(aeron, publication);
        agentState = AgentState.CLOSED;
    }

    @Override
    public String roleName()
    {
        return "publishing-agent";
    }

    private Aeron connectAeron()
    {
        MediaDriver.Context driverCtx = new MediaDriver.Context().dirDeleteOnStart(true);
        MediaDriver driver = MediaDriver.launchEmbedded(driverCtx);

        Aeron.Context aeronCtx = new Aeron.Context()
                .aeronDirectoryName(driver.aeronDirectoryName());
        return Aeron.connect(aeronCtx);
    }
}
