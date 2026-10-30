package com.foodbuddy.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderPanel extends JPanel {

    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel clockLabel;
    private final JButton refreshButton;

    public HeaderPanel(Runnable onRefresh) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
        setPreferredSize(new Dimension(1000, 75));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR));

        // --- Left: Title & Subtitle ---
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(16, 28, 14, 10));

        titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);
        textPanel.add(titleLabel);

        subtitleLabel = new JLabel("Real-time overview of community food donations and requests");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        textPanel.add(subtitleLabel);

        add(textPanel, BorderLayout.WEST);

        // --- Right: Clock & Refresh Action ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 18));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(0, 0, 0, 26));

        // Live Clock with Vector Icon
        clockLabel = new JLabel();
        clockLabel.setIcon(new UITheme.VectorIcon(UITheme.VectorIcon.Type.CLOCK, 15, UITheme.TEXT_DIM));
        clockLabel.setIconTextGap(8);
        clockLabel.setFont(UITheme.FONT_REGULAR);
        clockLabel.setForeground(UITheme.TEXT_MUTED);
        clockLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        updateClock();
        actionPanel.add(clockLabel);

        // Timer to update clock every 10 seconds
        javax.swing.Timer timer = new javax.swing.Timer(10000, e -> updateClock());
        timer.start();

        // Refresh Button with Vector Icon
        refreshButton = UITheme.createSecondaryButton("Refresh Data", UITheme.VectorIcon.Type.REFRESH);
        refreshButton.addActionListener(e -> {
            if (onRefresh != null) {
                onRefresh.run();
            }
        });
        actionPanel.add(refreshButton);

        add(actionPanel, BorderLayout.EAST);
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy • HH:mm");
        clockLabel.setText(sdf.format(new Date()));
    }

    public void setHeaderInfo(String title, String subtitle) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle);
    }
}
