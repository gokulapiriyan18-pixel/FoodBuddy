package com.foodbuddy;

import com.foodbuddy.ui.FoodBuddyApp;

import java.awt.GraphicsEnvironment;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // If --cli flag is passed or the environment does not support GUI, run terminal console
        if ((args.length > 0 && (args[0].equalsIgnoreCase("--cli") || args[0].equalsIgnoreCase("-c")))
                || GraphicsEnvironment.isHeadless()) {
            runConsoleApp(args);
        } else {
            // Launch Modern Java Swing GUI by default
            FoodBuddyApp.main(args);
        }
    }

    public static void runConsoleApp(String[] args) {
        Scanner sc = new Scanner(System.in);

        DonorDAO donorDAO = new DonorDAO();
        DonationDAO donationDAO = new DonationDAO();
        RecipientDAO recipientDAO = new RecipientDAO();
        FoodRequestDAO requestDAO = new FoodRequestDAO();

        int choice;

        do {
            System.out.println("\n===== FOODBUDDY (Console Mode) =====");
            System.out.println("1. Add Donor");
            System.out.println("2. View All Donors");
            System.out.println("3. Add Food Donation");
            System.out.println("4. View All Donations");
            System.out.println("5. Search Donation");
            System.out.println("6. Update Donation");
            System.out.println("7. Delete Donation");
            System.out.println("8. Register Recipient");
            System.out.println("9. View All Recipients");
            System.out.println("10. View Available Food");
            System.out.println("11. Request Food");
            System.out.println("12. View All Requests");
            System.out.println("13. Approve/Reject Request");
            System.out.println("14. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                break;
            }
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1: {
                    System.out.print("Enter donor name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter phone number: ");
                    String phone = sc.nextLine();

                    donorDAO.addDonor(new Donor(name, phone));
                    break;
                }

                case 2:
                    donorDAO.viewDonors();
                    break;

                case 3: {
                    System.out.print("Enter food name: ");
                    String food = sc.nextLine();

                    System.out.print("Enter quantity: ");
                    int quantity = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter pickup location: ");
                    String location = sc.nextLine();

                    System.out.print("Enter donor ID: ");
                    int donorId = sc.nextInt();
                    sc.nextLine();

                    FoodDonation donation =
                        new FoodDonation(food, quantity, location, donorId);

                    donationDAO.addDonation(donation);
                    break;
                }

                case 4:
                    donationDAO.viewDonations();
                    break;

                case 5: {
                    System.out.print("Enter donation ID to search: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    donationDAO.searchDonation(id);
                    break;
                }

                case 6: {
                    System.out.print("Enter donation ID to update: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter new food name: ");
                    String food = sc.nextLine();

                    System.out.print("Enter new quantity: ");
                    int quantity = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter new pickup location: ");
                    String location = sc.nextLine();

                    System.out.print("Enter new donor ID: ");
                    int donorId = sc.nextInt();
                    sc.nextLine();

                    FoodDonation updated = new FoodDonation(
                        id, food, quantity, location, donorId
                    );

                    donationDAO.updateDonation(updated);
                    break;
                }

                case 7: {
                    System.out.print("Enter donation ID to delete: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    donationDAO.deleteDonation(id);
                    break;
                }

                case 8: {
                    System.out.print("Enter recipient name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter phone number: ");
                    String phone = sc.nextLine();

                    recipientDAO.addRecipient(new Recipient(name, phone));
                    break;
                }

                case 9:
                    recipientDAO.viewRecipients();
                    break;

                case 10:
                    donationDAO.viewAvailableFood();
                    break;

                case 11: {
                    System.out.print("Enter recipient ID: ");
                    int recipientId = sc.nextInt();

                    System.out.print("Enter donation ID: ");
                    int donationId = sc.nextInt();
                    sc.nextLine();

                    requestDAO.requestFood(recipientId, donationId);
                    break;
                }

                case 12:
                    requestDAO.viewRequests();
                    break;

                case 13: {
                    System.out.print("Enter request ID: ");
                    int requestId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter status (APPROVED/REJECTED): ");
                    String status = sc.nextLine().trim().toUpperCase();

                    requestDAO.updateRequestStatus(requestId, status);
                    break;
                }

                case 14:
                    System.out.println("Thank you for using FoodBuddy!");
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 14);

        sc.close();
    }
}
