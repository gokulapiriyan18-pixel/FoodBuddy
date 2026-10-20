
package com.foodbuddy;

public class FoodDonation {
    private int donationId;
    private String foodName;
    private int quantity;
    private String location;
    private int donorId;

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
}
