package com.foodbuddy.ui;

import com.foodbuddy.Recipient;
import com.foodbuddy.RecipientDAO;

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

public class RecipientsPanel extends JPanel {

    private final RecipientDAO recipientDAO = new RecipientDAO();
    private final Runnable globalRefreshCallback;

    private JTable recipientsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel countLabel;

    public RecipientsPanel(Runnable globalRefreshCallback) {
        this.globalRefreshCallback = globalRefreshCallback;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        initUI();
        loadRecipientsAsync();
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

        searchField = UITheme.createTextField("Search recipient by name or phone...");
        searchField.setPreferredSize(new Dimension(300, 38));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadRecipientsAsync();
            }
        });
        searchRow.add(searchField);

        countLabel = new JLabel("Loading recipients...");
        countLabel.setFont(UITheme.FONT_REGULAR);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        countLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        searchRow.add(countLabel);

        topBar.add(searchRow, BorderLayout.WEST);

        // Action Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("Register Recipient", UITheme.VectorIcon.Type.PLUS);
        addBtn.setPreferredSize(new Dimension(170, 38));
        addBtn.addActionListener(e -> showAddRecipientDialog());
        buttonRow.add(addBtn);

        JButton editBtn = UITheme.createCyanButton("Edit", UITheme.VectorIcon.Type.EDIT);
        editBtn.setPreferredSize(new Dimension(95, 38));
        editBtn.addActionListener(e -> showEditRecipientDialog());
        buttonRow.add(editBtn);

        JButton deleteBtn = UITheme.createDangerButton("Delete", UITheme.VectorIcon.Type.DELETE);
        deleteBtn.setPreferredSize(new Dimension(105, 38));
        deleteBtn.addActionListener(e -> handleDeleteRecipient());
        buttonRow.add(deleteBtn);

        JButton refreshBtn = UITheme.createSecondaryButton("", UITheme.VectorIcon.Type.REFRESH);
        refreshBtn.setPreferredSize(new Dimension(44, 38));
        refreshBtn.setToolTipText("Refresh Recipients List");
        refreshBtn.addActionListener(e -> refresh());
        buttonRow.add(refreshBtn);

        topBar.add(buttonRow, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        // 2. Table in Card Panel
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        String[] cols = {"Recipient ID", "Recipient Full Name", "Phone Number", "Total Food Requests"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        recipientsTable = new JTable(tableModel);
        UITheme.styleTable(recipientsTable);
        recipientsTableRenderers();

        recipientsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && recipientsTable.getSelectedRow() != -1) {
                    showEditRecipientDialog();
                }
            }
        });

        card.add(UITheme.createScrollPane(recipientsTable), BorderLayout.CENTER);
        container.add(card, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
    }

    private void recipientsTableRenderers() {
        recipientsTable.getColumnModel().getColumn(0).setCellRenderer(new UITheme.CenterTableCellRenderer());
        recipientsTable.getColumnModel().getColumn(3).setCellRenderer(new UITheme.CenterTableCellRenderer());

        recipientsTable.getColumnModel().getColumn(0).setPreferredWidth(95);
        recipientsTable.getColumnModel().getColumn(0).setMaxWidth(110);
        recipientsTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        recipientsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        recipientsTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        recipientsTable.getColumnModel().getColumn(3).setMaxWidth(190);
    }

    public void refresh() {
        loadRecipientsAsync();
        if (globalRefreshCallback != null) {
            globalRefreshCallback.run();
        }
    }

    private void loadRecipientsAsync() {
        String keyword = searchField.getText().trim();

        SwingWorker<List<Recipient>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Recipient> doInBackground() throws Exception {
                return recipientDAO.searchRecipients(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<Recipient> list = get();
                    tableModel.setRowCount(0);
                    for (Recipient r : list) {
                        int reqCount = recipientDAO.getRequestCountForRecipient(r.getRecipientId());
                        tableModel.addRow(new Object[]{
                                r.getRecipientId(),
                                r.getRecipientName(),
                                r.getPhone(),
                                reqCount + " request(s)"
                        });
                    }
                    countLabel.setText("Showing " + list.size() + " recipient(s)");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RecipientsPanel.this,
                            "Failed to load recipients: " + ex.getMessage(),
                            "Query Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddRecipientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register New Recipient", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);

        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        headerRow.add(new JLabel(new UITheme.VectorIcon(UITheme.VectorIcon.Type.RECIPIENTS, 22, UITheme.PRIMARY)));
        JLabel titleLbl = new JLabel("Register Recipient Profile");
        titleLbl.setFont(UITheme.FONT_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        headerRow.add(titleLbl);
        panel.add(headerRow);
        panel.add(Box.createVerticalStrut(18));

        JTextField nameField = UITheme.createTextField("e.g. Alice Smith / Hope Community Shelter");
        JTextField phoneField = UITheme.createTextField("e.g. 9123456789");

        addFormField(panel, "Recipient / Organization Name *", nameField);
        addFormField(panel, "Contact Phone Number *", phoneField);

        panel.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(cancelBtn);

        JButton saveBtn = UITheme.createPrimaryButton("Save Recipient", UITheme.VectorIcon.Type.CHECK);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean ok = recipientDAO.addRecipientRecord(new Recipient(name, phone));
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Recipient registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Could not register recipient.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void showEditRecipientDialog() {
        int selectedRow = recipientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a recipient from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recipientId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPhone = (String) tableModel.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Recipient #" + recipientId, true);
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
        JLabel titleLbl = new JLabel("Edit Recipient Profile");
        titleLbl.setFont(UITheme.FONT_HEADING);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        headerRow.add(titleLbl);
        panel.add(headerRow);
        panel.add(Box.createVerticalStrut(18));

        JTextField nameField = UITheme.createTextField("");
        nameField.setText(currentName);

        JTextField phoneField = UITheme.createTextField("");
        phoneField.setText(currentPhone);

        addFormField(panel, "Recipient Name *", nameField);
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
                boolean ok = recipientDAO.updateRecipientRecord(new Recipient(recipientId, name, phone));
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Recipient updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Could not update recipient.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void handleDeleteRecipient() {
        int selectedRow = recipientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a recipient to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recipientId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String recipientName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete recipient #" + recipientId + " (" + recipientName + ")?\n" +
                "Note: Recipients with linked food requests cannot be deleted.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = recipientDAO.deleteRecipientRecord(recipientId);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Recipient #" + recipientId + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Recipient ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Cannot Delete Recipient", JOptionPane.WARNING_MESSAGE);
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
