package com.foodbuddy;

public class FoodDonation {
    private int donationId;
    private String foodName;
    private int quantity;
    private String location;
    private int donorId;
    private String donationStatus = "AVAILABLE";
    private String donorName = "";

    // Constructor for adding a donation
    public FoodDonation(String foodName, int quantity,
                        String location, int donorId) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.location = location;
        this.donorId = donorId;
    }

    // Constructor for updating an existing donation
    public FoodDonation(int donationId, String foodName, int quantity,
                        String location, int donorId) {
        this.donationId = donationId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.location = location;
        this.donorId = donorId;
    }

    // Constructor with status
    public FoodDonation(int donationId, String foodName, int quantity,
                        String location, int donorId, String donationStatus) {
        this.donationId = donationId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.location = location;
        this.donorId = donorId;
        this.donationStatus = donationStatus;
    }

    // Full constructor including donor name
    public FoodDonation(int donationId, String foodName, int quantity,
                        String location, int donorId, String donationStatus, String donorName) {
        this.donationId = donationId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.location = location;
        this.donorId = donorId;
        this.donationStatus = donationStatus;
        this.donorName = donorName;
    }

    public int getDonationId() {
        return donationId;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public int getDonorId() {
        return donorId;
    }

    public String getDonationStatus() {
        return donationStatus;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public void setDonationStatus(String donationStatus) {
        this.donationStatus = donationStatus;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    @Override
    public String toString() {
        return foodName + " (Qty: " + quantity + " at " + location + ")";
    }
}
