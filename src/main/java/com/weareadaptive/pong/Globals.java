package com.weareadaptive.pong;

import io.aeron.ChannelUriStringBuilder;
import io.aeron.CommonContext;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Globals
{
    public static int STREAM_ID = 10;
    public static String AERON_DIR_PATH = "/Volumes/DevShm/aeron-training";
    public static String ARCHIVE_DIR_PATH = "./pong-archive";
    public static String ARCHIVE_CONTROL_CHANNEL = "aeron:ipc";
    public static int ARCHIVE_CONTROL_STREAM_ID = 10001;
    public static int ARCHIVE_CONTROL_RESPONSE_STREAM_ID = 10002;

    public static String buildInboundChannel(final String serverIp)
    {
        return new ChannelUriStringBuilder()
                .media(CommonContext.UDP_MEDIA)
                .endpoint(serverIp + ":8999")
                .alias("ChatInboundChannel")
                .build();
    }

    public static String buildOutboundChannel(final String serverIp)
    {
        return new ChannelUriStringBuilder()
                .media(CommonContext.UDP_MEDIA)
                .controlMode(CommonContext.MDC_CONTROL_MODE_DYNAMIC)
                .controlEndpoint(serverIp + ":9000")
                .sessionId(50)
                .alias("ChatOutboundChannel")
                .build();
    }

    public static String getLocalIp()
    {
        try (final DatagramSocket socket = new DatagramSocket())
        {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        }
        catch (final Exception e)
        {
            return "127.0.0.1";
        }
    }

    public static int SCREEN_WIDTH = 1000;
    public static int SCREEN_HEIGHT = 600;
}
