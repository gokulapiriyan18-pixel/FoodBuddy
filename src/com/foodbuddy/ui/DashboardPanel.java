package com.foodbuddy.ui;

import com.foodbuddy.DashboardDAO;
import com.foodbuddy.FoodDonation;
import com.foodbuddy.FoodRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    public interface QuickNavCallback {
        void navigateTo(String pageId);
    }

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final QuickNavCallback navCallback;

    // Stat card labels
    private JLabel totalDonorsLabel;
    private JLabel totalDonationsLabel;
    private JLabel availableDonationsLabel;
    private JLabel registeredRecipientsLabel;
    private JLabel pendingRequestsLabel;
    private JLabel approvedRequestsLabel;

    // Recent activity tables
    private DefaultTableModel donationsTableModel;
    private DefaultTableModel requestsTableModel;
    private JTable donationsTable;
    private JTable requestsTable;

    public DashboardPanel(QuickNavCallback navCallback) {
        this.navCallback = navCallback;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        initUI();
        loadDashboardDataAsync();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(UITheme.BG_DARK);
        scrollContent.setBorder(new EmptyBorder(22, 28, 28, 28));

        // 1. Welcome Card
        scrollContent.add(createWelcomeCard());
        scrollContent.add(Box.createVerticalStrut(20));

        // 2. Metrics Grid (2 rows x 3 cards)
        scrollContent.add(createMetricsGrid());
        scrollContent.add(Box.createVerticalStrut(22));

        // 3. Recent Activity Section (Split Tables)
        scrollContent.add(createRecentActivitySection());

        JScrollPane scrollPane = UITheme.createScrollPane(scrollContent);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createWelcomeCard() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome to FoodBuddy");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeTitle.setForeground(UITheme.TEXT_MAIN);
        textPanel.add(welcomeTitle);

        JLabel welcomeSub = new JLabel("Connecting surplus food with people who need it.");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeSub.setForeground(UITheme.TEXT_MUTED);
        welcomeSub.setBorder(new EmptyBorder(4, 0, 0, 0));
        textPanel.add(welcomeSub);

        card.add(textPanel, BorderLayout.WEST);

        // Quick action buttons with Vector Icons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actions.setOpaque(false);

        JButton addDonationBtn = UITheme.createPrimaryButton("Donate Food", UITheme.VectorIcon.Type.PLUS);
        addDonationBtn.addActionListener(e -> {
            if (navCallback != null) navCallback.navigateTo("DONATIONS");
        });
        actions.add(addDonationBtn);

        JButton addReqBtn = UITheme.createCyanButton("Review Requests", UITheme.VectorIcon.Type.REQUESTS);
        addReqBtn.addActionListener(e -> {
            if (navCallback != null) navCallback.navigateTo("REQUESTS");
        });
        actions.add(addReqBtn);

        card.add(actions, BorderLayout.EAST);
        return card;
    }

    private JPanel createMetricsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(2200, 240));

        // Card 1: Total Donors
        totalDonorsLabel = new JLabel("—");
        grid.add(createStatCard("TOTAL DONORS", totalDonorsLabel, "Registered community donors", UITheme.SECONDARY, UITheme.VectorIcon.Type.USER));

        // Card 2: Total Donations
        totalDonationsLabel = new JLabel("—");
        grid.add(createStatCard("TOTAL DONATIONS", totalDonationsLabel, "All-time contributed food items", UITheme.PRIMARY, UITheme.VectorIcon.Type.FOOD_BOWL));

        // Card 3: Available Donations
        availableDonationsLabel = new JLabel("—");
        grid.add(createStatCard("AVAILABLE FOOD", availableDonationsLabel, "Ready for immediate pickup", new Color(0x34, 0xD3, 0x99), UITheme.VectorIcon.Type.CHECK));

        // Card 4: Registered Recipients
        registeredRecipientsLabel = new JLabel("—");
        grid.add(createStatCard("RECIPIENTS", registeredRecipientsLabel, "Verified community recipients", UITheme.PURPLE_ACCENT, UITheme.VectorIcon.Type.RECIPIENTS));

        // Card 5: Pending Requests
        pendingRequestsLabel = new JLabel("—");
        grid.add(createStatCard("PENDING REQUESTS", pendingRequestsLabel, "Awaiting approval / review", UITheme.AMBER, UITheme.VectorIcon.Type.CLOCK));

        // Card 6: Approved Requests
        approvedRequestsLabel = new JLabel("—");
        grid.add(createStatCard("APPROVED REQUESTS", approvedRequestsLabel, "Successfully matched & claimed", new Color(0x38, 0xBD, 0xF8), UITheme.VectorIcon.Type.STAR));

        return grid;
    }

    private JPanel createStatCard(String title, JLabel valLabel, String subtext, Color accent, UITheme.VectorIcon.Type iconType) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Header: Category Title + Vector Icon Badge
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_STAT_LABEL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        header.add(titleLbl, BorderLayout.WEST);

        // Rounded badge holding the vector icon
        JPanel iconBadge = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle 15% opacity accent background
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 36));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(32, 32));
        iconBadge.setOpaque(false);
        JLabel vectorIconLabel = new JLabel(new UITheme.VectorIcon(iconType, 16, accent));
        iconBadge.add(vectorIconLabel);
        header.add(iconBadge, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        // Center: Bold Value
        valLabel.setFont(UITheme.FONT_STAT_VAL);
        valLabel.setForeground(accent);
        valLabel.setBorder(new EmptyBorder(8, 0, 4, 0));
        card.add(valLabel, BorderLayout.CENTER);

        // Footer: Subtitle
        JLabel subLbl = new JLabel(subtext);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_DIM);
        card.add(subLbl, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRecentActivitySection() {
        JPanel container = new JPanel(new GridLayout(1, 2, 16, 0));
        container.setOpaque(false);

        // --- Left: Recent Donations ---
        UITheme.RoundedPanel donationsCard = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        donationsCard.setLayout(new BorderLayout());
        donationsCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel donHeader = new JPanel(new BorderLayout());
        donHeader.setOpaque(false);
        donHeader.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel donTitle = new JLabel("Recent Food Donations");
        donTitle.setFont(UITheme.FONT_HEADING);
        donTitle.setForeground(UITheme.TEXT_MAIN);
        donHeader.add(donTitle, BorderLayout.WEST);

        JButton viewAllDon = UITheme.createSecondaryButton("View All", UITheme.VectorIcon.Type.ARROW_RIGHT);
        viewAllDon.setFont(UITheme.FONT_SMALL);
        viewAllDon.setBorder(new EmptyBorder(4, 10, 4, 10));
        viewAllDon.addActionListener(e -> {
            if (navCallback != null) navCallback.navigateTo("DONATIONS");
        });
        donHeader.add(viewAllDon, BorderLayout.EAST);
        donationsCard.add(donHeader, BorderLayout.NORTH);

        // Donations Table
        String[] donCols = {"ID", "Food Item", "Qty", "Donor", "Status"};
        donationsTableModel = new DefaultTableModel(donCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        donationsTable = new JTable(donationsTableModel);
        UITheme.styleTable(donationsTable);
        donationsTable.getColumnModel().getColumn(0).setCellRenderer(new UITheme.CenterTableCellRenderer());
        donationsTable.getColumnModel().getColumn(2).setCellRenderer(new UITheme.CenterTableCellRenderer());
        donationsTable.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

        donationsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        donationsTable.getColumnModel().getColumn(0).setMaxWidth(65);
        donationsTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        donationsTable.getColumnModel().getColumn(2).setMaxWidth(75);
        donationsTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        donationsTable.getColumnModel().getColumn(4).setMaxWidth(130);

        JScrollPane donSp = UITheme.createScrollPane(donationsTable);
        donSp.setPreferredSize(new Dimension(450, 230));
        donationsCard.add(donSp, BorderLayout.CENTER);

        container.add(donationsCard);

        // --- Right: Recent Requests ---
        UITheme.RoundedPanel requestsCard = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        requestsCard.setLayout(new BorderLayout());
        requestsCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel reqHeader = new JPanel(new BorderLayout());
        reqHeader.setOpaque(false);
        reqHeader.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel reqTitle = new JLabel("Recent Food Requests");
        reqTitle.setFont(UITheme.FONT_HEADING);
        reqTitle.setForeground(UITheme.TEXT_MAIN);
        reqHeader.add(reqTitle, BorderLayout.WEST);

        JButton viewAllReq = UITheme.createSecondaryButton("View All", UITheme.VectorIcon.Type.ARROW_RIGHT);
        viewAllReq.setFont(UITheme.FONT_SMALL);
        viewAllReq.setBorder(new EmptyBorder(4, 10, 4, 10));
        viewAllReq.addActionListener(e -> {
            if (navCallback != null) navCallback.navigateTo("REQUESTS");
        });
        reqHeader.add(viewAllReq, BorderLayout.EAST);
        requestsCard.add(reqHeader, BorderLayout.NORTH);

        // Requests Table
        String[] reqCols = {"ID", "Recipient", "Food Item", "Status", "Date"};
        requestsTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        requestsTable = new JTable(requestsTableModel);
        UITheme.styleTable(requestsTable);
        requestsTable.getColumnModel().getColumn(0).setCellRenderer(new UITheme.CenterTableCellRenderer());
        requestsTable.getColumnModel().getColumn(3).setCellRenderer(new UITheme.StatusBadgeRenderer());
        requestsTable.getColumnModel().getColumn(4).setCellRenderer(new UITheme.CenterTableCellRenderer());

        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        requestsTable.getColumnModel().getColumn(0).setMaxWidth(65);
        requestsTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        requestsTable.getColumnModel().getColumn(3).setMaxWidth(130);
        requestsTable.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane reqSp = UITheme.createScrollPane(requestsTable);
        reqSp.setPreferredSize(new Dimension(450, 230));
        requestsCard.add(reqSp, BorderLayout.CENTER);

        container.add(requestsCard);

        return container;
    }

    public void refresh() {
        loadDashboardDataAsync();
    }

    private void loadDashboardDataAsync() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            DashboardDAO.DashboardStats stats;
            List<FoodDonation> recentDonations;
            List<FoodRequest> recentRequests;
            Exception error;

            @Override
            protected Void doInBackground() {
                try {
                    stats = dashboardDAO.getStats();
                    recentDonations = dashboardDAO.getRecentDonations(5);
                    recentRequests = dashboardDAO.getRecentRequests(5);
                } catch (Exception ex) {
                    error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    totalDonorsLabel.setText("Err");
                    totalDonationsLabel.setText("Err");
                    availableDonationsLabel.setText("Err");
                    registeredRecipientsLabel.setText("Err");
                    pendingRequestsLabel.setText("Err");
                    approvedRequestsLabel.setText("Err");
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                            "Failed to load dashboard metrics from database:\n" + error.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update stat cards with actual MySQL values
                totalDonorsLabel.setText(String.valueOf(stats.totalDonors));
                totalDonationsLabel.setText(String.valueOf(stats.totalDonations));
                availableDonationsLabel.setText(String.valueOf(stats.availableDonations));
                registeredRecipientsLabel.setText(String.valueOf(stats.registeredRecipients));
                pendingRequestsLabel.setText(String.valueOf(stats.pendingRequests));
                approvedRequestsLabel.setText(String.valueOf(stats.approvedRequests));

                // Populate recent donations table
                donationsTableModel.setRowCount(0);
                for (FoodDonation d : recentDonations) {
                    donationsTableModel.addRow(new Object[]{
                            d.getDonationId(),
                            d.getFoodName(),
                            d.getQuantity(),
                            d.getDonorName(),
                            d.getDonationStatus()
                    });
                }

                // Populate recent requests table with formatted dates
                requestsTableModel.setRowCount(0);
                for (FoodRequest r : recentRequests) {
                    requestsTableModel.addRow(new Object[]{
                            r.getRequestId(),
                            r.getRecipientName(),
                            r.getFoodName(),
                            r.getRequestStatus(),
                            UITheme.formatDate(r.getRequestDate())
                    });
                }
            }
        };
        worker.execute();
    }
}
