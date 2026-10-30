package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipientDAO {

    // ==========================================
    // Existing Console Application Methods
    // ==========================================

    // Register a new recipient
    public void addRecipient(Recipient recipient) {
        String sql = "INSERT INTO recipients " +
                     "(recipient_name, phone) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, recipient.getRecipientName());
            ps.setString(2, recipient.getPhone());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Recipient registered successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error registering recipient: "
                               + e.getMessage());
        }
    }

    // Display all recipients
    public void viewRecipients() {
        String sql = "SELECT * FROM recipients";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- RECIPIENT LIST ---");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println("Recipient ID: "
                                   + rs.getInt("recipient_id"));
                System.out.println("Name: "
                                   + rs.getString("recipient_name"));
                System.out.println("Phone: "
                                   + rs.getString("phone"));
                System.out.println("----------------------");
            }

            if (!found) {
                System.out.println("No recipients registered yet.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing recipients: "
                               + e.getMessage());
        }
    }

    // ==========================================
    // Java Swing & Programmatic Access Methods
    // ==========================================

    // Fetch all recipients as list
    public List<Recipient> getAllRecipients() throws SQLException {
        List<Recipient> list = new ArrayList<>();
        String sql = "SELECT recipient_id, recipient_name, phone FROM recipients ORDER BY recipient_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Recipient(
                    rs.getInt("recipient_id"),
                    rs.getString("recipient_name"),
                    rs.getString("phone")
                ));
            }
        }
        return list;
    }

    // Find recipient by ID
    public Recipient getRecipientById(int recipientId) throws SQLException {
        String sql = "SELECT recipient_id, recipient_name, phone FROM recipients WHERE recipient_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recipientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Recipient(
                        rs.getInt("recipient_id"),
                        rs.getString("recipient_name"),
                        rs.getString("phone")
                    );
                }
            }
        }
        return null;
    }

    // Register recipient with boolean return
    public boolean addRecipientRecord(Recipient recipient) throws SQLException {
        String sql = "INSERT INTO recipients (recipient_name, phone) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, recipient.getRecipientName());
            ps.setString(2, recipient.getPhone());

            return ps.executeUpdate() > 0;
        }
    }

    // Update recipient record
    public boolean updateRecipientRecord(Recipient recipient) throws SQLException {
        String sql = "UPDATE recipients SET recipient_name = ?, phone = ? WHERE recipient_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, recipient.getRecipientName());
            ps.setString(2, recipient.getPhone());
            ps.setInt(3, recipient.getRecipientId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete recipient record
    public boolean deleteRecipientRecord(int recipientId) throws SQLException {
        // Check if recipient has linked food requests
        String checkSql = "SELECT COUNT(*) FROM food_requests WHERE recipient_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setInt(1, recipientId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete this recipient because they have " + rs.getInt(1) + " linked food request(s).");
                }
            }
        }

        String sql = "DELETE FROM recipients WHERE recipient_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recipientId);
            return ps.executeUpdate() > 0;
        }
    }

    // Search recipients by name or phone
    public List<Recipient> searchRecipients(String keyword) throws SQLException {
        List<Recipient> list = new ArrayList<>();
        String sql = "SELECT recipient_id, recipient_name, phone FROM recipients " +
                     "WHERE LOWER(recipient_name) LIKE ? OR phone LIKE ? " +
                     "ORDER BY recipient_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String pattern = "%" + (keyword != null ? keyword.toLowerCase().trim() : "") + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Recipient(
                        rs.getInt("recipient_id"),
                        rs.getString("recipient_name"),
                        rs.getString("phone")
                    ));
                }
            }
        }
        return list;
    }

    // Count requests made by recipient
    public int getRequestCountForRecipient(int recipientId) {
        String sql = "SELECT COUNT(*) FROM food_requests WHERE recipient_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, recipientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }
}
