
package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DonorDAO {

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
}
