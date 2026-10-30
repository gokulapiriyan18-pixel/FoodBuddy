package com.foodbuddy.ui;

import com.foodbuddy.DonationDAO;
import com.foodbuddy.FoodDonation;
import com.foodbuddy.FoodRequest;
import com.foodbuddy.FoodRequestDAO;
import com.foodbuddy.Recipient;
import com.foodbuddy.RecipientDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

public class FoodRequestsPanel extends JPanel {

    private final FoodRequestDAO requestDAO = new FoodRequestDAO();
    private final RecipientDAO recipientDAO = new RecipientDAO();
    private final DonationDAO donationDAO = new DonationDAO();
    private final Runnable globalRefreshCallback;

    private JTable requestsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JLabel countLabel;

    public FoodRequestsPanel(Runnable globalRefreshCallback) {
        this.globalRefreshCallback = globalRefreshCallback;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        initUI();
        loadRequestsAsync();
    }

    private void initUI() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(UITheme.BG_DARK);
        container.setBorder(new EmptyBorder(22, 28, 24, 28));

        // 1. Top Control Bar (Search + Filter + Actions)
        JPanel topBar = new JPanel(new BorderLayout(14, 0));
        topBar.setOpaque(false);

        // Search & Filter (Left)
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        searchField = UITheme.createTextField("Search recipient or food item...");
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadRequestsAsync();
            }
        });
        filterRow.add(searchField);

        statusFilterCombo = UITheme.createComboBox(new String[]{"All Statuses", "PENDING", "APPROVED", "REJECTED"});
        statusFilterCombo.setPreferredSize(new Dimension(145, 38));
        statusFilterCombo.addActionListener(e -> loadRequestsAsync());
        filterRow.add(statusFilterCombo);

        countLabel = new JLabel("Loading requests...");
        countLabel.setFont(UITheme.FONT_REGULAR);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        countLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        filterRow.add(countLabel);

        topBar.add(filterRow, BorderLayout.WEST);

        // Action Buttons (Right)
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton submitReqBtn = UITheme.createPrimaryButton("New Request", UITheme.VectorIcon.Type.PLUS);
        submitReqBtn.setPreferredSize(new Dimension(150, 38));
        submitReqBtn.addActionListener(e -> showSubmitRequestDialog());
        buttonRow.add(submitReqBtn);

        JButton approveBtn = UITheme.createStyledButton("Approve", new Color(0x05, 0x96, 0x69), new Color(0x04, 0x78, 0x57), Color.WHITE, 10);
        approveBtn.setIcon(new UITheme.VectorIcon(UITheme.VectorIcon.Type.CHECK, 15, Color.WHITE));
        approveBtn.setIconTextGap(8);
        approveBtn.setPreferredSize(new Dimension(115, 38));
        approveBtn.addActionListener(e -> handleUpdateStatus("APPROVED"));
        buttonRow.add(approveBtn);

        JButton rejectBtn = UITheme.createDangerButton("Reject", UITheme.VectorIcon.Type.CROSS);
        rejectBtn.setPreferredSize(new Dimension(105, 38));
        rejectBtn.addActionListener(e -> handleUpdateStatus("REJECTED"));
        buttonRow.add(rejectBtn);

        JButton refreshBtn = UITheme.createSecondaryButton("", UITheme.VectorIcon.Type.REFRESH);
        refreshBtn.setPreferredSize(new Dimension(44, 38));
        refreshBtn.setToolTipText("Refresh Food Requests Table");
        refreshBtn.addActionListener(e -> refresh());
        buttonRow.add(refreshBtn);

        topBar.add(buttonRow, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        // 2. Table in Card Panel
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        String[] cols = {"Request ID", "Recipient ID", "Recipient Name", "Donation ID", "Requested Food", "Status", "Request Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        requestsTable = new JTable(tableModel);
        UITheme.styleTable(requestsTable);
        requestsTableRenderers();

        card.add(UITheme.createScrollPane(requestsTable), BorderLayout.CENTER);
        container.add(card, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
    }

    private void requestsTableRenderers() {
        requestsTable.getColumnModel().getColumn(0).setCellRenderer(new UITheme.CenterTableCellRenderer());
        requestsTable.getColumnModel().getColumn(1).setCellRenderer(new UITheme.CenterTableCellRenderer());
        requestsTable.getColumnModel().getColumn(3).setCellRenderer(new UITheme.CenterTableCellRenderer());
        requestsTable.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());
        requestsTable.getColumnModel().getColumn(6).setCellRenderer(new UITheme.CenterTableCellRenderer());

        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        requestsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        requestsTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        requestsTable.getColumnModel().getColumn(1).setMaxWidth(105);
        requestsTable.getColumnModel().getColumn(2).setPreferredWidth(170);
        requestsTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        requestsTable.getColumnModel().getColumn(3).setMaxWidth(105);
        requestsTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        requestsTable.getColumnModel().getColumn(5).setPreferredWidth(125);
        requestsTable.getColumnModel().getColumn(5).setMaxWidth(140);
        requestsTable.getColumnModel().getColumn(6).setPreferredWidth(170);
    }

    public void refresh() {
        loadRequestsAsync();
        if (globalRefreshCallback != null) {
            globalRefreshCallback.run();
        }
    }

    private void loadRequestsAsync() {
        String keyword = searchField.getText().trim();
        String selectedFilter = (String) statusFilterCombo.getSelectedItem();
        String statusFilter = "All Statuses".equals(selectedFilter) ? "ALL" : selectedFilter;

        SwingWorker<List<FoodRequest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FoodRequest> doInBackground() throws Exception {
                return requestDAO.searchRequests(keyword, statusFilter);
            }

            @Override
            protected void done() {
                try {
                    List<FoodRequest> list = get();
                    tableModel.setRowCount(0);
                    for (FoodRequest r : list) {
                        tableModel.addRow(new Object[]{
                                r.getRequestId(),
                                r.getRecipientId(),
                                r.getRecipientName(),
                                r.getDonationId(),
                                r.getFoodName(),
                                r.getRequestStatus(),
                                UITheme.formatDate(r.getRequestDate())
                        });
                    }
                    countLabel.setText("Showing " + list.size() + " request(s)");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FoodRequestsPanel.this,
                            "Failed to load requests: " + ex.getMessage(),
                            "Query Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Submit New Request Dialog
    private void showSubmitRequestDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Submit Food Request", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(490, 420);
        dialog.setLocationRelativeTo(this);

        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        headerRow.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.REQUESTS, 22, UITheme.PRIMARY)));
        JLabel titleLbl = new JLabel("Request Available Food Donation");
        titleLbl.setFont(UITheme.FONT_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        headerRow.add(titleLbl);
        panel.add(headerRow);
        panel.add(Box.createVerticalStrut(18));

        // Load recipients
        DefaultComboBoxModel<Recipient> recipientModel = new DefaultComboBoxModel<>();
        try {
            List<Recipient> recipients = recipientDAO.getAllRecipients();
            for (Recipient r : recipients) {
                recipientModel.addElement(r);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load recipients: " + ex.getMessage());
        }
        JComboBox<Recipient> recipientCombo = UITheme.createComboBox(recipientModel);

        // Load available donations
        DefaultComboBoxModel<FoodDonation> donationModel = new DefaultComboBoxModel<>();
        try {
            List<FoodDonation> availableList = donationDAO.getAvailableDonations();
            for (FoodDonation d : availableList) {
                donationModel.addElement(d);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load available donations: " + ex.getMessage());
        }
        JComboBox<FoodDonation> donationCombo = UITheme.createComboBox(donationModel);

        addFormField(panel, "Select Recipient *", recipientCombo);
        addFormField(panel, "Select Available Food Donation *", donationCombo);

        panel.add(Box.createVerticalStrut(18));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(cancelBtn);

        JButton submitBtn = UITheme.createPrimaryButton("Submit Request", UITheme.VectorIcon.Type.CHECK);
        submitBtn.addActionListener(e -> {
            Recipient selectedRec = (Recipient) recipientCombo.getSelectedItem();
            FoodDonation selectedDon = (FoodDonation) donationCombo.getSelectedItem();

            if (selectedRec == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a registered recipient first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selectedDon == null) {
                JOptionPane.showMessageDialog(dialog, "No available food donations selected.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                requestDAO.submitFoodRequest(selectedRec.getRecipientId(), selectedDon.getDonationId());
                JOptionPane.showMessageDialog(dialog, "Food request submitted successfully!\nInitial status: PENDING", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refresh();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Request Failed", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnRow.add(submitBtn);

        panel.add(btnRow);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Action: Approve or Reject Request
    private void handleUpdateStatus(String newStatus) {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
        String recipientName = (String) tableModel.getValueAt(selectedRow, 2);
        String foodName = (String) tableModel.getValueAt(selectedRow, 4);

        if (!"PENDING".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "Request #" + requestId + " is already " + currentStatus + ".\nOnly PENDING requests can be approved or rejected.",
                    "Action Prohibited", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String actionVerb = "APPROVED".equals(newStatus) ? "approve" : "reject";
        String extraNotice = "APPROVED".equals(newStatus)
                ? "\nApproving this request will automatically mark the food donation as CLAIMED."
                : "\nRejecting this request will keep the food donation as AVAILABLE.";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to " + actionVerb + " request #" + requestId + "?\n" +
                "Recipient: " + recipientName + "\n" +
                "Food: " + foodName + extraNotice,
                "Confirm Request " + newStatus,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                requestDAO.updateRequestStatusTransactional(requestId, newStatus);
                JOptionPane.showMessageDialog(this,
                        "Request #" + requestId + " " + actionVerb + "d successfully!",
                        "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to update request: " + ex.getMessage(),
                        "Transaction Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_BOLD);
        label.setForeground(UITheme.TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(2000, 36));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }
}
