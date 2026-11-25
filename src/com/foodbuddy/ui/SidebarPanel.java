package com.foodbuddy.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class SidebarPanel extends JPanel {

    public interface NavCallback {
        void onNavSelected(String pageId);
    }

    private final NavCallback callback;
    private String activePageId = "DASHBOARD";
    private final Map<String, NavButton> buttons = new HashMap<>();

    public SidebarPanel(NavCallback callback) {
        this.callback = callback;
        setLayout(new BorderLayout());
        setBackground(UITheme.SIDEBAR_BG);
        setPreferredSize(new Dimension(250, 800));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR));

        initUI();
    }

    private void initUI() {
        // --- 1. Brand Header ---
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(26, 22, 26, 22));

        // Brand icon + text row
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoRow.setOpaque(false);

        // Custom drawn Logo Icon (Food Bowl with Steam)
        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background rounded rectangle
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Pure White Icon Drawing inside badge
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Bowl
                g2.drawArc(6, 12, 20, 13, 180, 180);
                g2.drawLine(6, 18, 26, 18);
                // Steam
                g2.drawLine(11, 7, 11, 10);
                g2.drawLine(16, 5, 16, 9);
                g2.drawLine(21, 7, 21, 10);

                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(34, 34));
        iconBadge.setOpaque(false);
        logoRow.add(iconBadge);

        JLabel titleLabel = new JLabel("FoodBuddy");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(UITheme.TEXT_MAIN);
        logoRow.add(titleLabel);

        brandPanel.add(logoRow);

        JLabel subtitle = new JLabel("FOOD DONATION NETWORK");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subtitle.setForeground(UITheme.SECONDARY);
        subtitle.setBorder(new EmptyBorder(4, 44, 0, 0));
        brandPanel.add(subtitle);

        add(brandPanel, BorderLayout.NORTH);

        // --- 2. Navigation Items ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel menuHeader = new JLabel("MENU");
        menuHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuHeader.setForeground(UITheme.TEXT_DIM);
        menuHeader.setBorder(new EmptyBorder(0, 14, 10, 0));
        menuPanel.add(menuHeader);

        addNavItem(menuPanel, "DASHBOARD", "Dashboard", UITheme.VectorIcon.Type.DASHBOARD);
        addNavItem(menuPanel, "DONATIONS", "Donations", UITheme.VectorIcon.Type.DONATIONS);
        addNavItem(menuPanel, "DONORS", "Donors", UITheme.VectorIcon.Type.DONORS);
        addNavItem(menuPanel, "RECIPIENTS", "Recipients", UITheme.VectorIcon.Type.RECIPIENTS);
        addNavItem(menuPanel, "REQUESTS", "Food Requests", UITheme.VectorIcon.Type.REQUESTS);

        menuPanel.add(Box.createVerticalStrut(22));

        JLabel sysHeader = new JLabel("SYSTEM");
        sysHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sysHeader.setForeground(UITheme.TEXT_DIM);
        sysHeader.setBorder(new EmptyBorder(0, 14, 10, 0));
        menuPanel.add(sysHeader);

        addNavItem(menuPanel, "ABOUT", "About FoodBuddy", UITheme.VectorIcon.Type.ABOUT);

        add(menuPanel, BorderLayout.CENTER);

        // --- 3. Footer / Database Status ---
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 16, 20, 16));

        // DB Status Card
        UITheme.RoundedPanel dbCard = new UITheme.RoundedPanel(10, new Color(0x11, 0x1B, 0x2A), UITheme.BORDER_COLOR);
        dbCard.setLayout(new BorderLayout());
        dbCard.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel statusTitle = new JLabel("DATABASE");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 9));
        statusTitle.setForeground(UITheme.TEXT_DIM);
        dbCard.add(statusTitle, BorderLayout.NORTH);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        statusRow.setOpaque(false);

        // Pulsing green vector circle
        JPanel dotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(0, 2, 8, 8);
                g2.dispose();
            }
        };
        dotPanel.setPreferredSize(new Dimension(8, 12));
        dotPanel.setOpaque(false);
        statusRow.add(dotPanel);

        JLabel dbName = new JLabel("MySQL 8.0: Active");
        dbName.setFont(UITheme.FONT_BOLD);
        dbName.setForeground(UITheme.TEXT_MAIN);
        statusRow.add(dbName);

        dbCard.add(statusRow, BorderLayout.CENTER);
        footer.add(dbCard);

        add(footer, BorderLayout.SOUTH);
    }

    private void addNavItem(JPanel container, String id, String title, UITheme.VectorIcon.Type iconType) {
        NavButton btn = new NavButton(id, title, iconType);
        buttons.put(id, btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(5));
    }

    public void setActivePage(String pageId) {
        this.activePageId = pageId;
        for (Map.Entry<String, NavButton> entry : buttons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(pageId));
        }
    }

    // Inner class for polished Nav Item with Vector Icons
    private class NavButton extends JPanel {
        private final String id;
        private final UITheme.VectorIcon.Type iconType;
        private boolean isActive = false;
        private boolean isHovered = false;
        private final JLabel iconLabel;
        private final JLabel textLabel;

        public NavButton(String id, String title, UITheme.VectorIcon.Type iconType) {
            this.id = id;
            this.iconType = iconType;
            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(222, 42));
            setMaximumSize(new Dimension(235, 42));

            // Content row with strictly centered icon and text
            JPanel contentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
            contentRow.setOpaque(false);
            contentRow.setBorder(new EmptyBorder(0, 10, 0, 10));

            iconLabel = new JLabel(new UITheme.VectorIcon(iconType, 18, UITheme.TEXT_DIM));
            contentRow.add(iconLabel);

            textLabel = new JLabel(title);
            textLabel.setFont(UITheme.FONT_BOLD);
            textLabel.setForeground(UITheme.TEXT_MUTED);
            contentRow.add(textLabel);

            add(contentRow, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    if (!isActive) {
                        textLabel.setForeground(new Color(0xE2, 0xE8, 0xF0));
                        iconLabel.setIcon(new UITheme.VectorIcon(iconType, 18, new Color(0x94, 0xA3, 0xB8)));
                    }
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    if (!isActive) {
                        textLabel.setForeground(UITheme.TEXT_MUTED);
                        iconLabel.setIcon(new UITheme.VectorIcon(iconType, 18, UITheme.TEXT_DIM));
                    }
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    setActivePage(id);
                    if (callback != null) {
                        callback.onNavSelected(id);
                    }
                }
            });

            if (id.equals(activePageId)) {
                setActive(true);
            }
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                textLabel.setForeground(Color.WHITE);
                iconLabel.setIcon(new UITheme.VectorIcon(iconType, 18, UITheme.PRIMARY));
            } else {
                textLabel.setForeground(UITheme.TEXT_MUTED);
                iconLabel.setIcon(new UITheme.VectorIcon(iconType, 18, UITheme.TEXT_DIM));
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                // Active highlight background
                g2.setColor(new Color(0x16, 0x24, 0x38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Emerald left accent indicator pill
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
            } else if (isHovered) {
                g2.setColor(new Color(0x10, 0x19, 0x27));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
