package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public static class DashboardStats {
        public int totalDonors;
        public int totalDonations;
        public int availableDonations;
        public int registeredRecipients;
        public int pendingRequests;
        public int approvedRequests;
    }

    // Retrieve exact counts from MySQL
    public DashboardStats getStats() throws SQLException {
        DashboardStats stats = new DashboardStats();

        try (Connection con = DBConnection.getConnection()) {
            // 1. Total Donors
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM donors");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.totalDonors = rs.getInt(1);
            }

            // 2. Total Donations
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM donations");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.totalDonations = rs.getInt(1);
            }

            // 3. Available Donations
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM donations WHERE donation_status = 'AVAILABLE'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.availableDonations = rs.getInt(1);
            }

            // 4. Registered Recipients
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM recipients");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.registeredRecipients = rs.getInt(1);
            }

            // 5. Pending Requests
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM food_requests WHERE request_status = 'PENDING'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.pendingRequests = rs.getInt(1);
            }

            // 6. Approved Requests
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM food_requests WHERE request_status = 'APPROVED'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.approvedRequests = rs.getInt(1);
            }
        }

        return stats;
    }

    // Get latest donations for recent activity feed
    public List<FoodDonation> getRecentDonations(int limit) throws SQLException {
        List<FoodDonation> list = new ArrayList<>();
        String sql = "SELECT d.donation_id, d.food_name, d.quantity, " +
                     "d.location, d.donor_id, d.donation_status, dr.donor_name " +
                     "FROM donations d " +
                     "JOIN donors dr ON d.donor_id = dr.donor_id " +
                     "ORDER BY d.donation_id DESC LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new FoodDonation(
                        rs.getInt("donation_id"),
                        rs.getString("food_name"),
                        rs.getInt("quantity"),
                        rs.getString("location"),
                        rs.getInt("donor_id"),
                        rs.getString("donation_status"),
                        rs.getString("donor_name")
                    ));
                }
            }
        }
        return list;
    }

    // Get latest food requests for recent activity feed
    public List<FoodRequest> getRecentRequests(int limit) throws SQLException {
        List<FoodRequest> list = new ArrayList<>();
        String sql =
            "SELECT fr.request_id, fr.recipient_id, r.recipient_name, " +
            "fr.donation_id, d.food_name, fr.request_status, fr.request_date " +
            "FROM food_requests fr " +
            "JOIN recipients r ON fr.recipient_id = r.recipient_id " +
            "JOIN donations d ON fr.donation_id = d.donation_id " +
            "ORDER BY fr.request_id DESC LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
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
}

