
package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodRequestDAO {

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

}
