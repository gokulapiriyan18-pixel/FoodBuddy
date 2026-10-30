package com.foodbuddy.ui;

import com.foodbuddy.DonationDAO;
import com.foodbuddy.Donor;
import com.foodbuddy.DonorDAO;
import com.foodbuddy.FoodDonation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class DonationsPanel extends JPanel {

    private final DonationDAO donationDAO = new DonationDAO();
    private final DonorDAO donorDAO = new DonorDAO();
    private final Runnable globalRefreshCallback;

    private JTable donationsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JLabel countLabel;

    public DonationsPanel(Runnable globalRefreshCallback) {
        this.globalRefreshCallback = globalRefreshCallback;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        initUI();
        loadDonationsAsync();
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

        searchField = UITheme.createTextField("Search food, location, donor...");
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadDonationsAsync();
            }
        });
        filterRow.add(searchField);

        statusFilterCombo = UITheme.createComboBox(new String[]{"All Statuses", "AVAILABLE", "CLAIMED"});
        statusFilterCombo.setPreferredSize(new Dimension(145, 38));
        statusFilterCombo.addActionListener(e -> loadDonationsAsync());
        filterRow.add(statusFilterCombo);

        countLabel = new JLabel("Loading records...");
        countLabel.setFont(UITheme.FONT_REGULAR);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        countLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        filterRow.add(countLabel);

        topBar.add(filterRow, BorderLayout.WEST);

        // Action Buttons (Right)
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("Add Donation", UITheme.VectorIcon.Type.PLUS);
        addBtn.setPreferredSize(new Dimension(150, 38));
        addBtn.addActionListener(e -> showAddDonationDialog());
        buttonRow.add(addBtn);

        JButton editBtn = UITheme.createCyanButton("Edit", UITheme.VectorIcon.Type.EDIT);
        editBtn.setPreferredSize(new Dimension(95, 38));
        editBtn.addActionListener(e -> showEditDonationDialog());
        buttonRow.add(editBtn);

        JButton deleteBtn = UITheme.createDangerButton("Delete", UITheme.VectorIcon.Type.DELETE);
        deleteBtn.setPreferredSize(new Dimension(105, 38));
        deleteBtn.addActionListener(e -> handleDeleteDonation());
        buttonRow.add(deleteBtn);

        JButton refreshBtn = UITheme.createSecondaryButton("", UITheme.VectorIcon.Type.REFRESH);
        refreshBtn.setPreferredSize(new Dimension(44, 38));
        refreshBtn.setToolTipText("Refresh Donations Table");
        refreshBtn.addActionListener(e -> refresh());
        buttonRow.add(refreshBtn);

        topBar.add(buttonRow, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        // 2. Table in Card Panel
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        String[] cols = {"Donation ID", "Food Item Name", "Quantity", "Pickup Location", "Donor ID", "Donor Name", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        donationsTable = new JTable(tableModel);
        UITheme.styleTable(donationsTable);

        // Center renderers for numeric and badge columns
        donationsTable.getColumnModel().getColumn(0).setCellRenderer(new UITheme.CenterTableCellRenderer());
        donationsTable.getColumnModel().getColumn(2).setCellRenderer(new UITheme.CenterTableCellRenderer());
        donationsTable.getColumnModel().getColumn(4).setCellRenderer(new UITheme.CenterTableCellRenderer());
        donationsTable.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        // Width allocations
        donationsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        donationsTable.getColumnModel().getColumn(0).setMaxWidth(95);
        donationsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        donationsTable.getColumnModel().getColumn(2).setPreferredWidth(85);
        donationsTable.getColumnModel().getColumn(2).setMaxWidth(100);
        donationsTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        donationsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        donationsTable.getColumnModel().getColumn(4).setMaxWidth(95);
        donationsTable.getColumnModel().getColumn(5).setPreferredWidth(170);
        donationsTable.getColumnModel().getColumn(6).setPreferredWidth(125);
        donationsTable.getColumnModel().getColumn(6).setMaxWidth(140);

        // Double click to view/edit
        donationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && donationsTable.getSelectedRow() != -1) {
                    showEditDonationDialog();
                }
            }
        });

        card.add(UITheme.createScrollPane(donationsTable), BorderLayout.CENTER);
        container.add(card, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
    }

    public void refresh() {
        loadDonationsAsync();
        if (globalRefreshCallback != null) {
            globalRefreshCallback.run();
        }
    }

    private void loadDonationsAsync() {
        String keyword = searchField.getText().trim();
        String selectedFilter = (String) statusFilterCombo.getSelectedItem();
        String statusFilter = "All Statuses".equals(selectedFilter) ? "ALL" : selectedFilter;

        SwingWorker<List<FoodDonation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FoodDonation> doInBackground() throws Exception {
                return donationDAO.searchDonations(keyword, statusFilter);
            }

            @Override
            protected void done() {
                try {
                    List<FoodDonation> list = get();
                    tableModel.setRowCount(0);
                    for (FoodDonation d : list) {
                        tableModel.addRow(new Object[]{
                                d.getDonationId(),
                                d.getFoodName(),
                                d.getQuantity(),
                                d.getLocation(),
                                d.getDonorId(),
                                d.getDonorName(),
                                d.getDonationStatus()
                        });
                    }
                    countLabel.setText("Showing " + list.size() + " donation(s)");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DonationsPanel.this,
                            "Failed to load donations: " + ex.getMessage(),
                            "Query Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Dialog: Add Donation
    private void showAddDonationDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Food Donation", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(490, 490);
        dialog.setLocationRelativeTo(this);

        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        headerRow.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.FOOD_BOWL, 22, UITheme.PRIMARY)));
        JLabel titleLbl = new JLabel("Register Food Donation");
        titleLbl.setFont(UITheme.FONT_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        headerRow.add(titleLbl);
        panel.add(headerRow);
        panel.add(Box.createVerticalStrut(18));

        JTextField foodField = UITheme.createTextField("e.g. Cooked Meals, Bread, Vegetables");
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        qtySpinner.setFont(UITheme.FONT_REGULAR);
        qtySpinner.setBackground(UITheme.INPUT_BG);

        JTextField locField = UITheme.createTextField("e.g. Main Kitchen, Avadi");

        // Load donors from MySQL
        DefaultComboBoxModel<Donor> donorModel = new DefaultComboBoxModel<>();
        try {
            List<Donor> donors = donorDAO.getAllDonors();
            for (Donor d : donors) {
                donorModel.addElement(d);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load donors list: " + ex.getMessage());
        }
        JComboBox<Donor> donorCombo = UITheme.createComboBox(donorModel);

        addFormField(panel, "Food Item Name *", foodField);
        addFormField(panel, "Quantity (Servings / Units) *", qtySpinner);
        addFormField(panel, "Pickup Location *", locField);
        addFormField(panel, "Select Donor *", donorCombo);

        panel.add(Box.createVerticalStrut(18));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(cancelBtn);

        JButton saveBtn = UITheme.createPrimaryButton("Save Donation", UITheme.VectorIcon.Type.CHECK);
        saveBtn.addActionListener(e -> {
            String food = foodField.getText().trim();
            int qty = (Integer) qtySpinner.getValue();
            String loc = locField.getText().trim();
            Donor selectedDonor = (Donor) donorCombo.getSelectedItem();

            if (food.isEmpty() || loc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selectedDonor == null) {
                JOptionPane.showMessageDialog(dialog, "Please select an existing donor or register one first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                FoodDonation fd = new FoodDonation(food, qty, loc, selectedDonor.getDonorId());
                boolean success = donationDAO.addDonationRecord(fd);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Food donation added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Could not add donation record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnRow.add(saveBtn);

        panel.add(btnRow);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Dialog: Edit Donation
    private void showEditDonationDialog() {
        int selectedRow = donationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a donation from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int donationId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentFood = (String) tableModel.getValueAt(selectedRow, 1);
        int currentQty = (Integer) tableModel.getValueAt(selectedRow, 2);
        String currentLoc = (String) tableModel.getValueAt(selectedRow, 3);
        int currentDonorId = (Integer) tableModel.getValueAt(selectedRow, 4);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Donation #" + donationId, true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(490, 520);
        dialog.setLocationRelativeTo(this);

        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        headerRow.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.EDIT, 20, UITheme.SECONDARY)));
        JLabel titleLbl = new JLabel("Edit Food Donation Details");
        titleLbl.setFont(UITheme.FONT_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        headerRow.add(titleLbl);
        panel.add(headerRow);
        panel.add(Box.createVerticalStrut(18));

        JTextField foodField = UITheme.createTextField("");
        foodField.setText(currentFood);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(currentQty, 1, 10000, 1));
        qtySpinner.setFont(UITheme.FONT_REGULAR);
        qtySpinner.setBackground(UITheme.INPUT_BG);

        JTextField locField = UITheme.createTextField("");
        locField.setText(currentLoc);

        DefaultComboBoxModel<Donor> donorModel = new DefaultComboBoxModel<>();
        Donor currentSelectedDonor = null;
        try {
            List<Donor> donors = donorDAO.getAllDonors();
            for (Donor d : donors) {
                donorModel.addElement(d);
                if (d.getDonorId() == currentDonorId) {
                    currentSelectedDonor = d;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load donors: " + ex.getMessage());
        }
        JComboBox<Donor> donorCombo = UITheme.createComboBox(donorModel);
        if (currentSelectedDonor != null) {
            donorCombo.setSelectedItem(currentSelectedDonor);
        }

        JComboBox<String> statusCombo = UITheme.createComboBox(new String[]{"AVAILABLE", "CLAIMED"});
        statusCombo.setSelectedItem(currentStatus);

        addFormField(panel, "Food Item Name *", foodField);
        addFormField(panel, "Quantity *", qtySpinner);
        addFormField(panel, "Pickup Location *", locField);
        addFormField(panel, "Donor *", donorCombo);
        addFormField(panel, "Availability Status", statusCombo);

        panel.add(Box.createVerticalStrut(18));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(cancelBtn);

        JButton updateBtn = UITheme.createPrimaryButton("Update Record", UITheme.VectorIcon.Type.CHECK);
        updateBtn.addActionListener(e -> {
            String food = foodField.getText().trim();
            int qty = (Integer) qtySpinner.getValue();
            String loc = locField.getText().trim();
            Donor donor = (Donor) donorCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();

            if (food.isEmpty() || loc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (donor == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a donor.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                FoodDonation updated = new FoodDonation(donationId, food, qty, loc, donor.getDonorId(), status);
                boolean ok = donationDAO.updateDonationRecord(updated);
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Donation updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Could not update donation.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnRow.add(updateBtn);

        panel.add(btnRow);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Action: Delete Donation
    private void handleDeleteDonation() {
        int selectedRow = donationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a donation to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int donationId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String foodName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete donation #" + donationId + " (" + foodName + ")?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = donationDAO.deleteDonationRecord(donationId);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Donation #" + donationId + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Donation ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete donation #" + donationId + " because it is referenced by existing food requests.\n" +
                        "Database message: " + ex.getMessage(),
                        "Foreign Key Constraint", JOptionPane.ERROR_MESSAGE);
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
