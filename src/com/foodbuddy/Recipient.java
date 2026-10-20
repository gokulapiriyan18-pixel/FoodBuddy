
package com.foodbuddy;

public class Recipient {
    private int recipientId;
    private String recipientName;
    private String phone;

    // Constructor for registering a recipient
    public Recipient(String recipientName, String phone) {
        this.recipientName = recipientName;
        this.phone = phone;
    }

    // Constructor for an existing recipient
    public Recipient(int recipientId, String recipientName, String phone) {
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.phone = phone;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
