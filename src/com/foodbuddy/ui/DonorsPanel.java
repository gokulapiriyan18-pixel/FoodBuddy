package com.foodbuddy.ui;

import com.foodbuddy.Donor;
import com.foodbuddy.DonorDAO;

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

public class DonorsPanel extends JPanel {

    private final DonorDAO donorDAO = new DonorDAO();
    private final Runnable globalRefreshCallback;

    private JTable donorsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel countLabel;

    public DonorsPanel(Runnable globalRefreshCallback) {
        this.globalRefreshCallback = globalRefreshCallback;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        initUI();
        loadDonorsAsync();
    }

    private void initUI() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(UITheme.BG_DARK);
        container.setBorder(new EmptyBorder(22, 28, 24, 28));

        // 1. Top Control Bar
        JPanel topBar = new JPanel(new BorderLayout(14, 0));
        topBar.setOpaque(false);

        // Search
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);

        searchField = UITheme.createTextField("Search donor by name or phone...");
        searchField.setPreferredSize(new Dimension(300, 38));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadDonorsAsync();
            }
        });
        searchRow.add(searchField);

        countLabel = new JLabel("Loading donors...");
        countLabel.setFont(UITheme.FONT_REGULAR);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        countLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        searchRow.add(countLabel);

        topBar.add(searchRow, BorderLayout.WEST);

        // Action Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("Register Donor", UITheme.VectorIcon.Type.PLUS);
        addBtn.setPreferredSize(new Dimension(155, 38));
        addBtn.addActionListener(e -> showAddDonorDialog());
        buttonRow.add(addBtn);

        JButton editBtn = UITheme.createCyanButton("Edit", UITheme.VectorIcon.Type.EDIT);
        editBtn.setPreferredSize(new Dimension(95, 38));
        editBtn.addActionListener(e -> showEditDonorDialog());
        buttonRow.add(editBtn);

        JButton deleteBtn = UITheme.createDangerButton("Delete", UITheme.VectorIcon.Type.DELETE);
        deleteBtn.setPreferredSize(new Dimension(105, 38));
        deleteBtn.addActionListener(e -> handleDeleteDonor());
        buttonRow.add(deleteBtn);

        JButton refreshBtn = UITheme.createSecondaryButton("", UITheme.VectorIcon.Type.REFRESH);
        refreshBtn.setPreferredSize(new Dimension(44, 38));
        refreshBtn.setToolTipText("Refresh Donors List");
        refreshBtn.addActionListener(e -> refresh());
        buttonRow.add(refreshBtn);

        topBar.add(buttonRow, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        // 2. Table in Card Panel
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        String[] cols = {"Donor ID", "Donor Full Name", "Phone Number", "Total Contributions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        donorsTable = new JTable(tableModel);
        UITheme.styleTable(donorsTable);
        donationsTableRenderers();

        donorsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && donorsTable.getSelectedRow() != -1) {
                    showEditDonorDialog();
                }
            }
        });

        card.add(UITheme.createScrollPane(donorsTable), BorderLayout.CENTER);
        container.add(card, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
    }

    private void donationsTableRenderers() {
        donorsTable.getColumnModel().getColumn(0).setCellRenderer(new UITheme.CenterTableCellRenderer());
        donorsTable.getColumnModel().getColumn(3).setCellRenderer(new UITheme.CenterTableCellRenderer());

        donorsTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        donorsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        donorsTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        donorsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        donorsTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        donorsTable.getColumnModel().getColumn(3).setMaxWidth(190);
    }

    public void refresh() {
        loadDonorsAsync();
        if (globalRefreshCallback != null) {
            globalRefreshCallback.run();
        }
    }

    private void loadDonorsAsync() {
        String keyword = searchField.getText().trim();

        SwingWorker<List<Donor>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Donor> doInBackground() throws Exception {
                return donorDAO.searchDonors(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<Donor> list = get();
                    tableModel.setRowCount(0);
                    for (Donor d : list) {
                        int donationCount = donorDAO.getDonationCountForDonor(d.getDonorId());
                        tableModel.addRow(new Object[]{
                                d.getDonorId(),
                                d.getDonorName(),
                                d.getPhone(),
                                donationCount + " donation(s)"
                        });
                    }
                    countLabel.setText("Showing " + list.size() + " donor(s)");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DonorsPanel.this,
                            "Failed to load donors: " + ex.getMessage(),
                            "Query Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddDonorDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register New Donor", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);

        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        headerRow.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.USER, 22, UITheme.PRIMARY)));
        JLabel titleLbl = new JLabel("Register Donor Profile");
        titleLbl.setFont(UITheme.FONT_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        headerRow.add(titleLbl);
        panel.add(headerRow);
        panel.add(Box.createVerticalStrut(18));

        JTextField nameField = UITheme.createTextField("e.g. John Doe / Fresh Foods Market");
        JTextField phoneField = UITheme.createTextField("e.g. 9876543210");

        addFormField(panel, "Donor / Organization Name *", nameField);
        addFormField(panel, "Contact Phone Number *", phoneField);

        panel.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(cancelBtn);

        JButton saveBtn = UITheme.createPrimaryButton("Save Donor", UITheme.VectorIcon.Type.CHECK);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean ok = donorDAO.addDonorRecord(new Donor(name, phone));
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Donor registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Could not register donor.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void showEditDonorDialog() {
        int selectedRow = donorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a donor from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int donorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPhone = (String) tableModel.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Donor #" + donorId, true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);

        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        headerRow.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.EDIT, 20, UITheme.SECONDARY)));
        JLabel titleLbl = new JLabel("Edit Donor Profile");
        titleLbl.setFont(UITheme.FONT_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        headerRow.add(titleLbl);
        panel.add(headerRow);
        panel.add(Box.createVerticalStrut(18));

        JTextField nameField = UITheme.createTextField("");
        nameField.setText(currentName);

        JTextField phoneField = UITheme.createTextField("");
        phoneField.setText(currentPhone);

        addFormField(panel, "Donor Name *", nameField);
        addFormField(panel, "Phone Number *", phoneField);

        panel.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(cancelBtn);

        JButton saveBtn = UITheme.createPrimaryButton("Update Profile", UITheme.VectorIcon.Type.CHECK);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean ok = donorDAO.updateDonorRecord(new Donor(donorId, name, phone));
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Donor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Could not update donor.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void handleDeleteDonor() {
        int selectedRow = donorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a donor to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int donorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String donorName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete donor #" + donorId + " (" + donorName + ")?\n" +
                "Note: Donors with linked food donations cannot be deleted.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = donorDAO.deleteDonorRecord(donorId);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Donor #" + donorId + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Donor ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Cannot Delete Donor", JOptionPane.WARNING_MESSAGE);
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
