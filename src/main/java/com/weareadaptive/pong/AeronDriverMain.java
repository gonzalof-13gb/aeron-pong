package com.weareadaptive.pong;

import io.aeron.archive.Archive;
import io.aeron.archive.ArchivingMediaDriver;
import io.aeron.driver.MediaDriver;
import org.agrona.concurrent.ShutdownSignalBarrier;

import static com.weareadaptive.pong.Globals.*;

public class AeronDriverMain
{
    public static void main(final String[] args)
    {
        System.out.println("Start Archiving Media Driver");
        System.out.println("Archive dir: " + ARCHIVE_DIR_PATH);

        final MediaDriver.Context driverCtx = new MediaDriver.Context()
                .aeronDirectoryName(AERON_DIR_PATH)
                .dirDeleteOnStart(true);

        final String serverIp = getLocalIp();

        final Archive.Context archiveCtx = new Archive.Context()
                .aeronDirectoryName(AERON_DIR_PATH)
                .archiveDirectoryName(ARCHIVE_DIR_PATH)
                .controlChannel(buildArchiveControlChannel(serverIp))
                .controlStreamId(ARCHIVE_CONTROL_STREAM_ID)
                .replicationChannel("aeron:udp?endpoint=" + serverIp + ":0");

        try (final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
             final ArchivingMediaDriver ignored = ArchivingMediaDriver.launch(driverCtx, archiveCtx))
        {
            barrier.await();
        }
    }
}