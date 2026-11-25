package com.foodbuddy.ui;

import javax.swing.*;
import java.awt.*;

public class FoodBuddyApp extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);

    private SidebarPanel sidebarPanel;
    private HeaderPanel headerPanel;

    private DashboardPanel dashboardPanel;
    private DonationsPanel donationsPanel;
    private DonorsPanel donorsPanel;
    private RecipientsPanel recipientsPanel;
    private FoodRequestsPanel foodRequestsPanel;
    private AboutPanel aboutPanel;

    private String currentPageId = "DASHBOARD";

    public FoodBuddyApp() {
        super("FoodBuddy \u2014 Food Donation Management");
        initApp();
    }

    private void initApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1380, 850);
        setMinimumSize(new Dimension(1060, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);

        // Configure global Swing Look & Feel properties for clean dark theme
        try {
            UIManager.put("Panel.background", UITheme.BG_DARK);
            UIManager.put("OptionPane.background", UITheme.BG_DARK);
            UIManager.put("OptionPane.messageForeground", UITheme.TEXT_MAIN);
            UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
            UIManager.put("Table.focusSelectedCellHighlightBorder", BorderFactory.createEmptyBorder());
            UIManager.put("ScrollBar.thumb", new Color(0x2A, 0x3D, 0x56));
            UIManager.put("ScrollBar.track", UITheme.BG_DARK);
            UIManager.put("ScrollBar.width", 8);
        } catch (Exception ignored) {
        }

        setLayout(new BorderLayout());

        // 1. Sidebar Navigation
        sidebarPanel = new SidebarPanel(this::navigateTo);
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Right Container (Header + Content Cards)
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.BG_DARK);

        headerPanel = new HeaderPanel(this::refreshCurrentPage);
        mainArea.add(headerPanel, BorderLayout.NORTH);

        // 3. Central Card Panels
        contentCards.setBackground(UITheme.BG_DARK);

        dashboardPanel = new DashboardPanel(this::navigateTo);
        donationsPanel = new DonationsPanel(this::refreshDashboardOnly);
        donorsPanel = new DonorsPanel(this::refreshDashboardOnly);
        recipientsPanel = new RecipientsPanel(this::refreshDashboardOnly);
        foodRequestsPanel = new FoodRequestsPanel(this::refreshDashboardOnly);
        aboutPanel = new AboutPanel();

        contentCards.add(dashboardPanel, "DASHBOARD");
        contentCards.add(donationsPanel, "DONATIONS");
        contentCards.add(donorsPanel, "DONORS");
        contentCards.add(recipientsPanel, "RECIPIENTS");
        contentCards.add(foodRequestsPanel, "REQUESTS");
        contentCards.add(aboutPanel, "ABOUT");

        mainArea.add(contentCards, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Default route
        navigateTo("DASHBOARD");
    }

    public void navigateTo(String pageId) {
        this.currentPageId = pageId;
        cardLayout.show(contentCards, pageId);
        sidebarPanel.setActivePage(pageId);

        switch (pageId) {
            case "DASHBOARD":
                headerPanel.setHeaderInfo("Dashboard Overview", "Real-time statistics of community food donations and requests");
                dashboardPanel.refresh();
                break;
            case "DONATIONS":
                headerPanel.setHeaderInfo("Food Donations", "Manage surplus food items, quantities, and pickup locations");
                donationsPanel.refresh();
                break;
            case "DONORS":
                headerPanel.setHeaderInfo("Donor Directory", "Registered community donors, restaurants, and organizations");
                donorsPanel.refresh();
                break;
            case "RECIPIENTS":
                headerPanel.setHeaderInfo("Recipients Directory", "Registered community recipients, shelters, and distribution centers");
                recipientsPanel.refresh();
                break;
            case "REQUESTS":
                headerPanel.setHeaderInfo("Food Requests", "Review, approve, and track pending food distribution requests");
                foodRequestsPanel.refresh();
                break;
            case "ABOUT":
                headerPanel.setHeaderInfo("About FoodBuddy", "Project background, technology stack, and architecture");
                break;
        }
    }

    private void refreshCurrentPage() {
        switch (currentPageId) {
            case "DASHBOARD":
                dashboardPanel.refresh();
                break;
            case "DONATIONS":
                donationsPanel.refresh();
                break;
            case "DONORS":
                donorsPanel.refresh();
                break;
            case "RECIPIENTS":
                recipientsPanel.refresh();
                break;
            case "REQUESTS":
                foodRequestsPanel.refresh();
                break;
        }
    }

    private void refreshDashboardOnly() {
        dashboardPanel.refresh();
    }

    public static void main(String[] args) {
        // High quality font rendering flags
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            FoodBuddyApp app = new FoodBuddyApp();
            app.setVisible(true);
        });
    }
}
