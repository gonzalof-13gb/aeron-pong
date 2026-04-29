package com.weareadaptive.pong.client;

import com.weareadaptive.pong.client.agents.ReplayAgent;
import com.weareadaptive.pong.client.visuals.GameWindow;
import com.weareadaptive.pong.client.visuals.ReplayListPanel.RecordingEntry;
import com.weareadaptive.pong.utils.AgentErrorHandler;
import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.archive.client.AeronArchive;
import org.agrona.CloseHelper;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import src.main.resources.GameStatus;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.weareadaptive.pong.Globals.*;

public class ReplayClient
{
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final GameWindow gameWindow;
    private Aeron aeron;
    private AeronArchive aeronArchive;
    private AgentRunner replayRunner;
    private String serverIp;

    public ReplayClient(final GameWindow gameWindow)
    {
        this.gameWindow = gameWindow;
    }

    public void loadAndShowRecordings()
    {
        serverIp = askServerIp();
        if (serverIp == null)
        {
            return;
        }

        gameWindow.showReplayList();
        new Thread(() ->
        {
            try
            {
                connect();
                final List<RecordingEntry> entries = listRecordings();
                if (entries.isEmpty())
                {
                    gameWindow.showEmptyRecordings();
                }
                else
                {
                    gameWindow.showRecordings(entries, this::startReplay);
                }
            }
            catch (final Exception e)
            {
                System.err.println("[Replay] Failed to load recordings: " + e.getMessage());
                gameWindow.showMenu();
            }
        }, "replay-list-thread").start();
    }

    public void close()
    {
        CloseHelper.close(replayRunner);
        replayRunner = null;
        CloseHelper.close(aeronArchive);
        CloseHelper.close(aeron);
        aeronArchive = null;
        aeron = null;
    }

    private void connect()
    {
        if (aeron != null)
        {
            return;
        }
        final String localIp = getLocalIp();
        aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(AERON_DIR_PATH));
        aeronArchive = AeronArchive.connect(new AeronArchive.Context()
                .aeron(aeron)
                .controlRequestChannel(buildArchiveControlChannel(serverIp))
                .controlRequestStreamId(ARCHIVE_CONTROL_STREAM_ID)
                .controlResponseChannel(buildArchiveClientResponseChannel(localIp))
                .controlResponseStreamId(ARCHIVE_CLIENT_RESPONSE_STREAM_ID));
    }

    private List<RecordingEntry> listRecordings()
    {
        final List<RecordingEntry> entries = new ArrayList<>();
        aeronArchive.listRecordings(0, Integer.MAX_VALUE,
                (controlSessionId, correlationId, recordingId,
                 startTimestamp, stopTimestamp, startPosition, stopPosition,
                 initialTermId, segmentFileLength, termBufferLength, mtuLength,
                 sessionId, streamId, strippedChannel, originalChannel, sourceIdentity) ->
                {
                    if (stopTimestamp <= 0)
                    {
                        return;
                    }
                    final String dateTime = DATE_FMT.format(Instant.ofEpochMilli(startTimestamp));
                    final double durationSeconds = (stopTimestamp - startTimestamp) / 1000.0;
                    entries.add(new RecordingEntry(recordingId, startPosition, stopPosition, dateTime, durationSeconds));
                });
        return entries;
    }

    private void startReplay(final RecordingEntry entry)
    {
        CloseHelper.close(replayRunner);

        final String localIp = getLocalIp();
        final String replayChannel = buildArchiveReplayChannel(localIp);
        final long length = entry.stopPosition() - entry.startPosition();

        aeronArchive.startReplay(
                entry.recordingId(), entry.startPosition(), length,
                replayChannel, ARCHIVE_REPLAY_STREAM_ID);

        System.out.println("[Replay] Starting replay | recordingId=" + entry.recordingId()
                + " channel=" + replayChannel + " length=" + length + "b");

        final Subscription subscription = aeron.addSubscription(replayChannel, ARCHIVE_REPLAY_STREAM_ID);

        gameWindow.setGameStatus(GameStatus.PLAYING);

        final ReplayAgent replayAgent = new ReplayAgent(subscription, gameWindow, gameWindow::showMenu);
        replayRunner = new AgentRunner(
                new SleepingMillisIdleStrategy(1), new AgentErrorHandler(), null, replayAgent);
        AgentRunner.startOnThread(replayRunner);
    }

    private static String askServerIp()
    {
        final JTextField ipField = new JTextField(getLocalIp(), 20);
        final JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.add(new JLabel("Archive server IP:"));
        panel.add(ipField);

        final int result = JOptionPane.showConfirmDialog(
                null, panel, "Aeron Pong - Replays",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
        {
            return null;
        }
        final String ip = ipField.getText().trim();
        return ip.isEmpty() ? null : ip;
    }
}