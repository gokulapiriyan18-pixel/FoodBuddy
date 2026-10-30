package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {

    // ==========================================
    // Existing Console Application Methods
    // ==========================================

    // Add a new donor
    public void addDonor(Donor donor) {
        String sql = "INSERT INTO donors (donor_name, phone) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, donor.getDonorName());
            ps.setString(2, donor.getPhone());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Donor added successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding donor: " + e.getMessage());
        }
    }

    // View all donors
    public void viewDonors() {
        String sql = "SELECT * FROM donors";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- DONOR LIST ---");

            while (rs.next()) {
                System.out.println("Donor ID: " + rs.getInt("donor_id"));
                System.out.println("Name: " + rs.getString("donor_name"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("----------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing donors: " + e.getMessage());
        }
    }

    // ==========================================
    // Java Swing & Programmatic Access Methods
    // ==========================================

    // Fetch all donors as list
    public List<Donor> getAllDonors() throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT donor_id, donor_name, phone FROM donors ORDER BY donor_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Donor(
                    rs.getInt("donor_id"),
                    rs.getString("donor_name"),
                    rs.getString("phone")
                ));
            }
        }
        return list;
    }

    // Find donor by ID
    public Donor getDonorById(int donorId) throws SQLException {
        String sql = "SELECT donor_id, donor_name, phone FROM donors WHERE donor_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, donorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Donor(
                        rs.getInt("donor_id"),
                        rs.getString("donor_name"),
                        rs.getString("phone")
                    );
                }
            }
        }
        return null;
    }

    // Add donor with boolean return
    public boolean addDonorRecord(Donor donor) throws SQLException {
        String sql = "INSERT INTO donors (donor_name, phone) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, donor.getDonorName());
            ps.setString(2, donor.getPhone());

            return ps.executeUpdate() > 0;
        }
    }

    // Update donor record
    public boolean updateDonorRecord(Donor donor) throws SQLException {
        String sql = "UPDATE donors SET donor_name = ?, phone = ? WHERE donor_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, donor.getDonorName());
            ps.setString(2, donor.getPhone());
            ps.setInt(3, donor.getDonorId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete donor record
    public boolean deleteDonorRecord(int donorId) throws SQLException {
        // First check whether the donor has donations
        String checkSql = "SELECT COUNT(*) FROM donations WHERE donor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setInt(1, donorId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete this donor because they have " + rs.getInt(1) + " linked donation record(s).");
                }
            }
        }

        String sql = "DELETE FROM donors WHERE donor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, donorId);
            return ps.executeUpdate() > 0;
        }
    }

    // Search donors by name or phone
    public List<Donor> searchDonors(String keyword) throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT donor_id, donor_name, phone FROM donors " +
                     "WHERE LOWER(donor_name) LIKE ? OR phone LIKE ? " +
                     "ORDER BY donor_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String pattern = "%" + (keyword != null ? keyword.toLowerCase().trim() : "") + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Donor(
                        rs.getInt("donor_id"),
                        rs.getString("donor_name"),
                        rs.getString("phone")
                    ));
                }
            }
        }
        return list;
    }

    // Get count of donations contributed by this donor
    public int getDonationCountForDonor(int donorId) {
        String sql = "SELECT COUNT(*) FROM donations WHERE donor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donorId);
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
