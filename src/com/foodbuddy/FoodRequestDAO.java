package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodRequestDAO {

    // ==========================================
    // Existing Console Application Methods
    // ==========================================

    // Submit a food request
    public void requestFood(int recipientId, int donationId) {
        String checkSql =
            "SELECT donation_id FROM donations WHERE donation_id = ?";

        String activeSql =
            "SELECT request_id FROM food_requests " +
            "WHERE donation_id = ? " +
            "AND request_status IN ('PENDING', 'APPROVED')";

        String insertSql =
            "INSERT INTO food_requests " +
            "(recipient_id, donation_id) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            // Check whether the donation exists
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, donationId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Donation not found!");
                        return;
                    }
                }
            }

            // Check whether the donation is already requested
            try (PreparedStatement ps = con.prepareStatement(activeSql)) {
                ps.setInt(1, donationId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(
                            "This donation already has an active request!"
                        );
                        return;
                    }
                }
            }

            // Create the request
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, recipientId);
                ps.setInt(2, donationId);

                ps.executeUpdate();
                System.out.println("Food request submitted successfully!");
                System.out.println("Request status: PENDING");
            }

        } catch (SQLException e) {
            System.out.println("Error submitting request: "
                               + e.getMessage());
        }
    }

    // View all food requests
    public void viewRequests() {
        String sql =
            "SELECT fr.request_id, r.recipient_name, " +
            "d.food_name, fr.request_status, fr.request_date " +
            "FROM food_requests fr " +
            "JOIN recipients r ON fr.recipient_id = r.recipient_id " +
            "JOIN donations d ON fr.donation_id = d.donation_id " +
            "ORDER BY fr.request_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- FOOD REQUESTS ---");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println("\nRequest ID: "
                                   + rs.getInt("request_id"));
                System.out.println("Recipient: "
                                   + rs.getString("recipient_name"));
                System.out.println("Food: "
                                   + rs.getString("food_name"));
                System.out.println("Status: "
                                   + rs.getString("request_status"));
                System.out.println("Requested on: "
                                   + rs.getTimestamp("request_date"));
                System.out.println("----------------------");
            }

            if (!found) {
                System.out.println("No food requests yet.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing requests: "
                               + e.getMessage());
        }
    }

    // Approve or reject a pending request
    public void updateRequestStatus(int requestId, String status) {

        if (!status.equalsIgnoreCase("APPROVED") &&
            !status.equalsIgnoreCase("REJECTED")) {
            System.out.println("Invalid status.");
            return;
        }

        status = status.toUpperCase();

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int donationId;

                // Find the pending request
                String findSql =
                    "SELECT donation_id FROM food_requests " +
                    "WHERE request_id = ? AND request_status = 'PENDING' " +
                    "FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(findSql)) {
                    ps.setInt(1, requestId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            System.out.println(
                                "Request not found or already processed."
                            );
                            return;
                        }

                        donationId = rs.getInt("donation_id");
                    }
                }

                // Check whether the food is still available
                String checkSql =
                    "SELECT donation_status FROM donations " +
                    "WHERE donation_id = ? FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                    ps.setInt(1, donationId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() ||
                            !rs.getString("donation_status")
                               .equals("AVAILABLE")) {
                            con.rollback();
                            System.out.println(
                                "This food is no longer available."
                            );
                            return;
                        }
                    }
                }

                // Update the request status
                String updateRequestSql =
                    "UPDATE food_requests SET request_status = ? " +
                    "WHERE request_id = ? AND request_status = 'PENDING'";

                try (PreparedStatement ps =
                         con.prepareStatement(updateRequestSql)) {
                    ps.setString(1, status);
                    ps.setInt(2, requestId);
                    ps.executeUpdate();
                }

                // Mark food as claimed only when approved
                if (status.equals("APPROVED")) {
                    String updateDonationSql =
                        "UPDATE donations SET donation_status = 'CLAIMED' " +
                        "WHERE donation_id = ?";

                    try (PreparedStatement ps =
                             con.prepareStatement(updateDonationSql)) {
                        ps.setInt(1, donationId);
                        ps.executeUpdate();
                    }
                }

                con.commit();
                System.out.println("Request " + status.toLowerCase() + " successfully.");

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error updating request: " + e.getMessage());

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // ==========================================
    // Java Swing & Programmatic Access Methods
    // ==========================================

    // Fetch all requests with recipient name and food name
    public List<FoodRequest> getAllRequests() throws SQLException {
        List<FoodRequest> list = new ArrayList<>();
        String sql =
            "SELECT fr.request_id, fr.recipient_id, r.recipient_name, " +
            "fr.donation_id, d.food_name, fr.request_status, fr.request_date " +
            "FROM food_requests fr " +
            "JOIN recipients r ON fr.recipient_id = r.recipient_id " +
            "JOIN donations d ON fr.donation_id = d.donation_id " +
            "ORDER BY fr.request_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new FoodRequest(
                    rs.getInt("request_id"),
                    rs.getInt("recipient_id"),
                    rs.getString("recipient_name"),
                    rs.getInt("donation_id"),
                    rs.getString("food_name"),
                    rs.getString("request_status"),
                    rs.getTimestamp("request_date")
                ));
            }
        }
        return list;
    }

    // Filter and search requests
    public List<FoodRequest> searchRequests(String keyword, String statusFilter) throws SQLException {
        List<FoodRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT fr.request_id, fr.recipient_id, r.recipient_name, " +
            "fr.donation_id, d.food_name, fr.request_status, fr.request_date " +
            "FROM food_requests fr " +
            "JOIN recipients r ON fr.recipient_id = r.recipient_id " +
            "JOIN donations d ON fr.donation_id = d.donation_id " +
            "WHERE 1=1 "
        );

        boolean hasStatusFilter = statusFilter != null && !statusFilter.equalsIgnoreCase("ALL") && !statusFilter.trim().isEmpty();
        if (hasStatusFilter) {
            sql.append("AND fr.request_status = ? ");
        }

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("AND (LOWER(r.recipient_name) LIKE ? OR LOWER(d.food_name) LIKE ?) ");
        }

        sql.append("ORDER BY fr.request_id DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasStatusFilter) {
                ps.setString(paramIndex++, statusFilter.toUpperCase().trim());
            }
            if (hasKeyword) {
                String pattern = "%" + keyword.toLowerCase().trim() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new FoodRequest(
                        rs.getInt("request_id"),
                        rs.getInt("recipient_id"),
                        rs.getString("recipient_name"),
                        rs.getInt("donation_id"),
                        rs.getString("food_name"),
                        rs.getString("request_status"),
                        rs.getTimestamp("request_date")
                    ));
                }
            }
        }
        return list;
    }

    // Submit a request programmatically with validation
    public void submitFoodRequest(int recipientId, int donationId) throws SQLException {
        String checkRecipient = "SELECT recipient_id FROM recipients WHERE recipient_id = ?";
        String checkDonation = "SELECT donation_id, donation_status FROM donations WHERE donation_id = ?";
        String checkActiveReq = "SELECT request_id FROM food_requests WHERE donation_id = ? AND request_status IN ('PENDING', 'APPROVED')";
        String insertSql = "INSERT INTO food_requests (recipient_id, donation_id, request_status) VALUES (?, ?, 'PENDING')";

        try (Connection con = DBConnection.getConnection()) {
            // Check recipient
            try (PreparedStatement ps = con.prepareStatement(checkRecipient)) {
                ps.setInt(1, recipientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Recipient ID " + recipientId + " not found!");
                    }
                }
            }

            // Check donation
            try (PreparedStatement ps = con.prepareStatement(checkDonation)) {
                ps.setInt(1, donationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Donation ID " + donationId + " not found!");
                    }
                    String status = rs.getString("donation_status");
                    if (!"AVAILABLE".equalsIgnoreCase(status)) {
                        throw new SQLException("This donation is already marked as " + status + ".");
                    }
                }
            }

            // Check active request
            try (PreparedStatement ps = con.prepareStatement(checkActiveReq)) {
                ps.setInt(1, donationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new SQLException("This donation already has an active request (Pending or Approved)!");
                    }
                }
            }

            // Insert request
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, recipientId);
                ps.setInt(2, donationId);
                ps.executeUpdate();
            }
        }
    }

    // Process request status change within an explicit JDBC transaction
    public void updateRequestStatusTransactional(int requestId, String targetStatus) throws SQLException {
        if (!"APPROVED".equalsIgnoreCase(targetStatus) && !"REJECTED".equalsIgnoreCase(targetStatus)) {
            throw new IllegalArgumentException("Invalid status: " + targetStatus + ". Must be APPROVED or REJECTED.");
        }

        String finalStatus = targetStatus.toUpperCase();

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int donationId;

                // 1. Lock the request row
                String findSql = "SELECT donation_id FROM food_requests WHERE request_id = ? AND request_status = 'PENDING' FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(findSql)) {
                    ps.setInt(1, requestId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            throw new SQLException("Request #" + requestId + " was not found or is no longer PENDING.");
                        }
                        donationId = rs.getInt("donation_id");
                    }
                }

                // 2. Lock the donation row and verify availability
                String checkSql = "SELECT donation_status FROM donations WHERE donation_id = ? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                    ps.setInt(1, donationId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            throw new SQLException("Referenced donation #" + donationId + " does not exist.");
                        }
                        String donStatus = rs.getString("donation_status");
                        if (!"AVAILABLE".equalsIgnoreCase(donStatus) && "APPROVED".equals(finalStatus)) {
                            con.rollback();
                            throw new SQLException("Donation #" + donationId + " is no longer AVAILABLE (current status: " + donStatus + "). Cannot approve request.");
                        }
                    }
                }

                // 3. Update request status
                String updateReqSql = "UPDATE food_requests SET request_status = ? WHERE request_id = ? AND request_status = 'PENDING'";
                try (PreparedStatement ps = con.prepareStatement(updateReqSql)) {
                    ps.setString(1, finalStatus);
                    ps.setInt(2, requestId);
                    int updated = ps.executeUpdate();
                    if (updated == 0) {
                        con.rollback();
                        throw new SQLException("Failed to update request #" + requestId + ".");
                    }
                }

                // 4. If approved, mark donation as CLAIMED
                if ("APPROVED".equals(finalStatus)) {
                    String updateDonSql = "UPDATE donations SET donation_status = 'CLAIMED' WHERE donation_id = ?";
                    try (PreparedStatement ps = con.prepareStatement(updateDonSql)) {
                        ps.setInt(1, donationId);
                        int donUpdated = ps.executeUpdate();
                        if (donUpdated == 0) {
                            con.rollback();
                            throw new SQLException("Failed to mark donation #" + donationId + " as CLAIMED.");
                        }
                    }
                }

                // Commit transaction
                con.commit();

            } catch (SQLException ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    // Suppressed
                }
                throw ex;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
