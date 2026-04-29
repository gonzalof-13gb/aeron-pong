package com.weareadaptive.pong.client.visuals;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ReplayListPanel extends JPanel
{
    public record RecordingEntry(long recordingId, long startPosition, long stopPosition,
                                 String dateTime, double durationSeconds)
    {
    }

    private final JPanel listArea;

    public ReplayListPanel(final Runnable onBack)
    {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout(0, 16));

        final JLabel title = new JLabel("Replays", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        listArea = new JPanel();
        listArea.setBackground(Color.BLACK);
        listArea.setLayout(new BoxLayout(listArea, BoxLayout.Y_AXIS));

        final JScrollPane scroll = new JScrollPane(listArea);
        scroll.setBackground(Color.BLACK);
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        final JButton backButton = createButton("Back", 160, 45);
        backButton.addActionListener(e -> onBack.run());

        final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(Color.BLACK);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        bottom.add(backButton);
        add(bottom, BorderLayout.SOUTH);
    }

    public void showLoading()
    {
        SwingUtilities.invokeLater(() ->
        {
            listArea.removeAll();
            final JLabel label = centeredLabel("Loading recordings...", Color.GRAY, 22);
            listArea.add(Box.createVerticalStrut(40));
            listArea.add(label);
            listArea.revalidate();
            listArea.repaint();
        });
    }

    public void showEmpty()
    {
        SwingUtilities.invokeLater(() ->
        {
            listArea.removeAll();
            final JLabel label = centeredLabel("No recordings found", Color.GRAY, 22);
            listArea.add(Box.createVerticalStrut(40));
            listArea.add(label);
            listArea.revalidate();
            listArea.repaint();
        });
    }

    public void showRecordings(final List<RecordingEntry> entries, final Consumer<RecordingEntry> onSelect)
    {
        SwingUtilities.invokeLater(() ->
        {
            listArea.removeAll();
            for (final RecordingEntry entry : entries)
            {
                final String label = String.format("  #%d   %s   %.1fs  ",
                        entry.recordingId(), entry.dateTime(), entry.durationSeconds());
                final JButton btn = createButton(label, 600, 46);
                btn.addActionListener(e -> onSelect.accept(entry));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                listArea.add(Box.createVerticalStrut(8));
                listArea.add(btn);
            }
            listArea.revalidate();
            listArea.repaint();
        });
    }

    private JLabel centeredLabel(final String text, final Color color, final int size)
    {
        final JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("Arial", Font.PLAIN, size));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createButton(final String text, final int width, final int height)
    {
        final JButton btn = new JButton(text);
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 20));
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(width, height));
        btn.setPreferredSize(new Dimension(width, height));
        return btn;
    }
}