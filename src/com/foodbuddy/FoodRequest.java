package com.foodbuddy;

import java.sql.Timestamp;

public class FoodRequest {
    private int requestId;
    private int recipientId;
    private String recipientName;
    private int donationId;
    private String foodName;
    private String requestStatus;
    private Timestamp requestDate;

    public FoodRequest(int recipientId, int donationId) {
        this.recipientId = recipientId;
        this.donationId = donationId;
        this.requestStatus = "PENDING";
    }

    public FoodRequest(int requestId, int recipientId, String recipientName,
                       int donationId, String foodName,
                       String requestStatus, Timestamp requestDate) {
        this.requestId = requestId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.donationId = donationId;
        this.foodName = foodName;
        this.requestStatus = requestStatus;
        this.requestDate = requestDate;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public int getDonationId() {
        return donationId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public String toString() {
        return "Request #" + requestId + " [" + requestStatus + "] - " + recipientName + " for " + foodName;
    }
}

