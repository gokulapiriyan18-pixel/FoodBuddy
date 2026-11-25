package com.foodbuddy.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(UITheme.BG_DARK);
        scrollContent.setBorder(new EmptyBorder(24, 28, 28, 28));

        // 1. Hero Card
        UITheme.RoundedPanel heroCard = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        heroCard.setLayout(new BorderLayout());
        heroCard.setBorder(new EmptyBorder(24, 26, 24, 26));

        JPanel heroText = new JPanel();
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        heroText.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleRow.setOpaque(false);
        titleRow.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.FOOD_BOWL, 28, UITheme.PRIMARY)));

        JLabel logoTitle = new JLabel("FoodBuddy");
        logoTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoTitle.setForeground(UITheme.TEXT_MAIN);
        titleRow.add(logoTitle);
        heroText.add(titleRow);

        JLabel mission = new JLabel("“Reducing food waste. Connecting communities. Making surplus food meaningful.”");
        mission.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        mission.setForeground(UITheme.SECONDARY);
        mission.setBorder(new EmptyBorder(8, 0, 10, 0));
        heroText.add(mission);

        JLabel desc = new JLabel("<html>FoodBuddy is a full-featured desktop platform designed to bridge surplus food from community donors, restaurants, and individuals with local recipients, shelters, and distribution centers. By streamlining the food lifecycle—from donation registration to atomic request fulfillment—FoodBuddy ensures no good meal goes to waste while maintaining complete data integrity.</html>");
        desc.setFont(UITheme.FONT_REGULAR);
        desc.setForeground(UITheme.TEXT_MUTED);
        heroText.add(desc);

        heroCard.add(heroText, BorderLayout.CENTER);
        scrollContent.add(heroCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 2. Info Cards Grid (2 columns)
        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);

        // Tech Stack Card
        UITheme.RoundedPanel techCard = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        techCard.setLayout(new BoxLayout(techCard, BoxLayout.Y_AXIS));
        techCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel techHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        techHeader.setOpaque(false);
        techHeader.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.DASHBOARD, 18, UITheme.SECONDARY)));
        JLabel techTitle = new JLabel("Technology Stack");
        techTitle.setFont(UITheme.FONT_HEADING);
        techTitle.setForeground(UITheme.TEXT_MAIN);
        techHeader.add(techTitle);
        techCard.add(techHeader);
        techCard.add(Box.createVerticalStrut(14));

        addBulletPoint(techCard, "Frontend Interface:", "Java Swing (Dark Theme UI, Responsive Layouts)");
        addBulletPoint(techCard, "Programming Language:", "Java SE 21 (OOP, Strong Typing)");
        addBulletPoint(techCard, "Database Engine:", "MySQL 8.0 Relational DBMS");
        addBulletPoint(techCard, "Connectivity:", "JDBC with PreparedStatement & Connection Pooling");
        addBulletPoint(techCard, "Database Driver:", "Official MySQL Connector/J 8.0+");
        addBulletPoint(techCard, "Concurrency:", "SwingWorker for Non-blocking Async I/O");

        grid.add(techCard);

        // Architecture & Features Card
        UITheme.RoundedPanel featCard = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        featCard.setLayout(new BoxLayout(featCard, BoxLayout.Y_AXIS));
        featCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel featHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        featHeader.setOpaque(false);
        featHeader.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.STAR, 18, UITheme.PRIMARY)));
        JLabel featTitle = new JLabel("Architecture & Highlights");
        featTitle.setFont(UITheme.FONT_HEADING);
        featTitle.setForeground(UITheme.TEXT_MAIN);
        featHeader.add(featTitle);
        featCard.add(featHeader);
        featCard.add(Box.createVerticalStrut(14));

        addBulletPoint(featCard, "Data Access Layer:", "Clean DAO (Data Access Object) Pattern");
        addBulletPoint(featCard, "ACID Transactions:", "Atomicity during approval and row-level locking");
        addBulletPoint(featCard, "Zero-Framework Core:", "Pure Java standard libraries without bloated frameworks");
        addBulletPoint(featCard, "Dual Compatibility:", "Complete Swing GUI while preserving CLI terminal application");
        addBulletPoint(featCard, "Relational Integrity:", "Foreign key cascade protections between donors, food, and requests");

        grid.add(featCard);
        scrollContent.add(grid);
        scrollContent.add(Box.createVerticalStrut(18));

        // 3. Credits & Demonstration Card
        UITheme.RoundedPanel creditsCard = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        creditsCard.setLayout(new BorderLayout());
        creditsCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel creditsPanel = new JPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
        creditsPanel.setOpaque(false);

        JPanel credHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        credHeader.setOpaque(false);
        credHeader.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.ABOUT, 18, UITheme.PURPLE_ACCENT)));
        JLabel credTitle = new JLabel("Academic Project Demonstration");
        credTitle.setFont(UITheme.FONT_HEADING);
        credTitle.setForeground(UITheme.TEXT_MAIN);
        credHeader.add(credTitle);
        creditsPanel.add(credHeader);
        creditsPanel.add(Box.createVerticalStrut(10));

        JLabel credDesc = new JLabel("<html>Developed as a college project to demonstrate practical Java Object-Oriented Programming (OOP) concepts, Relational Database Management System (DBMS) integration, and production-grade Desktop UI development without external GUI dependencies.</html>");
        credDesc.setFont(UITheme.FONT_REGULAR);
        credDesc.setForeground(UITheme.TEXT_MUTED);
        creditsPanel.add(credDesc);

        creditsCard.add(creditsPanel, BorderLayout.CENTER);
        scrollContent.add(creditsCard);

        JScrollPane scrollPane = UITheme.createScrollPane(scrollContent);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addBulletPoint(JPanel panel, String boldKey, String value) {
        JLabel row = new JLabel("<html><font color='#38BDF8'><b>• " + boldKey + "</b></font> <font color='#F8FAFC'>" + value + "</font></html>");
        row.setFont(UITheme.FONT_REGULAR);
        row.setBorder(new EmptyBorder(3, 0, 3, 0));
        panel.add(row);
    }
}
