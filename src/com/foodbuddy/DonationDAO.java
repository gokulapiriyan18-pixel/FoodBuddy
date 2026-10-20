
package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DonationDAO {

    // 1. Add a food donation
    public void addDonation(FoodDonation fd) {
        String sql = "INSERT INTO donations " +
                     "(food_name, quantity, location, donor_id) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fd.getFoodName());
            ps.setInt(2, fd.getQuantity());
            ps.setString(3, fd.getLocation());
            ps.setInt(4, fd.getDonorId());

            ps.executeUpdate();
            System.out.println("Food donation added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding donation: " + e.getMessage());
        }
    }

    // 2. View all donations with donor names
    
    // View active donations with donor names
    public void viewDonations() {
        String sql = "SELECT d.donation_id, d.food_name, d.quantity, " +
                     "d.location, d.donor_id, dr.donor_name " +
                     "FROM donations d " +
                     "JOIN donors dr ON d.donor_id = dr.donor_id " +
                     "WHERE d.donation_status = 'AVAILABLE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- AVAILABLE DONATIONS ---");

            boolean found = false;

            while (rs.next()) {
                found = true;
                printDonation(rs);
            }

            if (!found) {
                System.out.println("No available donations.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing donations: " + e.getMessage());
        }
    }

    // 3. Search donation by ID
    public void searchDonation(int id) {
        String sql = "SELECT d.donation_id, d.food_name, d.quantity, " +
                     "d.location, d.donor_id, dr.donor_name " +
                     "FROM donations d JOIN donors dr " +
                     "ON d.donor_id = dr.donor_id " +
                     "WHERE d.donation_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    printDonation(rs);
                } else {
                    System.out.println("Donation not found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching donation: " + e.getMessage());
        }
    }

    // 4. Update donation
    public void updateDonation(FoodDonation fd) {
        String sql = "UPDATE donations SET food_name = ?, quantity = ?, " +
                     "location = ?, donor_id = ? WHERE donation_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fd.getFoodName());
            ps.setInt(2, fd.getQuantity());
            ps.setString(3, fd.getLocation());
            ps.setInt(4, fd.getDonorId());
            ps.setInt(5, fd.getDonationId());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Donation updated successfully!");
            } else {
                System.out.println("Donation ID not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error updating donation: " + e.getMessage());
        }
    }

    // 5. Delete donation
    public void deleteDonation(int id) {
        String sql = "DELETE FROM donations WHERE donation_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Donation deleted successfully!");
            } else {
                System.out.println("Donation ID not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting donation: " + e.getMessage());
        }
    }

    // Display one donation
    private void printDonation(ResultSet rs) throws SQLException {
        System.out.println("\nDonation ID: " + rs.getInt("donation_id"));
        System.out.println("Food: " + rs.getString("food_name"));
        System.out.println("Quantity: " + rs.getInt("quantity"));
        System.out.println("Location: " + rs.getString("location"));
        System.out.println("Donor ID: " + rs.getInt("donor_id"));
        System.out.println("Donor Name: " + rs.getString("donor_name"));
        System.out.println("----------------------");
    }
    
    // View available food for recipients
    public void viewAvailableFood() {
        String sql = "SELECT d.donation_id, d.food_name, " +
                     "d.quantity, d.location, dr.donor_name " +
                     "FROM donations d " +
                     "JOIN donors dr ON d.donor_id = dr.donor_id " +
                     "WHERE d.donation_status = 'AVAILABLE'\r\n" + //
                                                  "AND NOT EXISTS (" +
                     "SELECT 1 FROM food_requests fr " +
                     "WHERE fr.donation_id = d.donation_id " +
                     "AND fr.request_status IN ('PENDING', 'APPROVED'))";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- AVAILABLE FOOD ---");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println("\nDonation ID: "
                                   + rs.getInt("donation_id"));
                System.out.println("Food: "
                                   + rs.getString("food_name"));
                System.out.println("Quantity: "
                                   + rs.getInt("quantity"));
                System.out.println("Pickup location: "
                                   + rs.getString("location"));
                System.out.println("Donor: "
                                   + rs.getString("donor_name"));
                System.out.println("----------------------");
            }

            if (!found) {
                System.out.println("No available food donations.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing available food: "
                               + e.getMessage());
        }
    }

}
