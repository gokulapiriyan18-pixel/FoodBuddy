
package com.foodbuddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecipientDAO {

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
}
